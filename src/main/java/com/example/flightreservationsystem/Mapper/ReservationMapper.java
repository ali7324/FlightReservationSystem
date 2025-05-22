package com.example.flightreservationsystem.Mapper;

import com.example.flightreservationsystem.dto.request.ReservationRequestDto;
import com.example.flightreservationsystem.dto.response.ReservationResponseDto;
import com.example.flightreservationsystem.entity.ReservationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    ReservationEntity toEntity(ReservationRequestDto reservationRequestDto);

    ReservationResponseDto toDto(ReservationEntity reservationEntity);
}
