package com.example.flightreservationsystem.mapper;

import com.example.flightreservationsystem.dto.PassengerDto;
import com.example.flightreservationsystem.entity.PassengerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

    PassengerDto toDto(PassengerEntity entity);

    @Mapping(target = "reservations", ignore = true)
    PassengerEntity toEntity(PassengerDto dto);
}
