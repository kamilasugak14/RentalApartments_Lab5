package com.example.rentalapartments.exception;

public class ApartmentNotFoundException extends RuntimeException {
    public ApartmentNotFoundException(Long id) {
        super("Квартиру не знайдено: id=" + id);
    }
}
