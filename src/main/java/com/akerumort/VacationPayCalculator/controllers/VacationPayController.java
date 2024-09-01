package com.akerumort.VacationPayCalculator.controllers;

import com.akerumort.VacationPayCalculator.dto.VacationPayRequestDto;
import com.akerumort.VacationPayCalculator.dto.VacationPayResponseDto;
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
    public ResponseEntity<VacationPayResponseDto> calculateVacationPay(
            @Valid @RequestBody VacationPayRequestDto requestDto) {

        try {
            BigDecimal vacationPay = vacationPayService.calculateVacationPay(
                    requestDto.getAverageSalary(),
                    requestDto.getVacationDays(),
                    requestDto.getVacationDates());

            return ResponseEntity.ok(vacationPayMapper.toDto(vacationPay));
        } catch (CustomValidationException ex) {
            return ResponseEntity.badRequest().body(new VacationPayResponseDto(BigDecimal.ZERO));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(new VacationPayResponseDto(BigDecimal.ZERO));
        }
    }
}
