package com.example.rentalapartments.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long id) {
        super("Бронювання не знайдено: id=" + id);
    }
}
