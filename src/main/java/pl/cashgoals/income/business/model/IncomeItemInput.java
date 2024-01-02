package pl.cashgoals.income.business.model;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record IncomeItemInput(
        Long id,
        Long incomeId,
        String description,
        Double amount,
        LocalDate date
) {
}
