package com.akerumort.VacationPayCalculator.services;

import com.akerumort.VacationPayCalculator.dto.DetailedVacationPayResponseDto;
import com.akerumort.VacationPayCalculator.dto.SimpleVacationPayResponseDto;
import com.akerumort.VacationPayCalculator.exceptions.CustomValidationException;
import com.akerumort.VacationPayCalculator.mappers.VacationPayMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacationPayServiceTest {

    @InjectMocks
    private VacationPayService vacationPayService;

    @Mock
    private VacationPayMapper vacationPayMapper;

    private BigDecimal averageSalary;
    private int vacationDays;
    private List<LocalDate> vacationDates;
    private LocalDate vacationStartDate;
    private LocalDate vacationEndDate;

    @BeforeEach
    public void setUp() {
        averageSalary = new BigDecimal("80000.00");
        vacationDays = 14;
        vacationStartDate = LocalDate.of(2024, 9, 1);
        vacationEndDate = LocalDate.of(2024, 9, 14);
        vacationDates = new ArrayList<>(vacationStartDate.datesUntil(vacationEndDate.plusDays(1)).toList());
    }

    @Test
    public void testCalculateVacationPayWithoutSpecificDates() {
        SimpleVacationPayResponseDto expectedResponse = new SimpleVacationPayResponseDto(
                new BigDecimal("32000.00"), "Amount is calculated after deducting 13% tax.");

        when(vacationPayMapper.toSimpleDto(any(BigDecimal.class), any(String.class)))
                .thenReturn(expectedResponse);

        Object response = vacationPayService.calculateVacationPay(averageSalary, vacationDays,
                null, null, null);

        assertTrue(response instanceof SimpleVacationPayResponseDto);
        SimpleVacationPayResponseDto simpleDto = (SimpleVacationPayResponseDto) response;
        assertNotNull(simpleDto.getVacationPay());
        assertEquals("Amount is calculated after deducting 13% tax.", simpleDto.getMessage());
    }

    @Test
    public void testCalculateVacationPayWithSpecificDates() {
        DetailedVacationPayResponseDto expectedResponse = new DetailedVacationPayResponseDto(
                new BigDecimal("32000.00"), 4, 10, "Amount is " +
                "calculated after deducting 13% tax.");

        when(vacationPayMapper.toDetailedDto(any(BigDecimal.class), anyInt(), anyInt(), any(String.class)))
                .thenReturn(expectedResponse);

        Object response = vacationPayService.calculateVacationPay(averageSalary, vacationDays,
                vacationDates, vacationStartDate, vacationEndDate);

        assertTrue(response instanceof DetailedVacationPayResponseDto);
        DetailedVacationPayResponseDto detailedDto = (DetailedVacationPayResponseDto) response;
        assertNotNull(detailedDto.getVacationPay());
        assertEquals(4, detailedDto.getWeekendsAndHolidays());
        assertEquals(10, detailedDto.getPaidVacationDays());
        assertEquals("Amount is calculated after deducting 13% tax.", detailedDto.getMessage());
    }

    @Test
    public void testCalculateVacationPayWithMismatchedVacationDays() {
        vacationDays = 12;

        CustomValidationException exception = assertThrows(CustomValidationException.class, () ->
                vacationPayService.calculateVacationPay(averageSalary, vacationDays,
                        vacationDates, vacationStartDate, vacationEndDate));
        assertEquals("Unexpected error: The vacation days don't match the number of dates shown.",
                exception.getMessage());
    }

    @Test
    public void testCalculateVacationPayWithDuplicateDates() {
        vacationDates.add(vacationStartDate);

        CustomValidationException exception = assertThrows(CustomValidationException.class, () ->
                vacationPayService.calculateVacationPay(averageSalary, vacationDays,
                        vacationDates, vacationStartDate, vacationEndDate));
        assertEquals("Unexpected error: Vacation dates don't match the provided start and end dates. " +
                "Please ensure the dates match or choose one consistent method.", exception.getMessage());
    }

    @Test
    public void testCalculateVacationPayWithNullStartDate() {
        vacationStartDate = null;

        CustomValidationException exception = assertThrows(CustomValidationException.class, () ->
                vacationPayService.calculateVacationPay(averageSalary, vacationDays,
                        vacationDates, vacationStartDate, vacationEndDate));
        assertEquals("Unexpected error: Both start and end dates of the leave must be entered.",
                exception.getMessage());
    }

    @Test
    public void testCalculateVacationPayWithEndDateBeforeStartDate() {
        vacationEndDate = LocalDate.of(2024, 8, 31);

        CustomValidationException exception = assertThrows(CustomValidationException.class, () ->
                vacationPayService.calculateVacationPay(averageSalary, vacationDays,
                        vacationDates, vacationStartDate, vacationEndDate));

        assertEquals("Unexpected error: The end date of the leave may not be earlier than the start date.",
                exception.getMessage());
    }
}
