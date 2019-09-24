package com.acxiom.ams.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 14:13 3/23/2018
 */
@Configuration
@ConditionalOnProperty(prefix = "swagger",value = {"enable"},havingValue = "true")
@EnableSwagger2
public class SwaggerConfig {
    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Audience Curation")
            .description("")
            .termsOfServiceUrl("")
            .version("1.0")
            .build();
    }

    @Bean
    public Docket AcpApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("ACP-API")
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.acxiom.ams.controller"))
            .paths(PathSelectors.any()).build();
    }
}
