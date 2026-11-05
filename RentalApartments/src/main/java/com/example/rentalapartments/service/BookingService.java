package com.example.rentalapartments.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.example.rentalapartments.model.Booking;

public interface BookingService {

	List<Booking> findAll();

	Booking findById(Long id);

	List<Booking> findByClientName(String clientName);

	List<Booking> findByApartmentId(Long apartmentId);

	Booking create(Booking booking);

	/**
	 * Бронює кілька квартир в одній транзакції: або всі, або жодної.
	 * Якщо для будь-якої квартири виникає помилка, уже створені
	 * бронювання цього виклику скасовуються (rollback).
	 */
	List<Booking> createMany(String clientName, List<Long> apartmentIds, LocalDate dateFrom, LocalDate dateTo);

	Booking cancel(Long id);

	void delete(Long id);

}