package com.example.flightreservationsystem.Mapper;

import com.example.flightreservationsystem.dto.request.PassengerRequestDto;
import com.example.flightreservationsystem.dto.response.PassengerResponseDto;
import com.example.flightreservationsystem.entity.PassengerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PassengerMapper {
    PassengerMapper INSTANCE = Mappers.getMapper(PassengerMapper.class);

    PassengerEntity toEntity(PassengerRequestDto passengerRequestDto);

    PassengerResponseDto toDto(PassengerEntity passengerEntity);
}
