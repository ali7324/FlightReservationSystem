package com.example.flightreservationsystem.mapper;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.entity.FlightEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FlightMapper {

    // Entity -> DTO: reservations sahəsi DTO-da yoxdur, problem deyil
    FlightDto toDto(FlightEntity entity);

    // DTO -> Entity: reservations döngə yaratmasın deyə ignore edirik
    @Mapping(target = "reservations", ignore = true)
    FlightEntity toEntity(FlightDto dto);
}
