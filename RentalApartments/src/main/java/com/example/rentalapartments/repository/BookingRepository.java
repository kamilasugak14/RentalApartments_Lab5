package com.example.rentalapartments.repository;

import com.example.rentalapartments.model.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    List<Booking> findAll();
    Optional<Booking> findById(Long id);
    List<Booking> findByClientName(String clientName);
    List<Booking> findByApartmentId(Long apartmentId);
    boolean existsOverlapping(Long apartmentId, LocalDate dateFrom, LocalDate dateTo);
    Booking save(Booking booking);
    boolean deleteById(Long id);
}
