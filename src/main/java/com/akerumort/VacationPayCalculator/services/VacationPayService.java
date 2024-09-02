package com.akerumort.VacationPayCalculator.services;

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
    private static final BigDecimal WORK_DAYS_IN_MONTH = new BigDecimal("29.3");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.13"); // НДФЛ 13%
    private static final String TAX_MESSAGE = "Amount is calculated after deducting 13% tax.";

    private final VacationPayMapper vacationPayMapper;

    /**
     * Calculates vacation pay considering the average salary, number of vacation days, and vacation dates.
     *
     * @param averageSalary Average salary
     * @param vacationDays Number of vacation days
     * @param vacationDates List of specific vacation dates
     * @param vacationStartDate Start date of the vacation
     * @param vacationEndDate End date of the vacation
     * @return DTO with vacation pay calculation
     */
    public Object calculateVacationPay(BigDecimal averageSalary, int vacationDays,
                                       List<LocalDate> vacationDates,
                                       LocalDate vacationStartDate,
                                       LocalDate vacationEndDate) {

        logger.info("Calculating vacation pay with averageSalary: {}, vacationDays: {}, " +
                        "vacationDates: {}, vacationStartDate: {}, vacationEndDate: {}",
                averageSalary, vacationDays, vacationDates, vacationStartDate, vacationEndDate);

        try {
            validateDates(vacationStartDate, vacationEndDate);

            vacationDates = initializeVacationDates(vacationDates, vacationStartDate, vacationEndDate);
            vacationDates = handleVacationDates(vacationDates, vacationStartDate, vacationEndDate, vacationDays);
            checkForDuplicateDates(vacationDates);

            if (vacationDates.isEmpty()) {
                return calculateSimpleVacationPay(averageSalary, vacationDays);
            } else {
                return calculateDetailedVacationPay(averageSalary, vacationDays, vacationDates);
            }
        } catch (ArithmeticException ex) {
            logger.error("Error calculating vacation pay: {}", ex.getMessage());
            throw new CustomValidationException("Error calculating vacation pay: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error: {}", ex.getMessage());
            throw new CustomValidationException("Unexpected error: " + ex.getMessage());
        }
    }

    /**
     * Validates the start and end dates of the vacation.
     *
     * @param vacationStartDate Start date of the vacation
     * @param vacationEndDate End date of the vacation
     */
    private void validateDates(LocalDate vacationStartDate, LocalDate vacationEndDate) {
        logger.info("Validating dates: vacationStartDate={}, vacationEndDate={}",
                vacationStartDate, vacationEndDate);

        if ((vacationStartDate != null && vacationEndDate == null) ||
                (vacationEndDate != null && vacationStartDate == null)) {
            throw new CustomValidationException("Both start and end dates of the leave must be entered.");
        }

        if (vacationStartDate != null && vacationEndDate != null && vacationEndDate.isBefore(vacationStartDate)) {
            throw new CustomValidationException("The end date of the leave may not be earlier than the start date.");
        }
    }

    /**
     * Initializes the list of vacation dates if it is null.
     *
     * @param vacationDates List of vacation dates
     * @param vacationStartDate Start date of the vacation
     * @param vacationEndDate End date of the vacation
     * @return Initialized list of vacation dates
     */
    private List<LocalDate> initializeVacationDates(List<LocalDate> vacationDates,
                                                    LocalDate vacationStartDate, LocalDate vacationEndDate) {
        if (vacationDates == null) {
            vacationDates = List.of();
        }
        return vacationDates;
    }

    /**
     * Processes and adjusts the list of vacation dates based on the provided start and end dates.
     *
     * @param vacationDates List of vacation dates
     * @param vacationStartDate Start date of the vacation
     * @param vacationEndDate End date of the vacation
     * @param vacationDays Number of vacation days
     * @return Processed list of vacation dates
     */
    private List<LocalDate> handleVacationDates(List<LocalDate> vacationDates, LocalDate vacationStartDate,
                                                LocalDate vacationEndDate, int vacationDays) {

        logger.info("Handling vacation dates...");

        if (vacationStartDate != null && vacationEndDate != null) {
            List<LocalDate> generatedDates = vacationStartDate.datesUntil(vacationEndDate.plusDays(1)).toList();

            if (vacationDates.isEmpty()) {
                vacationDates = generatedDates;
                logger.info("Generated vacation dates: {}", vacationDates);
            } else {
                validateVacationDates(vacationDates, vacationStartDate, vacationEndDate);
            }

            if (vacationDates.size() != vacationDays) {
                throw new CustomValidationException("The vacation days don't match the number of dates shown.");
            }
        }

        return vacationDates;
    }

    /**
     * Validates that the provided vacation dates match the expected dates based on the start and end dates.
     *
     * @param vacationDates List of vacation dates
     * @param startDate Start date of the vacation
     * @param endDate End date of the vacation
     */
    private void validateVacationDates(List<LocalDate> vacationDates, LocalDate startDate, LocalDate endDate) {
        logger.info("Validating vacation dates: {}", vacationDates);

        List<LocalDate> expectedDates = startDate.datesUntil(endDate.plusDays(1)).toList();

        if (!vacationDates.equals(expectedDates)) {
            throw new CustomValidationException("Vacation dates don't match the provided start and end dates. " +
                    "Please ensure the dates match or choose one consistent method.");
        }
    }

    /**
     * Checks for duplicate dates in the list of vacation dates.
     *
     * @param vacationDates List of vacation dates
     */
    private void checkForDuplicateDates(List<LocalDate> vacationDates) {
        logger.info("Checking for duplicate dates in vacationDates...");

        Set<LocalDate> dateSet = new HashSet<>(vacationDates);

        if (dateSet.size() < vacationDates.size()) {
            throw new CustomValidationException("Duplicate dates were found in the list of vacation dates.");
        }
    }

    /**
     * Calculates vacation pay without considering specific dates.
     *
     * @param averageSalary Average salary
     * @param vacationDays Number of vacation days
     * @return DTO with vacation pay calculation
     */
    private Object calculateSimpleVacationPay(BigDecimal averageSalary, int vacationDays) {
        BigDecimal grossVacationPay = calculateGrossVacationPay(averageSalary, vacationDays);
        BigDecimal vacationPay = calculateNetVacationPay(grossVacationPay);
        logger.info("Calculated vacation pay without specific dates: {}", vacationPay);
        return vacationPayMapper.toSimpleDto(vacationPay, TAX_MESSAGE);
    }

    /**
     * Calculates vacation pay with consideration of specific dates.
     *
     * @param averageSalary Average salary
     * @param vacationDays Number of vacation days
     * @param vacationDates List of vacation dates
     * @return DTO with vacation pay calculation
     */
    private Object calculateDetailedVacationPay(BigDecimal averageSalary, int vacationDays,
                                                List<LocalDate> vacationDates) {
        int weekendsAndHolidays = filterOutHolidaysAndWeekends(vacationDates);
        int paidVacationDays = vacationDates.size() - weekendsAndHolidays;

        if (vacationDays != (paidVacationDays + weekendsAndHolidays)) {
            throw new CustomValidationException("The number of vacation days does not match " +
                    "the provided vacation dates.");
        }

        BigDecimal grossVacationPay = calculateGrossVacationPay(averageSalary, paidVacationDays);
        BigDecimal vacationPay = calculateNetVacationPay(grossVacationPay);

        logger.info("Calculated vacation pay with specific dates: vacationPay={}, " +
                        "weekendsAndHolidays={}, paidVacationDays={}",
                vacationPay, weekendsAndHolidays, paidVacationDays);
        return vacationPayMapper.toDetailedDto(vacationPay, weekendsAndHolidays, paidVacationDays, TAX_MESSAGE);
    }

    /**
     * Calculates gross vacation pay.
     *
     * @param averageSalary Average salary
     * @param days Number of paid days
     * @return Gross vacation pay
     */
    private BigDecimal calculateGrossVacationPay(BigDecimal averageSalary, int days) {
        return averageSalary.divide(WORK_DAYS_IN_MONTH, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(days));
    }

    /**
     * Calculates net vacation pay after tax deduction.
     *
     * @param grossVacationPay Gross vacation pay
     * @return Net vacation pay
     */
    private BigDecimal calculateNetVacationPay(BigDecimal grossVacationPay) {
        BigDecimal taxAmount = grossVacationPay.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        return grossVacationPay.subtract(taxAmount);
    }

    /**
     * Filters out holidays and weekends from the list of vacation dates.
     *
     * @param vacationDates List of vacation dates
     * @return Number of holidays and weekends
     */
    private int filterOutHolidaysAndWeekends(List<LocalDate> vacationDates) {
        logger.info("Filtering out holidays and weekends from vacationDates...");
        return (int) vacationDates.stream().filter(this::isHolidayOrWeekend).count();
    }

    /**
     * Checks if a given date is a holiday or weekend.
     *
     * @param date Date to check
     * @return true if the date is a holiday or weekend; otherwise false
     */
    private boolean isHolidayOrWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                date.getDayOfWeek() == DayOfWeek.SUNDAY ||
                isPublicHoliday(date);
    }

    /**
     * Checks if a given date is a public holiday.
     *
     * @param date Date to check
     * @return true if the date is a public holiday; otherwise false
     */
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
