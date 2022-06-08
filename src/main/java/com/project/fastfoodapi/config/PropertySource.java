package com.project.fastfoodapi.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PropertySource {

    @Value("${app.name}")
    private String appName;

    @Value("${app.auth.apikey.key}")
    private String appAuthApiKey;

    @Value("${app.auth.token.type}")
    private String appAuthTokenType;

    @Value("${app.auth.secret}")
    private String appAuthSecret;

    @Value("${app.auth.token.expire}")
    private int expire;

    @Value("${app.auth.header.key}")
    private String appAuthHeaderKey;

    @Value("${app.auth.token.cookieName}")
    private String cookieName;
}
