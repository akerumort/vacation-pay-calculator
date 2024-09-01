package com.akerumort.VacationPayCalculator.controllers;

import com.akerumort.VacationPayCalculator.dto.VacationPayRequestDto;
import com.akerumort.VacationPayCalculator.exceptions.CustomValidationException;
import com.akerumort.VacationPayCalculator.services.VacationPayService;
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

    @PostMapping
    public ResponseEntity<Object> calculateVacationPay(@Valid @RequestBody VacationPayRequestDto requestDto) {

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
