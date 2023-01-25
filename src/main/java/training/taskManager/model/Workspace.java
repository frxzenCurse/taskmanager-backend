package training.taskManager.model;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "workspaces")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name = "My workspace";
    @ManyToMany
    @JoinTable(
            name = "user_workspace",
            joinColumns = @JoinColumn(name = "workspace_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private List<User> members = new ArrayList<>();
    @OneToMany(
            mappedBy = "workspace",
            cascade = CascadeType.ALL
    )
    private List<Task> tasks;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Role> roles;

    public void addMember(User user) {
        members.add(user);
    }
}
