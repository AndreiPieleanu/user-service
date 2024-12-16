package s6.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import s6.userservice.datalayer.entities.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdatedEvent {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
}
