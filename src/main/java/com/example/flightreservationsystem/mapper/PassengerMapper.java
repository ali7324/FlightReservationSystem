package com.example.flightreservationsystem.mapper;

import com.example.flightreservationsystem.dto.PassengerDto;
import com.example.flightreservationsystem.entity.PassengerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

    PassengerDto toDto(PassengerEntity entity);

    PassengerEntity toEntity(PassengerDto dto);
}
