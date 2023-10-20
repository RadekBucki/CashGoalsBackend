package pl.cashgoals.budget.persistence.model;

import jakarta.persistence.*;
import lombok.*;
import pl.cashgoals.user.persistence.model.User;

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

    @Id
    @ManyToOne
    private Budget budget;

    @Id
    @Enumerated(EnumType.STRING)
    private Right right;
}
