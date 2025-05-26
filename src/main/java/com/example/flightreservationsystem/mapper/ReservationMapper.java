package com.example.flightreservationsystem.mapper;

import com.example.flightreservationsystem.dto.ReservationDto;
import com.example.flightreservationsystem.entity.ReservationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(source = "status", target = "status")
    ReservationDto toDto(ReservationEntity entity);

    @Mapping(source = "status", target = "status")
    ReservationEntity toEntity(ReservationDto dto);
}