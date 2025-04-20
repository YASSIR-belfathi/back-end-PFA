package rag_system.demo.keycloak_config;

import java.util.Arrays;
import java.util.Collections;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;

@Service
public class KeycloakService {
    @Autowired
    KeycloakConfig keycloakConfig;

    private String realm = "Project_PFA";

    public void addUser(KeycloakDto keycloakDto) {
        CredentialRepresentation credentials = Credentials.credentialRepresentation(keycloakDto.getPassword());
        UserRepresentation user = new UserRepresentation();
        user.setUsername(keycloakDto.getUserName());
        user.setFirstName(keycloakDto.getFirstName());
        user.setLastName(keycloakDto.getLastName());
        user.setEmail(keycloakDto.getEmail());
        user.setEnabled(true);
        user.setCredentials(Collections.singletonList(credentials));

        Keycloak keycloak = keycloakConfig.getKeycloakInstance();
        RealmResource realmResource = keycloak.realm(realm);
        Response response = realmResource.users().create(user);
        String userId = CreatedResponseUtil.getCreatedId(response);
        UserResource instance = realmResource.users().get(userId);
        instance.resetPassword(credentials);

        ClientRepresentation clientKey = realmResource.clients().findByClientId("api-test").get(0);
        RoleRepresentation userRole = realmResource.clients().get(clientKey.getId()).roles().get("client_user")
                .toRepresentation();

        instance.roles().clientLevel(clientKey.getId()).add(Arrays.asList(userRole));
    }

    public void deleteUser(String userId) {
        Keycloak keycloak = keycloakConfig.getKeycloakInstance();
        RealmResource realmResource = keycloak.realm(realm);
        UserResource instance = realmResource.users().get(userId);
        instance.remove();
    }

    public void updateUser(KeycloakDto keycloakDto, String userId) {

        UserRepresentation user = new UserRepresentation();
        user.setFirstName(keycloakDto.getFirstName());
        user.setLastName(keycloakDto.getLastName());
        user.setEmail(keycloakDto.getEmail());
        user.setEnabled(true);

        if (keycloakDto.getPassword() != null && !keycloakDto.getPassword().isEmpty()
                && keycloakDto.getPassword().trim() != "") {
            CredentialRepresentation credentials = Credentials.credentialRepresentation(keycloakDto.getPassword());
            user.setCredentials(Collections.singletonList(credentials));
        }

        Keycloak keycloak = keycloakConfig.getKeycloakInstance();
        RealmResource realmResource = keycloak.realm(realm);
        UserResource instance = realmResource.users().get(userId);
        instance.update(user);
    }
}
