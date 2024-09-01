package com.akerumort.VacationPayCalculator.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class VacationPayService {

    private static final BigDecimal WORK_DAYS_IN_MONTH = new BigDecimal(29.3);
    private static final BigDecimal TAX_RATE = new BigDecimal("0.13"); // НДФЛ 13%

    public BigDecimal calculateVacationPay(BigDecimal averageSalary, int vacationDays, List<LocalDate> vacationDates) {
        if (vacationDates != null && !vacationDates.isEmpty()) {
            vacationDays = filterOutHolidaysAndWeekends(vacationDates);
        }
        // до вычета налогов
        BigDecimal grossVacationPay = averageSalary.divide(WORK_DAYS_IN_MONTH, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(vacationDays));

        // НДФЛ
        BigDecimal taxAmount = grossVacationPay.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);

        // чистые
        return grossVacationPay.subtract(taxAmount);
    }

    private int filterOutHolidaysAndWeekends(List<LocalDate> vacationDays) {
        return (int) vacationDays.stream().filter(date -> !isHolidayOrWeekend(date)).count();
    }

    private boolean isHolidayOrWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY || isPublicHoliday(date);
    }

    private boolean isPublicHoliday(LocalDate date) {
        // праздники
        Set<LocalDate> holidays = Set.of(
                LocalDate.of(date.getYear(), 1, 1),
                LocalDate.of(date.getYear(), 1, 2),
                LocalDate.of(date.getYear(), 1, 3),
                LocalDate.of(date.getYear(), 1, 4),
                LocalDate.of(date.getYear(), 1, 5),
                LocalDate.of(date.getYear(), 1, 6),
                LocalDate.of(date.getYear(), 1, 7),
                LocalDate.of(date.getYear(), 1, 8),
                LocalDate.of(date.getYear(), 2, 23),
                LocalDate.of(date.getYear(), 3, 8),
                LocalDate.of(date.getYear(), 5, 1),
                LocalDate.of(date.getYear(), 5, 9),
                LocalDate.of(date.getYear(), 6, 12),
                LocalDate.of(date.getYear(), 11, 4)
        );
        return holidays.contains(date);
    }
}
