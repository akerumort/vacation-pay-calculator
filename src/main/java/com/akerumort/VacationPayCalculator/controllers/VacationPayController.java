package com.akerumort.VacationPayCalculator.controllers;

import com.akerumort.VacationPayCalculator.dto.VacationPayRequestDto;
import com.akerumort.VacationPayCalculator.exceptions.CustomValidationException;
import com.akerumort.VacationPayCalculator.services.VacationPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calculate")
@RequiredArgsConstructor
@Validated
public class VacationPayController {

    private final VacationPayService vacationPayService;

    @Operation(
            summary = "Calculate vacation pay with personal income tax",
            description = "Calculates vacation pay based on average salary, number of vacation days, " +
                    "and optional vacation dates.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully calculated vacation pay",
                            content = @Content(
                                    schema = @Schema(implementation = Object.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                    @ApiResponse(responseCode = "500", description = "Unexpected server error")
            }
    )
    @PostMapping
    public ResponseEntity<Object> calculateVacationPay(@Valid @RequestBody
                                                           @Parameter(description =
                                                                   "Request payload for calculating vacation pay")
                                                           VacationPayRequestDto requestDto) {
        try {
            Object response = vacationPayService.calculateVacationPay(
                    requestDto.getAverageSalary(),
                    requestDto.getVacationDays(),
                    requestDto.getVacationDates(),
                    requestDto.getVacationStartDate(),
                    requestDto.getVacationEndDate()
            );

            return ResponseEntity.ok(response);
        } catch (CustomValidationException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Unexpected error: " + ex.getMessage());
        }
    }
}
