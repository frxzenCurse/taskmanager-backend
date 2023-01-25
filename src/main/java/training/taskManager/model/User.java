package training.taskManager.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "users")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column
    private String avatar;
    @ManyToMany(mappedBy = "members")
    private List<Workspace> workspaces = new ArrayList<>(); // ?

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Role> roles = new ArrayList<>();

    public User(String name, String email, String avatar) {
        this.name = name;
        this.email = email;
        this.avatar = avatar;
    }

    public void addWorkspace(Workspace workspace) {
        workspaces.add(workspace);
    }
}
