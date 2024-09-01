package com.akerumort.VacationPayCalculator.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VacationPayResponseDto {

    @DecimalMin(value = "0.0", inclusive = false, message = "Vacation pay must be greater than zero")
    private BigDecimal vacationPay;
}
