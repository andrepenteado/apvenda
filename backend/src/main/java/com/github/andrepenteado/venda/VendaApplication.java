package com.github.andrepenteado.venda;

import br.unesp.fc.andrepenteado.core.upload.Upload;
import br.unesp.fc.andrepenteado.core.upload.UploadRepository;
import br.unesp.fc.andrepenteado.core.upload.UploadResource;
import br.unesp.fc.andrepenteado.core.web.config.CorsConfig;
import br.unesp.fc.andrepenteado.core.web.config.SecurityConfig;
import br.unesp.fc.andrepenteado.core.web.exceptions.DatabaseExceptionHandler;
import br.unesp.fc.andrepenteado.core.web.exceptions.DefaultExceptionHandler;
import br.unesp.fc.andrepenteado.core.web.resources.AuthResource;
import br.unesp.fc.andrepenteado.core.web.services.SecurityService;
import br.unesp.fc.andrepenteado.core.web.services.UserLoginOAuth2Service;
import br.unesp.fc.andrepenteado.core.web.services.UserLoginOidcService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    scanBasePackages = {
        "com.github.andrepenteado.venda"
    },
    scanBasePackageClasses = {
        SecurityConfig.class,
        AuthResource.class,
        UserLoginOAuth2Service.class,
        UserLoginOidcService.class,
        CorsConfig.class,
        UploadResource.class,
        DefaultExceptionHandler.class,
        DatabaseExceptionHandler.class,
        SecurityService.class
    }
)
@EntityScan(
    basePackages = {
        "com.github.andrepenteado.venda"
    },
    basePackageClasses = {
        Upload.class
    }
)
@EnableJpaRepositories(
    basePackages = {
        "com.github.andrepenteado.venda"
    },
    basePackageClasses = {
        UploadRepository.class
    }
)
public class VendaApplication {

    public static final String PERFIL_CAIXA = "ROLE_com.github.andrepenteado.venda_CAIXA";

    static void main(String[] args) {
        SpringApplication.run(VendaApplication.class, args);
    }

}
