package com.example.rentalapartments.service;

import java.util.List;

import com.example.rentalapartments.model.Apartment;
import com.example.rentalapartments.model.SearchCriteria;

public interface ApartmentService {

	// ---- сценарій клієнта (сторінка пошуку) ----
	List<Apartment> search(SearchCriteria c);

	// ---- REST: фільтрація + пагінація ----
	List<Apartment> search(SearchCriteria c, int page, int size);

	long count(SearchCriteria c);

	// ---- сценарії хазяїна / CRUD ----
	List<Apartment> findAll();

	Apartment findById(Long id);

	Apartment save(Apartment apartment);

	void delete(Long id);

}