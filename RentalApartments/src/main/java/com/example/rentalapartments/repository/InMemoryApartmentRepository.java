package com.example.rentalapartments.repository;

import com.example.rentalapartments.model.Apartment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryApartmentRepository implements ApartmentRepository {

    private final Map<Long, Apartment> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public InMemoryApartmentRepository() {
        save(new Apartment(null, "Затишна однокімнатна", "Біля метро, з ремонтом", "Київ", 1, 38.5, 12000));
        save(new Apartment(null, "Простора двокімнатна", "Центр, меблі та техніка", "Київ", 2, 62.0, 22000));
        save(new Apartment(null, "Трикімнатна для родини", "Тихий двір, поруч школа", "Львів", 3, 80.0, 18000));
    }

    @Override
    public List<Apartment> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Apartment> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Apartment save(Apartment apartment) {
        if (apartment.getId() == null) {
            apartment.setId(sequence.incrementAndGet());
        }
        storage.put(apartment.getId(), apartment);
        return apartment;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}