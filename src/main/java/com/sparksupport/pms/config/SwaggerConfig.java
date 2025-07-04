package com.sparksupport.pms.config;

import io.swagger.v3.oas.models.info.Info;

import java.util.Arrays;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi userApiGroup() {
        return GroupedOpenApi.builder()
                .group("user-api")
                .pathsToMatch("/api/**")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("PMS User API")
                        .description("Accessible by normal users")
                        .version("1.0")))
                .addOperationCustomizer((operation, handlerMethod) -> {
                    if (handlerMethod.hasMethodAnnotation(DeleteMapping.class) ||
                            handlerMethod.hasMethodAnnotation(PutMapping.class) ||
                            handlerMethod.hasMethodAnnotation(PatchMapping.class)) {
                        return null;
                    }

                    RequestMapping rm = handlerMethod.getMethodAnnotation(RequestMapping.class);
                    if (rm != null && Arrays.stream(rm.method()).anyMatch(method -> method == RequestMethod.DELETE ||
                            method == RequestMethod.PUT ||
                            method == RequestMethod.PATCH)) {
                        return null;
                    }

                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi adminApiGroup() {
        return GroupedOpenApi.builder()
                .group("admin-api")
                .pathsToMatch("/api/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.info(new Info()
                            .title("PMS Admin API")
                            .description("Product Management System - Full API access for administrators")
                            .version("1.0"));
                })
                .build();
    }

}
