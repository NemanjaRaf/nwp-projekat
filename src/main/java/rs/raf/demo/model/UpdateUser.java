package rs.raf.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Getter
@Setter
public class UpdateUser {
    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "Email is mandatory")
    private String email;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Transient
    private String fullName;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> permissions;
}
