package com.example.flightreservationsystem.mapper;

import com.example.flightreservationsystem.dto.ReservationDto;
import com.example.flightreservationsystem.entity.ReservationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    ReservationDto toDto(ReservationEntity entity);

    ReservationEntity toEntity(ReservationDto dto);
}