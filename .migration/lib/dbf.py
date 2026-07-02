"""
Leitor genérico de arquivos .DBF (dBase III / FoxBase+) do sistema legado Clipper.

Uso pelo humano e pela IA
-------------------------
Este módulo NÃO tem dependências externas (só a biblioteca padrão do Python).
Ele foi escrito para ser reaproveitado na migração de qualquer tabela .DBF do
sistema Clipper (MAT_006, MAT_004, etc.), agora e no futuro com dados mais novos.

Formato suportado
-----------------
- dBase III / FoxBase+ sem campo memo (byte de versão 0x03). É o formato de
  TODOS os .DBF encontrados na pasta `estoque/` deste projeto.
- Tipos de campo: C (texto), N (numérico), D (data AAAAMMDD), L (lógico).
- Registros marcados como deletados (primeiro byte '*') são ignorados por
  padrão (parâmetro `incluir_deletados`).

Encoding
--------
Os dados foram gravados em DOS, então o padrão é CP850 (code page latina do
DOS-BR). Passe `encoding="cp850"` (padrão). Se algum dia um arquivo vier em
Windows-1252, troque para `cp1252`.

Exemplo
-------
    from dbf import DBF
    tabela = DBF("estoque/MAT_006.DBF", encoding="cp850")
    print(tabela.info())                 # metadados (versão, nº registros, campos)
    for reg in tabela.registros():       # cada registro é um dict {CAMPO: valor}
        print(reg["CODIGO"], reg["NOMEGRUPO"])
"""

from __future__ import annotations

import struct
from dataclasses import dataclass
from pathlib import Path
from typing import Iterator


@dataclass
class Campo:
    """Descrição de um campo (coluna) do .DBF."""
    nome: str
    tipo: str          # C, N, D, L, ...
    tamanho: int
    decimais: int
    offset: int        # posição do campo dentro do registro (byte 0 = flag de deleção)


class DBF:
    """Leitor de um arquivo .DBF dBase III / FoxBase+."""

    def __init__(self, caminho: str | Path, encoding: str = "cp850"):
        self.caminho = Path(caminho)
        self.encoding = encoding
        self._ler_cabecalho()

    def _ler_cabecalho(self) -> None:
        with self.caminho.open("rb") as f:
            hdr = f.read(32)
            if len(hdr) < 32:
                raise ValueError(f"{self.caminho}: arquivo pequeno demais para ser um DBF")

            self.versao = hdr[0]
            aa, mes, dia = hdr[1], hdr[2], hdr[3]
            # o byte do ano guarda 2 dígitos; pivô Y2K: <80 => 2000+aa, senão 1900+aa
            ano = 2000 + aa if aa < 80 else 1900 + aa
            self.atualizado_em = f"{ano:04d}-{mes:02d}-{dia:02d}"
            self.num_registros = struct.unpack("<I", hdr[4:8])[0]
            self.tam_cabecalho = struct.unpack("<H", hdr[8:10])[0]
            self.tam_registro = struct.unpack("<H", hdr[10:12])[0]

            # descritores de campo: 32 bytes cada, terminados por 0x0D
            self.campos: list[Campo] = []
            offset = 1  # byte 0 do registro é a flag de deleção
            f.seek(32)
            while True:
                fd = f.read(32)
                if not fd or fd[0] == 0x0D or len(fd) < 32:
                    break
                nome = fd[0:11].split(b"\x00")[0].decode("ascii", "replace")
                tipo = chr(fd[11])
                tamanho = fd[16]
                decimais = fd[17]
                self.campos.append(Campo(nome, tipo, tamanho, decimais, offset))
                offset += tamanho

    def info(self) -> str:
        linhas = [
            f"Arquivo........: {self.caminho.name}",
            f"Versão.........: 0x{self.versao:02x}",
            f"Atualizado em..: {self.atualizado_em}",
            f"Nº registros...: {self.num_registros}",
            f"Tam. cabeçalho.: {self.tam_cabecalho}",
            f"Tam. registro..: {self.tam_registro}",
            "Campos:",
        ]
        for c in self.campos:
            linhas.append(f"  {c.nome:12s} {c.tipo} len={c.tamanho:<3d} dec={c.decimais}")
        return "\n".join(linhas)

    def _converter(self, campo: Campo, bruto: bytes):
        """Converte os bytes de um campo para um valor Python."""
        texto = bruto.decode(self.encoding, "replace").strip()
        if campo.tipo == "C":
            return texto
        if campo.tipo == "N":
            if texto == "" or texto == ".":
                return None
            try:
                return int(texto) if campo.decimais == 0 else float(texto)
            except ValueError:
                return None
        if campo.tipo == "L":
            return texto.upper() in ("T", "Y", "S")  # verdadeiro
        if campo.tipo == "D":
            return texto or None  # AAAAMMDD como string; converta na camada de negócio
        return texto

    def registros(self, incluir_deletados: bool = False) -> Iterator[dict]:
        """Itera sobre os registros, devolvendo cada um como dict {CAMPO: valor}."""
        with self.caminho.open("rb") as f:
            f.seek(self.tam_cabecalho)
            for _ in range(self.num_registros):
                rec = f.read(self.tam_registro)
                if len(rec) < self.tam_registro:
                    break
                deletado = rec[0:1] == b"*"
                if deletado and not incluir_deletados:
                    continue
                linha = {"_deletado": deletado}
                for c in self.campos:
                    linha[c.nome] = self._converter(c, rec[c.offset:c.offset + c.tamanho])
                yield linha


if __name__ == "__main__":
    # Uso rápido: python dbf.py caminho/arquivo.DBF  -> imprime metadados + amostra
    import sys

    if len(sys.argv) < 2:
        print("uso: python dbf.py ARQUIVO.DBF [encoding]")
        raise SystemExit(1)
    enc = sys.argv[2] if len(sys.argv) > 2 else "cp850"
    tab = DBF(sys.argv[1], encoding=enc)
    print(tab.info())
    print("-" * 60)
    for i, reg in enumerate(tab.registros()):
        if i >= 10:
            break
        print({k: v for k, v in reg.items() if k != "_deletado"})
