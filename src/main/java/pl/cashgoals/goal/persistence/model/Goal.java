package pl.cashgoals.goal.persistence.model;

import jakarta.persistence.*;
import lombok.*;
import pl.cashgoals.expence.persistence.model.Category;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private GoalType type;
    private Double value;

    @ManyToOne
    private Category category;
}
