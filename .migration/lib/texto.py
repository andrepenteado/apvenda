"""
Normalização de texto para as migrações do sistema legado Clipper.

O Clipper gravava os textos TODO EM MAIÚSCULO, SEM ACENTOS/CEDILHA (limitação de
code page) e CHEIO DE ABREVIAÇÕES. Este módulo padroniza esses valores:

1. Remove o preenchimento de pontos (ex.: "AMP..............." -> "AMP").
2. Desfaz abreviações conhecidas (ex.: "P/" -> "para", "PARAF." -> "parafuso").
3. Restaura acentos e cedilha de palavras conhecidas (ex.: "ACO" -> "aço").
4. Padroniza a capitalização: só a 1ª letra maiúscula, mantendo letras sozinhas
   em maiúsculo (ex.: "TIPO U" -> "tipo U").

IMPORTANTE: os dicionários abaixo (ABREVIACOES e ACENTOS) foram montados a partir
dos termos mais frequentes do acervo de material elétrico/hidráulico. São a parte
que precisa de revisão humana e podem/devem ser estendidos conforme surgirem novos
termos nos dados atuais.
"""

from __future__ import annotations

import re

# Abreviação (em MAIÚSCULO, sem o ponto final) -> forma por extenso (minúscula).
ABREVIACOES = {
    "PARA": "para", "COM": "com", "SEM": "sem",  # resultantes de p/ c/ s/
    "PARAF": "parafuso",
    "ELETR": "elétrico",
    "TELEF": "telefone",
    "GALVANIZ": "galvanizado",
    "RESIST": "resistência",
    "PORC": "porcelana",
    "SOLD": "solda",
    "MAQ": "máquina",
    "DURAL": "duralumínio",
    "TERM": "terminal",
    "MAD": "madeira",
    "LAMP": "lâmpada",
    "FLUOR": "fluorescente",
    "IMPORT": "importado",
    "ABRAC": "abraçadeira",
    "PLAST": "plástico",
    "TOM": "tomada",
    "AMP": "ampère",
    "MAT": "material",
    "MOD": "modelo",
    "GAL": "galvanizado",
    # Revisadas a partir de tokens-nao-reconhecidos.txt:
    "ADAP": "adaptador",
    "ALUM": "alumínio",
    "TUBUL": "tubulação",
    "ZINC": "zincado",
    "COMPRES": "compressor",
    "PEND": "pendente",
    "ELET": "elétrico",
    "ISOL": "isolador",
    "GALV": "galvanizado",
    "CAMP": "campainha",
    "BIP": "bipolar",
    "CARREG": "carregador",
    "ANT": "antena",
    "TEL": "telefone",
    "INTER": "interruptor",
    "PUBL": "pública",
    "COD": "código",
    "RED": "redução",
    # 2ª rodada de revisão (tokens-nao-reconhecidos.txt):
    "EMERG": "emergência",
    "PINT": "pintura",
    "QUADR": "quadrado",
    "ROSC": "rosca",
    "SUP": "suporte",
    "TRIF": "trifásico",
    "ADPT": "adaptador",
    "ADP": "adaptador",
    "CAIX": "caixa",
    "DICR": "dicroica",
    "DISJ": "disjuntor",
    "REFL": "refletor",
    "TAMP": "tampa",
    "TRANSIST": "transistor",
    "TUB": "tubo",
    "UNIV": "universal",
    "VENT": "ventilador",
    "ALUMIN": "alumínio",
    "AMPERIM": "amperímetro",
    "AMPLIF": "amplificador",
    "CAB": "cabo",
    "CIGAR": "cigarra",
    "CIRC": "circular",
    "COMUNIC": "comunicação",
    "CONTR": "controle",
    "DIAM": "diâmetro",
    "MONOF": "monofásico",
    "MOT": "motor",
    "RETANG": "retangular",
    "TRIPOL": "tripolar",
    "UNIP": "unipolar",
    "LUMIN": "luminária",
    "MT": "metro",
    "SEC": "seccionadora",
    "SOBR": "sobrecarga",
    "OSC": "oscilante",
    "EXT": "externo",
    "MECAN": "mecânico",
    "GRD": "grande",
    "PQ": "pequeno",
    "FOTOEL": "fotoelétrico",
    "BRAQ": "braquete",
    "DEGR": "degrau",
    "COLOR": "colorido",
    "CONJ": "conjunto",
    "EL": "elétrico",
    "EMB": "embutir",
    "PASS": "passagem",
    "EXTENC": "extensão",
    "LUBRIF": "lubrificante",
    "ASPIR": "aspirador",
    "METAL": "metal",
    "TIJ": "tijolo",
}

# Palavra sem acento (MAIÚSCULO) -> palavra acentuada (minúscula).
ACENTOS = {
    "ACO": "aço",
    "BRACO": "braço", "BRACOS": "braços",
    "ABRACADEIRA": "abraçadeira", "ABRACADEIRAS": "abraçadeiras",
    "FLEXIVEL": "flexível",
    "HIDRAULICA": "hidráulica", "HIDRAULICO": "hidráulico",
    "PLASTICA": "plástica", "PLASTICO": "plástico",
    "LAMPADA": "lâmpada", "LAMPADAS": "lâmpadas",
    "FUSIVEL": "fusível",
    "LUMINARIA": "luminária",
    "PRESSAO": "pressão",
    "RELE": "relé",
    "RAPIDO": "rápido", "RAPIDA": "rápida",
    "RIGIDO": "rígido", "RIGIDA": "rígida",
    "DURALUMINIO": "duralumínio",
    "ELETRICO": "elétrico", "ELETRICA": "elétrica",
    "CONEXAO": "conexão", "CONEXOES": "conexões",
    "EXTENSAO": "extensão",
    "PROTECAO": "proteção",
    "FIXACAO": "fixação",
    "ILUMINACAO": "iluminação",
    "VALVULA": "válvula",
    "PORCELANA": "porcelana",
    # Revisadas a partir de tokens-nao-reconhecidos.txt (alta confiança):
    "EXTENSAO": "extensão", "EXTENCAO": "extensão",  # EXTENCAO = grafia errada no legado
    "ARMACAO": "armação",
    "CORDAO": "cordão",
    "MOVEL": "móvel",
    "PADRAO": "padrão",
    "LIGACAO": "ligação",
    "REDUCAO": "redução",
    "FORMAO": "formão",
    "ROSCAVEL": "roscável",
    "SOLDAVEL": "soldável",
    "TENSAO": "tensão",
    "CARTAO": "cartão",
    "DEMARCACAO": "demarcação",
    "FOGAO": "fogão",
    "FUNCOES": "funções",
    "ISOLACAO": "isolação",
    "JUNCAO": "junção",
    "LATAO": "latão",
    "NIVEL": "nível",
    "RECARREGAVEL": "recarregável",
    "REGULAVEL": "regulável",
    "REPOSICAO": "reposição",
    "SINALIZACAO": "sinalização",
    "VERSAO": "versão",
    "BOTJAO": "botijão",  # BOTJAO = grafia errada de BOTIJAO no legado
    "TRADICAO": "tradição",
    "BAINO": "baiano",  # BAINO = grafia errada de BAIANO no legado
}


def _expandir_token(token: str) -> str:
    """Expande/acentua um único token, se conhecido; senão devolve inalterado."""
    chave = token.rstrip(".").upper()

    if chave in ABREVIACOES:
        return ABREVIACOES[chave]
    if chave in ACENTOS:
        return ACENTOS[chave]

    # abreviações grudadas por ponto, ex.: "TELEF.ACO", "EL.-CABO", "ALUM.100W".
    # Só expande se ALGUMA parte for uma abreviação/palavra conhecida; assim os
    # números decimais ("06.0", "1.5MM") ficam intactos (suas partes não constam
    # nos dicionários) e códigos/modelos ("P.R.") também são preservados.
    if "." in token:
        partes = [p.strip("-") for p in token.split(".")]
        partes = [p for p in partes if p]
        chaves = [p.upper() for p in partes]
        if any(k in ABREVIACOES or k in ACENTOS for k in chaves):
            return " ".join(
                ABREVIACOES.get(k) or ACENTOS.get(k) or orig
                for k, orig in zip(chaves, partes)
            )

    # limpa ponto final solto de abreviação desconhecida (ex.: "BRUM." -> "BRUM")
    if token.endswith(".") and not any(c.isdigit() for c in token):
        return token.rstrip(".")

    return token


def normalizar_nome(texto: str) -> str:
    """
    Normaliza um nome vindo do Clipper: remove preenchimento, desfaz abreviações,
    restaura acentos/cedilha e ajusta a capitalização.

    Só atua quando o texto está TODO EM MAIÚSCULO (padrão do legado). Textos que já
    vierem em caixa mista são preservados — importante para quando os dados atuais
    já vierem tratados.
    """
    if not texto or any(c.islower() for c in texto):
        return texto

    # 1. remove preenchimento de pontos (2 ou mais) e normaliza espaços
    s = re.sub(r"\.{2,}", " ", texto)

    # 2. prefixos com barra: P/ -> para, C/ -> com, S/ -> sem (mesmo grudados)
    s = re.sub(r"\bP/", "PARA ", s)
    s = re.sub(r"\bC/", "COM ", s)
    s = re.sub(r"\bS/", "SEM ", s)

    # 3. expande/acentua token a token
    s = " ".join(_expandir_token(tok) for tok in s.split())

    # 4. capitalização: só a 1ª letra maiúscula, letras sozinhas em maiúsculo
    s = s[:1].upper() + s[1:].lower()
    return " ".join(
        palavra.upper() if len(palavra) == 1 and palavra.isalpha() else palavra
        for palavra in s.split(" ")
    )
