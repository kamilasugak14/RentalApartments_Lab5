package com.example.rentalapartments.repository;

import com.example.rentalapartments.model.Apartment;
import com.example.rentalapartments.model.SearchCriteria;

import java.util.List;
import java.util.Optional;

public interface ApartmentRepository {
    List<Apartment> findAll();
    Optional<Apartment> findById(Long id);
    List<Apartment> search(SearchCriteria criteria);
    Apartment save(Apartment apartment);
    void deleteById(Long id);
}