package com.akerumort.VacationPayCalculator.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
public class VacationPayRequestDto {

    @NotNull(message = "Average salary cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Average salary must be greater than zero")
    private BigDecimal averageSalary;

    @NotNull(message = "Vacation days cannot be null")
    @Min(value = 1, message = "Vacation days must be at least 1")
    private int vacationDays;

    @NotEmpty(message = "Vacation dates cannot be empty")
    private List<@NotNull(message = "Date cannot be null")
            LocalDate> vacationDates;
}
