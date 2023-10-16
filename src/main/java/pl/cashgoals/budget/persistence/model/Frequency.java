package pl.cashgoals.budget.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;


@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Frequency {
    @Enumerated(EnumType.STRING)
    private Period period;
    @Column(name = "period_value")
    private Integer value;
}
