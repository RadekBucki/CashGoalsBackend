package pl.cashgoals.budget.persistence.model;

import jakarta.persistence.*;
import lombok.*;
import pl.cashgoals.user.persistence.model.User;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Budget budget;

    @Enumerated(EnumType.STRING)
    @Column(name = "right_type")
    private Right right;
}
