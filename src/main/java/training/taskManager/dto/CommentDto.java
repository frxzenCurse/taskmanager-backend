package training.taskManager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String body;
    private Long taskId;

    public CommentDto(String body, Long taskId) {
        this.body = body;
        this.taskId = taskId;
    }
}
