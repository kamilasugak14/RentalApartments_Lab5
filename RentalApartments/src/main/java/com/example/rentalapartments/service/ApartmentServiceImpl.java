package com.example.rentalapartments.service;

import com.example.rentalapartments.exception.ApartmentNotFoundException;
import com.example.rentalapartments.model.Apartment;
import com.example.rentalapartments.model.SearchCriteria;
import com.example.rentalapartments.model.SearchLog;
import com.example.rentalapartments.repository.ApartmentRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApartmentServiceImpl implements ApartmentService {

    private final ApartmentRepository repository;
    private final ObjectProvider<SearchLog> searchLogs;
    private ApartmentValidator validator;

    public ApartmentServiceImpl(ApartmentRepository repository, ObjectProvider<SearchLog> searchLogs) {
        this.repository = repository;
        this.searchLogs = searchLogs;
    }

    @Autowired
    public void setValidator(ApartmentValidator validator) {
        this.validator = validator;
    }

    // ---- сценарій клієнта (сторінка пошуку) ----
    @Override
	public List<Apartment> search(SearchCriteria c) {
        List<Apartment> result = filter(c);
        SearchLog log = searchLogs.getObject();
        log.print(c, result.size());
        return result;
    }

    // ---- REST: фільтрація + пагінація ----
    @Override
	public List<Apartment> search(SearchCriteria c, int page, int size) {
        List<Apartment> all = filter(c);
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        return all.subList(from, to);
    }

    @Override
	public long count(SearchCriteria c) {
        return filter(c).size();
    }

    private List<Apartment> filter(SearchCriteria c) {
        return repository.search(c);
    }

    // ---- сценарії хазяїна / CRUD ----
    @Override
	public List<Apartment> findAll() { return repository.findAll(); }

    @Override
	public Apartment findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApartmentNotFoundException(id));
    }

    @Override
	public Apartment save(Apartment apartment) {
        validator.validate(apartment);
        return repository.save(apartment);
    }

    @Override
	public void delete(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new ApartmentNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private boolean isEmpty(String s) { return s == null || s.isBlank(); }
}
