package com.akerumort.VacationPayCalculator.mappers;

import com.akerumort.VacationPayCalculator.dto.SimpleVacationPayResponseDto;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface VacationPayMapper {
    SimpleVacationPayResponseDto toDto(BigDecimal vacationPay);
}
