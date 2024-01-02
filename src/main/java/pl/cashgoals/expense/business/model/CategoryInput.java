package pl.cashgoals.expense.business.model;

import java.util.List;

public record CategoryInput(
        Long id,
        Long parentId,
        String name,
        String description,
        Boolean visible,
        List<CategoryInput> children
) {

}
