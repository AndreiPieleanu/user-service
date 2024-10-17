package s6.userservice.requestresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import s6.userservice.datalayer.entities.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private int userId;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Role role;
    private String bio;
    private String location;
    private String website;
}
