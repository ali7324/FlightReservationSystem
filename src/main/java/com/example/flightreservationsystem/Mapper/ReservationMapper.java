package com.example.flightreservationsystem.Mapper;

import com.example.flightreservationsystem.dto.ReservationDto;
import com.example.flightreservationsystem.entity.ReservationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationDto toDto(ReservationEntity entity);

    ReservationEntity toEntity(ReservationDto dto);
}