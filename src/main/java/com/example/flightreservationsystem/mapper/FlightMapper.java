package com.example.flightreservationsystem.mapper;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.entity.FlightEntity;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = ReservationMapper.class)
public interface FlightMapper {
    FlightDto toDto(FlightEntity entity);

    FlightEntity toEntity(FlightDto dto);
}
