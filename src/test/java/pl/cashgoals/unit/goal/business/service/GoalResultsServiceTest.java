package pl.cashgoals.unit.goal.business.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.cashgoals.expense.business.ExpenseFacade;
import pl.cashgoals.expense.persistence.model.Category;
import pl.cashgoals.expense.persistence.model.Expense;
import pl.cashgoals.goal.business.model.GoalResult;
import pl.cashgoals.goal.business.service.GoalResultsService;
import pl.cashgoals.goal.business.service.GoalService;
import pl.cashgoals.goal.business.strategies.goal.result.AmountStrategy;
import pl.cashgoals.goal.business.strategies.goal.result.GoalResultStrategyResolver;
import pl.cashgoals.goal.business.strategies.goal.result.PercentageStrategy;
import pl.cashgoals.goal.persistence.model.Goal;
import pl.cashgoals.goal.persistence.model.GoalType;
import pl.cashgoals.income.business.IncomeFacade;
import pl.cashgoals.income.persistence.model.IncomeItem;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalResultsServiceTest {
    @Mock
    private IncomeFacade incomeFacade;
    @Mock
    private ExpenseFacade expenseFacade;
    @Mock
    private GoalService goalService;
    @Mock
    private GoalResultStrategyResolver goalResultStrategyResolver;
    @InjectMocks
    private GoalResultsService goalResultsService;

    private final UUID budgetId = UUID.fromString("b3a0a0a0-0a0a-0a0a-0a0a-0a0a0a0a0a0a");
    private final Integer year = 2024;
    private final Integer month = 1;
    Category category = Category.builder().id(1L).budgetId(budgetId).build();

    private List<Goal> goals;
    private List<Expense> expenses;
    private List<IncomeItem> incomes;

    private void mockServices() {
        when(goalService.getGoals(budgetId)).thenReturn(goals);
        when(expenseFacade.getExpenses(budgetId, month, year)).thenReturn(expenses);
        when(incomeFacade.getIncomeItems(budgetId, month, year)).thenReturn(incomes);

        AmountStrategy amountStrategy = new AmountStrategy();
        PercentageStrategy percentageStrategy = new PercentageStrategy(amountStrategy);
        if (goals.stream().anyMatch(goal -> goal.getType() == GoalType.AMOUNT)) {
            when(goalResultStrategyResolver.resolve(GoalType.AMOUNT)).thenReturn(amountStrategy);
        }
        if (goals.stream().anyMatch(goal -> goal.getType() == GoalType.PERCENTAGE)) {
            when(goalResultStrategyResolver.resolve(GoalType.PERCENTAGE)).thenReturn(percentageStrategy);
        }
    }

    @Test
    @DisplayName("Should return true when amount min AMOUNT goal is reached")
    void shouldReturnTrueWhenAmountMinAmountGoalIsReached() {
        // Given
        Goal goal = Goal.builder()
                .id(1L)
                .budgetId(budgetId)
                .category(category)
                .type(GoalType.AMOUNT)
                .min(100.0)
                .build();
        goals = List.of(goal);
        
        expenses = List.of(
                Expense.builder()
                        .id(1L)
                        .category(category)
                        .amount(100.0)
                        .build()
        );
        incomes = List.of(
                IncomeItem.builder()
                        .id(1L)
                        .amount(100.0)
                        .build()
        );
        mockServices();

        // When
        List<GoalResult> goalResults = goalResultsService.getGoalResults(budgetId, year, month);

        // Then
        assertEquals(1, goalResults.size());
        GoalResult result = goalResults.get(0);
        assertEquals(goal, result.goal());
        assertEquals(100.0, result.actual());
        assertTrue(result.reached());
    }

    @Test
    @DisplayName("Should return true when amount max AMOUNT goal is reached")
    void shouldReturnTrueWhenAmountMaxAmountGoalIsReached() {
        // Given
        Goal goal = Goal.builder()
                .id(1L)
                .budgetId(budgetId)
                .category(category)
                .type(GoalType.AMOUNT)
                .max(100.0)
                .build();
        goals = List.of(goal);

        expenses = List.of(
                Expense.builder()
                        .id(1L)
                        .category(category)
                        .amount(100.0)
                        .build()
        );
        incomes = List.of(
                IncomeItem.builder()
                        .id(1L)
                        .amount(100.0)
                        .build()
        );
        mockServices();

        // When
        List<GoalResult> goalResults = goalResultsService.getGoalResults(budgetId, year, month);

        // Then
        assertEquals(1, goalResults.size());
        GoalResult result = goalResults.get(0);
        assertEquals(goal, result.goal());
        assertEquals(100.0, result.actual());
        assertTrue(result.reached());
    }

    @Test
    @DisplayName("Should return true when amount min and max AMOUNT goal are reached")
    void shouldReturnTrueWhenAmountMinAndMaxAmountGoalAreReached() {
        // Given
        Goal goal = Goal.builder()
                .id(1L)
                .budgetId(budgetId)
                .category(category)
                .type(GoalType.AMOUNT)
                .min(50.0)
                .max(100.0)
                .build();
        goals = List.of(goal);

        expenses = List.of(
                Expense.builder()
                        .id(1L)
                        .category(category)
                        .amount(100.0)
                        .build()
        );
        incomes = List.of(
                IncomeItem.builder()
                        .id(1L)
                        .amount(100.0)
                        .build()
        );
        mockServices();

        // When
        List<GoalResult> goalResults = goalResultsService.getGoalResults(budgetId, year, month);

        // Then
        assertEquals(1, goalResults.size());
        GoalResult result = goalResults.get(0);
        assertEquals(goal, result.goal());
        assertEquals(100.0, result.actual());
        assertTrue(result.reached());
    }

    @Test
    @DisplayName("Should return false when amount min AMOUNT goal is not reached")
    void shouldReturnFalseWhenAmountMinAmountGoalIsNotReached() {
        // Given
        Goal goal = Goal.builder()
                .id(1L)
                .budgetId(budgetId)
                .category(category)
                .type(GoalType.AMOUNT)
                .min(100.0)
                .build();
        goals = List.of(goal);

        expenses = List.of(
                Expense.builder()
                        .id(1L)
                        .category(category)
                        .amount(50.0)
                        .build()
        );
        incomes = List.of(
                IncomeItem.builder()
                        .id(1L)
                        .amount(100.0)
                        .build()
        );
        mockServices();

        // When
        List<GoalResult> goalResults = goalResultsService.getGoalResults(budgetId, year, month);

        // Then
        assertEquals(1, goalResults.size());
        GoalResult result = goalResults.get(0);
        assertEquals(goal, result.goal());
        assertEquals(50.0, result.actual());
        assertFalse(result.reached());
    }

    @Test
    @DisplayName("Should return false when amount max AMOUNT goal is not reached")
    void shouldReturnFalseWhenAmountMaxAmountGoalIsNotReached() {
        // Given
        Goal goal = Goal.builder()
                .id(1L)
                .budgetId(budgetId)
                .category(category)
                .type(GoalType.AMOUNT)
                .max(100.0)
                .build();
        goals = List.of(goal);

        expenses = List.of(
                Expense.builder()
                        .id(1L)
                        .category(category)
                        .amount(150.0)
                        .build()
        );
        incomes = List.of(
                IncomeItem.builder()
                        .id(1L)
                        .amount(100.0)
                        .build()
        );
        mockServices();

        // When
        List<GoalResult> goalResults = goalResultsService.getGoalResults(budgetId, year, month);

        // Then
        assertEquals(1, goalResults.size());
        GoalResult result = goalResults.get(0);
        assertEquals(goal, result.goal());
        assertEquals(150.0, result.actual());
        assertFalse(result.reached());
    }

    @Test
    @DisplayName("Should return false when amount min and max AMOUNT goal are not reached")
    void shouldReturnFalseWhenAmountMinAndMaxAmountGoalAreNotReached() {
        // Given
        Goal goal = Goal.builder()
                .id(1L)
                .budgetId(budgetId)
                .category(category)
                .type(GoalType.AMOUNT)
                .min(50.0)
                .max(100.0)
                .build();
        goals = List.of(goal);

        expenses = List.of(
                Expense.builder()
                        .id(1L)
                        .category(category)
                        .amount(150.0)
                        .build()
        );
        incomes = List.of(
                IncomeItem.builder()
                        .id(1L)
                        .amount(100.0)
                        .build()
        );
        mockServices();

        // When
        List<GoalResult> goalResults = goalResultsService.getGoalResults(budgetId, year, month);

        // Then
        assertEquals(1, goalResults.size());
        GoalResult result = goalResults.get(0);
        assertEquals(goal, result.goal());
        assertEquals(150.0, result.actual());
        assertFalse(result.reached());
    }

    @Test
    @DisplayName("Should return true when percentage min PERCENTAGE goal is reached")
    void shouldReturnTrueWhenPercentageMinPercentageGoalIsReached() {
        // Given
        Goal goal = Goal.builder()
                .id(1L)
                .budgetId(budgetId)
                .category(category)
                .type(GoalType.PERCENTAGE)
                .min(50.0)
                .build();
        goals = List.of(goal);

        expenses = List.of(
                Expense.builder()
                        .id(1L)
                        .category(category)
                        .amount(50.0)
                        .build()
        );
        incomes = List.of(
                IncomeItem.builder()
                        .id(1L)
                        .amount(100.0)
                        .build()
        );
        mockServices();

        // When
        List<GoalResult> goalResults = goalResultsService.getGoalResults(budgetId, year, month);

        // Then
        assertEquals(1, goalResults.size());
        GoalResult result = goalResults.get(0);
        assertEquals(goal, result.goal());
        assertEquals(50.0, result.actual());
        assertTrue(result.reached());
    }

    @Test
    @DisplayName("Should return true when percentage max PERCENTAGE goal is reached")
    void shouldReturnTrueWhenPercentageMaxPercentageGoalIsReached() {
        // Given
        Goal goal = Goal.builder()
                .id(1L)
                .budgetId(budgetId)
                .category(category)
                .type(GoalType.PERCENTAGE)
                .max(50.0)
                .build();
        goals = List.of(goal);

        expenses = List.of(
                Expense.builder()
                        .id(1L)
                        .category(category)
                        .amount(50.0)
                        .build()
        );
        incomes = List.of(
                IncomeItem.builder()
                        .id(1L)
                        .amount(100.0)
                        .build()
        );
        mockServices();

        // When
        List<GoalResult> goalResults = goalResultsService.getGoalResults(budgetId, year, month);

        // Then
        assertEquals(1, goalResults.size());
        GoalResult result = goalResults.get(0);
        assertEquals(goal, result.goal());
        assertEquals(50.0, result.actual());
        assertTrue(result.reached());
    }

    @Test
    @DisplayName("Should return true when percentage min and max PERCENTAGE goal are reached")
    void shouldReturnTrueWhenPercentageMinAndMaxPercentageGoalAreReached() {
        // Given
        Goal goal = Goal.builder()
                .id(1L)
                .budgetId(budgetId)
                .category(category)
                .type(GoalType.PERCENTAGE)
                .min(25.0)
                .max(50.0)
                .build();
        goals = List.of(goal);

        expenses = List.of(
                Expense.builder()
                        .id(1L)
                        .category(category)
                        .amount(25.0)
                        .build()
        );
        incomes = List.of(
                IncomeItem.builder()
                        .id(1L)
                        .amount(100.0)
                        .build()
        );
        mockServices();

        // When
        List<GoalResult> goalResults = goalResultsService.getGoalResults(budgetId, year, month);

        // Then
        assertEquals(1, goalResults.size());
        GoalResult result = goalResults.get(0);
        assertEquals(goal, result.goal());
        assertEquals(25.0, result.actual());
        assertTrue(result.reached());
    }

    @Test
    @DisplayName("Should return false when percentage min PERCENTAGE goal is not reached")
    void shouldReturnFalseWhenPercentageMinPercentageGoalIsNotReached() {
        // Given
        Goal goal = Goal.builder()
                .id(1L)
                .budgetId(budgetId)
                .category(category)
                .type(GoalType.PERCENTAGE)
                .min(50.0)
                .build();
        goals = List.of(goal);

        expenses = List.of(
                Expense.builder()
                        .id(1L)
                        .category(category)
                        .amount(25.0)
                        .build()
        );
        incomes = List.of(
                IncomeItem.builder()
                        .id(1L)
                        .amount(100.0)
                        .build()
        );
        mockServices();

        // When
        List<GoalResult> goalResults = goalResultsService.getGoalResults(budgetId, year, month);

        // Then
        assertEquals(1, goalResults.size());
        GoalResult result = goalResults.get(0);
        assertEquals(goal, result.goal());
        assertFalse(result.reached());
    }

    @Test
    @DisplayName("Should return false when percentage max PERCENTAGE goal is not reached")
    void shouldReturnFalseWhenPercentageMaxPercentageGoalIsNotReached() {
        // Given
        Goal goal = Goal.builder()
                .id(1L)
                .budgetId(budgetId)
                .category(category)
                .type(GoalType.PERCENTAGE)
                .max(50.0)
                .build();
        goals = List.of(goal);

        expenses = List.of(
                Expense.builder()
                        .id(1L)
                        .category(category)
                        .amount(75.0)
                        .build()
        );
        incomes = List.of(
                IncomeItem.builder()
                        .id(1L)
                        .amount(100.0)
                        .build()
        );
        mockServices();

        // When
        List<GoalResult> goalResults = goalResultsService.getGoalResults(budgetId, year, month);

        // Then
        assertEquals(1, goalResults.size());
        GoalResult result = goalResults.get(0);
        assertEquals(goal, result.goal());
        assertFalse(result.reached());
    }

    @Test
    @DisplayName("Should return false when percentage min and max PERCENTAGE goal are not reached")
    void shouldReturnFalseWhenPercentageMinAndMaxPercentageGoalAreNotReached() {
        // Given
        Goal goal = Goal.builder()
                .id(1L)
                .budgetId(budgetId)
                .category(category)
                .type(GoalType.PERCENTAGE)
                .min(25.0)
                .max(50.0)
                .build();
        goals = List.of(goal);

        expenses = List.of(
                Expense.builder()
                        .id(1L)
                        .category(category)
                        .amount(75.0)
                        .build()
        );
        incomes = List.of(
                IncomeItem.builder()
                        .id(1L)
                        .amount(100.0)
                        .build()
        );
        mockServices();

        // When
        List<GoalResult> goalResults = goalResultsService.getGoalResults(budgetId, year, month);

        // Then
        assertEquals(1, goalResults.size());
        GoalResult result = goalResults.get(0);
        assertEquals(goal, result.goal());
        assertFalse(result.reached());
    }

    @Test
    @DisplayName("Should use parent category when checking expenses for goal")
    void shouldUseParentCategoryWhenCheckingExpensesForGoal() {
        // Given
        Category parentCategory = Category.builder().id(2L).budgetId(budgetId).build();
        Category childCategory = Category.builder().id(1L).budgetId(budgetId).parent(parentCategory).build();
        Goal goal = Goal.builder()
                .id(1L)
                .budgetId(budgetId)
                .category(parentCategory)
                .type(GoalType.AMOUNT)
                .min(100.0)
                .build();
        goals = List.of(goal);

        expenses = List.of(
                Expense.builder()
                        .id(1L)
                        .category(childCategory)
                        .amount(100.0)
                        .build()
        );
        incomes = List.of(
                IncomeItem.builder()
                        .id(1L)
                        .amount(100.0)
                        .build()
        );
        mockServices();

        // When
        List<GoalResult> goalResults = goalResultsService.getGoalResults(budgetId, year, month);

        // Then
        assertEquals(1, goalResults.size());
        GoalResult result = goalResults.get(0);
        assertEquals(100.0, result.actual());
        assertTrue(result.reached());
    }
}
