package net.troja.eve.pve.esi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;

import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.auth.OAuth;

public abstract class AbstractEsiService {
    public static final String DATASOURCE = "tranquility";

    @Autowired
    protected ResourceServerProperties resourceProperties;

    private OAuth auth;

    protected void init(final ApiClient apiClient) {
        auth = (OAuth) apiClient.getAuthentication("evesso");
        auth.setClientId(resourceProperties.getClientId());
        auth.setClientSecret(resourceProperties.getClientSecret());
    }

    protected void switchRefreshToken(final String token) {
        auth.setRefreshToken(token);
    }

    void setResourceProperties(final ResourceServerProperties resourceProperties) {
        this.resourceProperties = resourceProperties;
    }

    void setAuth(final OAuth auth) {
        this.auth = auth;
    }
}
