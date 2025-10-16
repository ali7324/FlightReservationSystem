package com.example.flightreservationsystem.mapper;

import com.example.flightreservationsystem.dto.ReservationDto;
import com.example.flightreservationsystem.entity.ReservationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {FlightMapper.class, PassengerMapper.class})
public interface ReservationMapper {


    ReservationDto toDto(ReservationEntity entity);


    @Mapping(target = "flight", ignore = true)
    @Mapping(target = "passenger", ignore = true)
    @Mapping(target = "reservationDate", ignore = true)
    ReservationEntity toEntity(ReservationDto dto);
}
