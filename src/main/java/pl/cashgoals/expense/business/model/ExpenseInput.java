package pl.cashgoals.expense.business.model;

public record ExpenseInput(
        Long id,
        String description,
        Double amount,
        String date,
        Long categoryId
) {
}
