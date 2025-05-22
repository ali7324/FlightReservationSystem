package com.example.flightreservationsystem.Mapper;

import com.example.flightreservationsystem.dto.request.FlightRequestDto;
import com.example.flightreservationsystem.dto.response.FlightResponseDto;
import com.example.flightreservationsystem.entity.FlightEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FlightMapper {
    FlightMapper INSTANCE = Mappers.getMapper(FlightMapper.class);

    FlightEntity toEntity(FlightRequestDto flightRequestDto);

    FlightResponseDto toDto(FlightEntity flight);
}
