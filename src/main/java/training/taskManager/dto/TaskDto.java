package training.taskManager.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import training.taskManager.enumeration.Status;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    private String name;
    private Status status;
    private String expectedTime;
    private String description;
    private List<String> subtasks = new ArrayList<>();
    private Long workspaceId;
    private List<MultipartFile> files = new ArrayList<>();
}


