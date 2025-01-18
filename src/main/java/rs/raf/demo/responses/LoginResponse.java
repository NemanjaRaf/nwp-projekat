package rs.raf.demo.responses;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class LoginResponse {
    private String jwt;
    private String error;
    private Set<String> permissions;

    public LoginResponse(String jwt, Set<String> permissions) {
        this.jwt = jwt;
        this.permissions = new HashSet<>(permissions);
    }

    public LoginResponse(String jwt, String error) {
        this.jwt = jwt;
        this.error = error;
    }
}
