package s6.userservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreatedEvent {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    // Any other relevant fields.
}