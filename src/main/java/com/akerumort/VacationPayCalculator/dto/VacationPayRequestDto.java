package com.akerumort.VacationPayCalculator.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class VacationPayRequestDto {

    private BigDecimal averageSalary;
    private int vacationDays;
    private List<LocalDate> vacationDates;
}
