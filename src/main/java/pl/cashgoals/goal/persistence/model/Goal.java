package pl.cashgoals.goal.persistence.model;

import jakarta.persistence.*;
import lombok.*;
import pl.cashgoals.expence.persistence.model.Category;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private GoalType type;
    private Double min;
    private Double max;

    @ManyToOne
    private Category category;

    private UUID budgetId;
}
