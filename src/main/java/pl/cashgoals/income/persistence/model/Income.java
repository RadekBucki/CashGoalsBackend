package pl.cashgoals.income.persistence.model;

import jakarta.persistence.*;
import lombok.*;
import pl.cashgoals.budget.persistence.model.Budget;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double amount;
    @Embedded
    private Frequency frequency;
    @ManyToOne
    private Budget budget;
}
