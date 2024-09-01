package com.akerumort.VacationPayCalculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Response DTO for simple vacation pay calculation")
public class SimpleVacationPayResponseDto {

    @Schema(description = "The calculated vacation pay", example = "1000.00")
    @DecimalMin(value = "0.0", inclusive = false, message = "Vacation pay must be greater than zero")
    private BigDecimal vacationPay;
}
