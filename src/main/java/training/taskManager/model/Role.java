package training.taskManager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import training.taskManager.enumeration.Roles;

import javax.persistence.*;

@Entity
@Table(name = "roles_in_workspaces")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Roles role;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    @JsonIgnore
    private Workspace workspace;

    public Role(Roles role, User user, Workspace workspace) {
        this.role = role;
        this.user = user;
        this.workspace = workspace;
    }
}
