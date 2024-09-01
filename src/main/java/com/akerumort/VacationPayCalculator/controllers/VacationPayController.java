package com.akerumort.VacationPayCalculator.controllers;

import com.akerumort.VacationPayCalculator.dto.VacationPayRequestDto;
import com.akerumort.VacationPayCalculator.dto.SimpleVacationPayResponseDto;
import com.akerumort.VacationPayCalculator.exceptions.CustomValidationException;
import com.akerumort.VacationPayCalculator.mappers.VacationPayMapper;
import com.akerumort.VacationPayCalculator.services.VacationPayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/calculate")
@RequiredArgsConstructor
@Validated
public class VacationPayController {

    private final VacationPayService vacationPayService;
    private final VacationPayMapper vacationPayMapper;

    @PostMapping
    public ResponseEntity<Object> calculateVacationPay(@Valid @RequestBody VacationPayRequestDto requestDto) {
        try {
            // без конкретных дат
            Object response = vacationPayService.calculateVacationPay(
                    requestDto.getAverageSalary(),
                    requestDto.getVacationDays(),
                    requestDto.getVacationDates());

            // с конкретными датами
            if (requestDto.getVacationDates() == null || requestDto.getVacationDates().isEmpty()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.ok(response);
            }
        } catch (CustomValidationException ex) {
            return ResponseEntity.badRequest().body(new SimpleVacationPayResponseDto(BigDecimal.ZERO));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(new SimpleVacationPayResponseDto(BigDecimal.ZERO));
        }
    }
}
