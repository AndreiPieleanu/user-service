package s6.userservice.requestresponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateRoleResponse {
    private Integer updatedId;
}
