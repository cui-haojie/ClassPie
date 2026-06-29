package org.example.classpiserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "classpi.jwt")
public class JwtProperties {
    private String secret = "classpi-dev-secret-change-in-production-min-32-chars!!";
    private int expireHours = 72;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getExpireHours() {
        return expireHours;
    }

    public void setExpireHours(int expireHours) {
        this.expireHours = expireHours;
    }
}
