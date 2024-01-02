package pl.cashgoals.expense.business.model;

import java.time.LocalDate;

public record ExpenseInput(
        Long id,
        String description,
        Double amount,
        LocalDate date,
        Long categoryId
) {
}
