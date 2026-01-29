package com.lived.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI swagger() {
        Info info = new Info()
                .title("살아보니 API 명세서")
                .description("자취생 루틴 관리 서비스 '살아보니' 백엔드 API")
                .version("1.0.0");

        String securityScheme = "JWT TOKEN";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityScheme);

        Components components = new Components()
                .addSecuritySchemes(securityScheme, new SecurityScheme()
                        .name(securityScheme)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("Bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .info(info)
                .addServersItem(new Server().url("/"))
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    @Bean
    public OpenApiCustomizer socialLoginPathCustomizer() {
        return openApi -> {
            openApi.getPaths().addPathItem("/api/auth/login/{provider}", new PathItem()
                    .get(new Operation()
                            .tags(List.of("인증/로그인"))
                            .summary("소셜 로그인 및 가입 확인")
                            .description("사용자를 소셜 로그인 페이지로 리다이렉트합니다.")
                            .addParametersItem(new Parameter()
                                    .name("provider")
                                    .in("path")
                                    .required(true)
                                    .description("소셜 서비스 제공자 (google, kakao)")
                                    .schema(new StringSchema()._enum(List.of("google", "kakao"))))
                            .responses(new ApiResponses()
                                    .addApiResponse("302", new ApiResponse().description("리다이렉트 성공")))));
        };
    }
}