package com.example.rentalapartments.service;

import com.example.rentalapartments.exception.ApartmentNotFoundException;
import com.example.rentalapartments.exception.BookingConflictException;
import com.example.rentalapartments.exception.BookingNotFoundException;
import com.example.rentalapartments.model.Booking;
import com.example.rentalapartments.repository.ApartmentRepository;
import com.example.rentalapartments.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ApartmentRepository apartmentRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                          ApartmentRepository apartmentRepository) {
        this.bookingRepository = bookingRepository;
        this.apartmentRepository = apartmentRepository;
    }

    @Override
	public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Override
	public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    @Override
	public List<Booking> findByClientName(String clientName) {
        return bookingRepository.findByClientName(clientName);
    }

    @Override
	public List<Booking> findByApartmentId(Long apartmentId) {
        return bookingRepository.findByApartmentId(apartmentId);
    }

    @Override
	@Transactional
    public Booking create(Booking booking) {
        return book(booking.getApartmentId(), booking.getClientName(),
                booking.getDateFrom(), booking.getDateTo());
    }

    /**
     * Бронює кілька квартир в одній транзакції: або всі, або жодної.
     * Якщо для будь-якої квартири виникає помилка, уже створені
     * бронювання цього виклику скасовуються (rollback).
     */
    @Override
	@Transactional
    public List<Booking> createMany(String clientName, List<Long> apartmentIds,
                                    LocalDate dateFrom, LocalDate dateTo) {
        List<Booking> result = new ArrayList<>();
        for (Long apartmentId : apartmentIds) {
            result.add(book(apartmentId, clientName, dateFrom, dateTo));
        }
        return result;
    }

    @Override
	@Transactional
    public Booking cancel(Long id) {
        Booking booking = findById(id);
        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }

    @Override
	@Transactional
    public void delete(Long id) {
        if (!bookingRepository.deleteById(id)) {
            throw new BookingNotFoundException(id);
        }
    }

    private Booking book(Long apartmentId, String clientName,
                         LocalDate dateFrom, LocalDate dateTo) {
        if (clientName == null || clientName.isBlank()) {
            throw new IllegalArgumentException("Ім’я клієнта не може бути порожнім");
        }
        if (dateFrom == null || dateTo == null || !dateTo.isAfter(dateFrom)) {
            throw new IllegalArgumentException("Дата завершення має бути пізнішою за дату початку");
        }
        apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));
        if (bookingRepository.existsOverlapping(apartmentId, dateFrom, dateTo)) {
            throw new BookingConflictException(apartmentId, dateFrom, dateTo);
        }
        return bookingRepository.save(
                new Booking(null, apartmentId, clientName.trim(), dateFrom, dateTo, "ACTIVE"));
    }
}
