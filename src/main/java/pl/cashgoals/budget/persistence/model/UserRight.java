package pl.cashgoals.budget.persistence.model;

import jakarta.persistence.*;
import lombok.*;
import pl.cashgoals.user.persistence.model.User;

import java.util.UUID;

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
    @JoinColumn(name = "budget_id", insertable = false, updatable = false)
    private Budget budget;

    @Column(name = "budget_id")
    private UUID budgetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "right_type")
    private Right right;
}
