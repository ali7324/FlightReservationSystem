package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.entity.PassengerEntity;
import com.example.flightreservationsystem.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;

    @Autowired
    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    public List<PassengerEntity> getAllPassengers(){
        return passengerRepository.findAll();
    }

    public Optional<PassengerEntity> getPassengerById(Long id){
        return passengerRepository.findById(id);
    }

    public PassengerEntity addPassenger(PassengerEntity passenger){
        return passengerRepository.save(passenger);
    }

    public PassengerEntity updatePassenger(Long id, PassengerEntity passenger){
        if (passengerRepository.existsById(id)) {
            passenger.setId(id);
            return passengerRepository.save(passenger);
        } else {
            return null;
        }
    }

    public void deletePassenger(Long id){
        passengerRepository.deleteById(id);
    }
}
