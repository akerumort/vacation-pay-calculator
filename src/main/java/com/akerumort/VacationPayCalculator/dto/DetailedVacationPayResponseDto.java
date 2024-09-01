package com.akerumort.VacationPayCalculator.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailedVacationPayResponseDto {

    @DecimalMin(value = "0.0", inclusive = false, message = "Vacation pay must be greater than zero")
    private BigDecimal vacationPay;

    private Integer weekendsAndHolidays;
    private Integer paidVacationDays;
}
