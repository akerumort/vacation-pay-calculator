package com.akerumort.VacationPayCalculator.services;

import com.akerumort.VacationPayCalculator.dto.DetailedVacationPayResponseDto;
import com.akerumort.VacationPayCalculator.dto.SimpleVacationPayResponseDto;
import com.akerumort.VacationPayCalculator.exceptions.CustomValidationException;
import com.akerumort.VacationPayCalculator.mappers.VacationPayMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VacationPayService {

    private static final Logger logger = LogManager.getLogger(VacationPayService.class);
    private static final BigDecimal WORK_DAYS_IN_MONTH = new BigDecimal(29.3);
    private static final BigDecimal TAX_RATE = new BigDecimal("0.13"); // НДФЛ 13%
    private static final String TAX_MESSAGE = "Amount is calculated after deducting 13% tax.";

    private final VacationPayMapper vacationPayMapper;

    public Object calculateVacationPay(BigDecimal averageSalary, int vacationDays,
                                       List<LocalDate> vacationDates,
                                       LocalDate vacationStartDate,
                                       LocalDate vacationEndDate) {

        logger.info("Calculating vacation pay with averageSalary: {}, vacationDays: {}, " +
                        "vacationDates: {}, vacationStartDate: {}, vacationEndDate: {}",
                averageSalary, vacationDays, vacationDates, vacationStartDate, vacationEndDate);

        try {
            validateDates(vacationStartDate, vacationEndDate);

            // инициализация списка vacationDates как пустого, если он равен null
            if (vacationDates == null) {
                vacationDates = List.of();
            }

            // проверка на совпадение с vacationDays
            vacationDates = handleVacationDates(vacationDates, vacationStartDate, vacationEndDate, vacationDays);

            // проверка на наличие дублирующихся дат
            checkForDuplicateDates(vacationDates);

            if (vacationDates.isEmpty()) {
                // без учета конкретных дат
                BigDecimal grossVacationPay = averageSalary.divide(WORK_DAYS_IN_MONTH, 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(vacationDays));
                BigDecimal taxAmount = grossVacationPay.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
                BigDecimal vacationPay = grossVacationPay.subtract(taxAmount);
                logger.info("Calculated vacation pay without specific dates: {}", vacationPay);
                return vacationPayMapper.toSimpleDto(vacationPay, TAX_MESSAGE);
            } else {
                // с учетом конкретных дат
                int weekendsAndHolidays = filterOutHolidaysAndWeekends(vacationDates);
                int paidVacationDays = vacationDates.size() - weekendsAndHolidays;

                if (vacationDays != (paidVacationDays + weekendsAndHolidays)) {
                    throw new CustomValidationException("The number of vacation days does not match the " +
                            "provided vacation dates.");
                }

                BigDecimal grossVacationPay = averageSalary.divide(WORK_DAYS_IN_MONTH, 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(paidVacationDays));
                BigDecimal taxAmount = grossVacationPay.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
                BigDecimal vacationPay = grossVacationPay.subtract(taxAmount);

                logger.info("Calculated vacation pay with specific dates: vacationPay={}, " +
                                "weekendsAndHolidays={}, paidVacationDays={}",
                        vacationPay, weekendsAndHolidays, paidVacationDays);
                return vacationPayMapper.toDetailedDto(vacationPay, weekendsAndHolidays, paidVacationDays, TAX_MESSAGE);
            }
        } catch (ArithmeticException ex) {
            logger.error("Error calculating vacation pay: {}", ex.getMessage());
            throw new CustomValidationException("Error calculating vacation pay: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error: {}", ex.getMessage());
            throw new CustomValidationException("Unexpected error: " + ex.getMessage());
        }
    }

    private int filterOutHolidaysAndWeekends(List<LocalDate> vacationDates) {
        logger.info("Filtering out holidays and weekends from vacationDates...");

        return (int) vacationDates.stream().filter(this::isHolidayOrWeekend).count();
    }

    private List<LocalDate> handleVacationDates(List<LocalDate> vacationDates, LocalDate vacationStartDate,
                                                LocalDate vacationEndDate, int vacationDays) {

        logger.info("Handling vacation dates...");

        if (vacationStartDate != null && vacationEndDate != null) {
            List<LocalDate> generatedDates = vacationStartDate.datesUntil(vacationEndDate.
                    plusDays(1)).toList();

            if (vacationDates == null || vacationDates.isEmpty()) {
                vacationDates = generatedDates;
                logger.info("Generated vacation dates: {}", vacationDates);
            } else {
                // проверка на то, что переданные даты совпадают с ожидаемыми
                validateVacationDates(vacationDates, vacationStartDate, vacationEndDate);
            }

            if (vacationDates.size() != vacationDays) {
                throw new CustomValidationException("The vacation days don't match the number of dates shown.");
            }
        }

        return vacationDates;
    }

    private void validateDates(LocalDate vacationStartDate, LocalDate vacationEndDate) {

        logger.info("Validating dates: vacationStartDate={}, vacationEndDate={}",
                vacationStartDate, vacationEndDate);

        if ((vacationStartDate != null && vacationEndDate == null) ||
                (vacationEndDate != null && vacationStartDate == null)) {
            throw new CustomValidationException("Both start and end dates of the leave must be entered.");
        }

        if (vacationStartDate != null && vacationEndDate != null) {
            if (vacationEndDate.isBefore(vacationStartDate)) {
                throw new CustomValidationException("The end date of the leave " +
                        "may not be earlier than the start date.");
            }
        }
    }

    private void validateVacationDates(List<LocalDate> vacationDates, LocalDate startDate, LocalDate endDate) {
        logger.info("Validating vacation dates: {}", vacationDates);

        List<LocalDate> expectedDates = startDate.datesUntil(endDate.plusDays(1)).toList();

        if (!vacationDates.equals(expectedDates)) {
            throw new CustomValidationException("Vacation dates don't match the provided start and end dates. " +
                    "Please ensure the dates match or choose one consistent method.");
        }
    }

    private void checkForDuplicateDates(List<LocalDate> vacationDates) {
        logger.info("Checking for duplicate dates in vacationDates...");

        Set<LocalDate> dateSet = new HashSet<>(vacationDates);
        if (dateSet.size() < vacationDates.size()) {
            throw new CustomValidationException("Duplicate dates were found in the list of vacation dates.");
        }
    }

    private boolean isHolidayOrWeekend(LocalDate date) {
        boolean isHolidayOrWeekend = date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                date.getDayOfWeek() == DayOfWeek.SUNDAY ||
                isPublicHoliday(date);
        if (isHolidayOrWeekend) {
            logger.debug("Date {} is a holiday or weekend", date);
        }
        return isHolidayOrWeekend;
    }

    private boolean isPublicHoliday(LocalDate date) {
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
