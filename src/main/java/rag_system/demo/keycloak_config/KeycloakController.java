package rag_system.demo.keycloak_config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class KeycloakController {

    @Autowired
    KeycloakService keycloakService;

    @GetMapping("userInfo")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getClaims();
    }

    // private String realm = "Poject_PFA";
    @PostMapping("createUser")
    public String addUser(@RequestBody KeycloakDto keycloakDto) {
        keycloakService.addUser(keycloakDto);
        return "user created";
    }

    @DeleteMapping("/delete/{Id}")
    @PreAuthorize("hasRole('client_user')")
    public String deleteUser(@PathVariable("Id") String userId) {
        try {
            keycloakService.deleteUser(userId);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "user deleted";
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasRole('client_user')")
    public String updateUser(@PathVariable("userId") String userId, @RequestBody KeycloakDto keycloakDto) {
        try {
            keycloakService.updateUser(keycloakDto, userId);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "user updated";
    }
}
