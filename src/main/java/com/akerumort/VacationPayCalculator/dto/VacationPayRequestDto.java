package com.akerumort.VacationPayCalculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for vacation pay calculation")
public class VacationPayRequestDto {

    @Schema(description = "The average salary over the year used for vacation pay calculation", example = "50000.00")
    @NotNull(message = "Average salary cannot be null")
    @DecimalMin(value = "1.0", inclusive = true, message = "Average salary must be greater than zero")
    private BigDecimal averageSalary;

    @Schema(description = "The total number of vacation days requested", example = "25")
    @NotNull(message = "Vacation days cannot be null")
    @Min(value = 1, message = "Vacation days must be at least 1")
    private int vacationDays;

    @Schema(description = "List of specific vacation dates", example = "[\"2024-09-01\", \"2024-09-02\"]")
    @Valid
    private List<LocalDate> vacationDates;

    @Schema(description = "Start date of the vacation period", example = "2024-09-01")
    private LocalDate vacationStartDate;

    @Schema(description = "End date of the vacation period", example = "2024-09-15")
    private LocalDate vacationEndDate;
}
