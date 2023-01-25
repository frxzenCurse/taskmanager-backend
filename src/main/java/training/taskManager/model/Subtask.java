package training.taskManager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "subtasks")
@Data
@NoArgsConstructor
public class Subtask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Boolean complete = false;
    @ManyToOne
    @JoinColumn(name = "task_id")
    @JsonIgnore
    private Task task;

    public Subtask(String name, Task task) {
        this.name = name;
        this.task = task;
    }
}
