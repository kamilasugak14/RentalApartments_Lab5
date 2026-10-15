package com.example.rentalapartments.service;

import com.example.rentalapartments.model.Apartment;
import org.springframework.stereotype.Component;

@Component
public class ApartmentValidator {
    public void validate(Apartment a) {
        if (a.getTitle() == null || a.getTitle().isBlank())
            throw new IllegalArgumentException("Назва не може бути порожньою");
        if (a.getPrice() <= 0 || a.getArea() <= 0 || a.getRooms() <= 0)
            throw new IllegalArgumentException("Ціна, площа і кількість кімнат мають бути > 0");
    }
}