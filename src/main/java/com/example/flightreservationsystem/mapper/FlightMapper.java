package com.example.flightreservationsystem.mapper;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.entity.FlightEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FlightMapper {


    FlightDto toDto(FlightEntity entity);


    @Mapping(target = "reservations", ignore = true)
    FlightEntity toEntity(FlightDto dto);
}
