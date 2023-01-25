package training.taskManager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import training.taskManager.enumeration.Status;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDate expectedTime;

    private String description;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subtask> subtasks = new ArrayList<>();
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    @ManyToOne
    @JoinColumn(name = "workspace_id")
    @JsonIgnore
    private Workspace workspace;

    @OneToMany(mappedBy = "task")
    private List<FilesInfo> files;

    public Task(String name,
                Status status,
                LocalDate expectedTime,
                String description,
                User user,
                Workspace workspace) {
        this.name = name;
        this.status = status;
        this.expectedTime = expectedTime;
        this.description = description;
        this.user = user;
        this.workspace = workspace;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }
}
