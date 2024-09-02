package com.akerumort.VacationPayCalculator.mappers;

import com.akerumort.VacationPayCalculator.dto.DetailedVacationPayResponseDto;
import com.akerumort.VacationPayCalculator.dto.SimpleVacationPayResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface VacationPayMapper {
    @Mapping(target = "message", source = "taxMessage")
    SimpleVacationPayResponseDto toSimpleDto(BigDecimal vacationPay, String taxMessage);

    @Mapping(target = "message", source = "taxMessage")
    @Mapping(target = "weekendsAndHolidays", source = "weekendsAndHolidays")
    @Mapping(target = "paidVacationDays", source = "paidVacationDays")
    DetailedVacationPayResponseDto toDetailedDto(BigDecimal vacationPay, int weekendsAndHolidays,
                                                 int paidVacationDays, String taxMessage);
}
