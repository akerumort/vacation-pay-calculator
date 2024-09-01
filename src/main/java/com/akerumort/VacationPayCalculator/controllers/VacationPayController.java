package com.akerumort.VacationPayCalculator.controllers;

import com.akerumort.VacationPayCalculator.dto.VacationPayRequestDto;
import com.akerumort.VacationPayCalculator.dto.VacationPayResponseDto;
import com.akerumort.VacationPayCalculator.mappers.VacationPayMapper;
import com.akerumort.VacationPayCalculator.services.VacationPayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

        BigDecimal vacationPay = vacationPayService.calculateVacationPay(
                requestDto.getAverageSalary(),
                requestDto.getVacationDays(),
                requestDto.getVacationDates());

        return ResponseEntity.ok(vacationPayMapper.toDto(vacationPay));
    }
}
