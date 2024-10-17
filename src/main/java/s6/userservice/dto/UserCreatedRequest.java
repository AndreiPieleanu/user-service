package s6.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import s6.userservice.datalayer.entities.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
    private String bio;
    private String location;
    private String website;
    // Any other relevant fields.
}
