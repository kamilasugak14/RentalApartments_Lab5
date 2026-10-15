package com.example.rentalapartments.mapper;

import com.example.rentalapartments.dto.ApartmentDto;
import com.example.rentalapartments.model.Apartment;

public class ApartmentMapper {

    private ApartmentMapper() {}

    public static ApartmentDto toDto(Apartment a) {
        ApartmentDto dto = new ApartmentDto();
        dto.setId(a.getId());
        dto.setTitle(a.getTitle());
        dto.setDescription(a.getDescription());
        dto.setCity(a.getCity());
        dto.setRooms(a.getRooms());
        dto.setArea(a.getArea());
        dto.setPrice(a.getPrice());
        return dto;
    }

    public static Apartment toEntity(ApartmentDto dto) {
        Apartment a = new Apartment();
        a.setTitle(dto.getTitle());
        a.setDescription(dto.getDescription());
        a.setCity(dto.getCity());
        a.setRooms(dto.getRooms());
        a.setArea(dto.getArea());
        a.setPrice(dto.getPrice());
        return a;
    }
}
