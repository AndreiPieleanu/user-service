package s6.userservice.requestresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import s6.userservice.datalayer.entities.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindUserResponse {
    private User foundUser;
}
