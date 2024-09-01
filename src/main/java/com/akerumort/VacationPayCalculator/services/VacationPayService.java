package com.akerumort.VacationPayCalculator.services;

import com.akerumort.VacationPayCalculator.dto.DetailedVacationPayResponseDto;
import com.akerumort.VacationPayCalculator.dto.SimpleVacationPayResponseDto;
import com.akerumort.VacationPayCalculator.exceptions.CustomValidationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VacationPayService {

    private static final BigDecimal WORK_DAYS_IN_MONTH = new BigDecimal(29.3);
    private static final BigDecimal TAX_RATE = new BigDecimal("0.13"); // НДФЛ 13%

    public Object calculateVacationPay(BigDecimal averageSalary, int vacationDays,
                                       List<LocalDate> vacationDates,
                                       LocalDate vacationStartDate,
                                       LocalDate vacationEndDate) {
        try {
            // проверка корректности дат
            if (vacationStartDate != null && vacationEndDate != null) {
                if (vacationEndDate.isBefore(vacationStartDate)) {
                    throw new CustomValidationException("The end date of the leave may " +
                            "not be earlier than the start date.");
                }

                List<LocalDate> generatedDates = vacationStartDate.datesUntil(vacationEndDate.
                        plusDays(1)).toList();

                if (generatedDates.size() != vacationDays) {
                    throw new CustomValidationException("The vacation days don't match the number " +
                            "of days between the beginning and the end of the leave.");
                }

                if (vacationDates != null && !vacationDates.isEmpty()) {
                    validateVacationDates(vacationDates, vacationStartDate, vacationEndDate);
                } else {
                    vacationDates = generatedDates;
                }
            }

            // проверка на совпадение vacationDays и vacationDates
            if (vacationDates != null && !vacationDates.isEmpty()) {
                if (vacationDates.size() != vacationDays) {
                    throw new CustomValidationException("The vacation days don't match the number of dates shown.");
                }
                checkForDuplicateDates(vacationDates);
            }

            int weekendsAndHolidays = filterOutHolidaysAndWeekends(vacationDates);
            int paidVacationDays = vacationDates.size() - weekendsAndHolidays;

            BigDecimal grossVacationPay = averageSalary.divide(WORK_DAYS_IN_MONTH, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(paidVacationDays));

            BigDecimal taxAmount = grossVacationPay.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);

            BigDecimal vacationPay = grossVacationPay.subtract(taxAmount);

            if (vacationDates == null || vacationDates.isEmpty()) {
                return new SimpleVacationPayResponseDto(vacationPay);
            } else {
                return new DetailedVacationPayResponseDto(vacationPay, weekendsAndHolidays, paidVacationDays);
            }
        } catch (ArithmeticException ex) {
            throw new CustomValidationException("Error in calculating vacation pay: " + ex.getMessage());
        } catch (Exception ex) {
            throw new CustomValidationException("Unexpected error: " + ex.getMessage());
        }
    }

    private void validateVacationDates(List<LocalDate> vacationDates, LocalDate startDate, LocalDate endDate) {
        List<LocalDate> expectedDates = startDate.datesUntil(endDate.plusDays(1)).toList();

        if (!vacationDates.equals(expectedDates)) {
            throw new CustomValidationException("Vacation dates don't match the provided start and end dates. " +
                    "Please ensure the dates match or choose one consistent method.");
        }
    }

    private void checkForDuplicateDates(List<LocalDate> vacationDates) {
        Set<LocalDate> dateSet = new HashSet<>(vacationDates);
        if (dateSet.size() < vacationDates.size()) {
            throw new CustomValidationException("Duplicate dates were found in the list of vacation dates.");
        }
    }

    private int filterOutHolidaysAndWeekends(List<LocalDate> vacationDates) {
        return (int) vacationDates.stream().filter(this::isHolidayOrWeekend).count();
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
