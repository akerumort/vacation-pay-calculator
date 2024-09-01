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
@Schema(description = "Response DTO for detailed vacation pay calculation including weekends and holidays")
public class DetailedVacationPayResponseDto {

    @Schema(description = "The calculated vacation pay", example = "1500.00")
    @DecimalMin(value = "0.0", inclusive = false, message = "Vacation pay must be greater than zero")
    private BigDecimal vacationPay;

    @Schema(description = "The number of weekends and public holidays within the vacation period", example = "5")
    private Integer weekendsAndHolidays;

    @Schema(description = "The number of paid vacation days excluding weekends and public holidays", example = "20")
    private Integer paidVacationDays;

    @Schema(description = "Message indicating that the amount is after tax deduction",
            example = "Amount is calculated after deducting 13% tax.")
    private String message = "Amount is calculated after deducting 13% tax.";
}
