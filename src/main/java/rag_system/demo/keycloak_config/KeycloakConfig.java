package rag_system.demo.keycloak_config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {
    Keycloak keycloak;

    private String serverUrl = "http://localhost:8081";
    private String realm = "Project_PFA";
    private String client_id = "api-test";
    private String grantType = "password";
    private String userName = "admin";
    private String password = "1234";

    public Keycloak getKeycloakInstance() {
        if (keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl).realm(realm)
                    .clientId(client_id).grantType(grantType)
                    .username(userName).password(password).build();
        }
        return keycloak;
    }
}
