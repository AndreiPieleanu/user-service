package s6.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostCreatedEvent {
    private Integer id;
    private String text;
    private Boolean isBlocked;
}