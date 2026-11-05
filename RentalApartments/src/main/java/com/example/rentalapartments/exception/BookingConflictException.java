package com.example.rentalapartments.exception;

import java.time.LocalDate;

public class BookingConflictException extends RuntimeException {
    public BookingConflictException(Long apartmentId, LocalDate dateFrom, LocalDate dateTo) {
        super("Квартира id=" + apartmentId + " вже заброньована на період "
                + dateFrom + " – " + dateTo);
    }
}
