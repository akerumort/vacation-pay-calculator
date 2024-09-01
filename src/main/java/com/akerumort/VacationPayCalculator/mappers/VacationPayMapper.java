package com.akerumort.VacationPayCalculator.mappers;

import com.akerumort.VacationPayCalculator.dto.VacationPayResponseDto;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface VacationPayMapper {
    VacationPayResponseDto toDto(BigDecimal vacationPay);
}
