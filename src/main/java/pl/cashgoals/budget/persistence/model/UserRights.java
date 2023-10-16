package pl.cashgoals.budget.persistence.model;

import jakarta.persistence.*;
import lombok.*;
import pl.cashgoals.user.persistence.model.User;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserRights {
    @Id
    @ManyToOne
    private User user;
    @ElementCollection(targetClass = Right.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "user_right",
            joinColumns = {@JoinColumn(name = "user_id"), @JoinColumn(name = "budget_id")}
    )
    @Column(name = "right", nullable = false)
    private List<Right> rights;
}
