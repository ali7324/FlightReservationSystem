package com.example.flightreservationsystem.Mapper;

import com.example.flightreservationsystem.dto.PassengerDto;
import com.example.flightreservationsystem.entity.PassengerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

    PassengerDto toDto(PassengerEntity entity);
    PassengerEntity toEntity(PassengerDto dto);
}
