package com.example.rentalapartments.repository;

import com.example.rentalapartments.exception.BookingNotFoundException;
import com.example.rentalapartments.model.Booking;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcClientBookingRepository implements BookingRepository {

    private static final String SELECT =
            "SELECT id, apartment_id, client_name, date_from, date_to, status FROM bookings";

    private final JdbcClient jdbcClient;

    public JdbcClientBookingRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Booking> findAll() {
        return jdbcClient.sql(SELECT + " ORDER BY id")
                .query(Booking.class)
                .list();
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return jdbcClient.sql(SELECT + " WHERE id = :id")
                .param("id", id)
                .query(Booking.class)
                .optional();
    }

    @Override
    public List<Booking> findByClientName(String clientName) {
        return jdbcClient.sql(SELECT + " WHERE LOWER(client_name) LIKE LOWER(:name) ORDER BY id")
                .param("name", "%" + clientName.trim() + "%")
                .query(Booking.class)
                .list();
    }

    @Override
    public List<Booking> findByApartmentId(Long apartmentId) {
        return jdbcClient.sql(SELECT + " WHERE apartment_id = :apartmentId ORDER BY id")
                .param("apartmentId", apartmentId)
                .query(Booking.class)
                .list();
    }

    @Override
    public boolean existsOverlapping(Long apartmentId, LocalDate dateFrom, LocalDate dateTo) {
        Long count = jdbcClient.sql(
                        "SELECT count(*) FROM bookings WHERE apartment_id = :apartmentId "
                                + "AND status = 'ACTIVE' AND date_from < :dateTo AND date_to > :dateFrom")
                .param("apartmentId", apartmentId)
                .param("dateFrom", dateFrom)
                .param("dateTo", dateTo)
                .query(Long.class)
                .single();
        return count > 0;
    }

    @Override
    public Booking save(Booking b) {
        return b.getId() == null ? create(b) : update(b);
    }

    private Booking create(Booking b) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("INSERT INTO bookings (apartment_id, client_name, date_from, date_to, status) "
                        + "VALUES (:apartmentId, :clientName, :dateFrom, :dateTo, :status)")
                .paramSource(b)
                .update(keyHolder, "id");
        b.setId(keyHolder.getKey().longValue());
        return b;
    }

    private Booking update(Booking b) {
        int updated = jdbcClient.sql("UPDATE bookings SET apartment_id = :apartmentId, "
                        + "client_name = :clientName, date_from = :dateFrom, date_to = :dateTo, "
                        + "status = :status WHERE id = :id")
                .paramSource(b)
                .update();
        if (updated == 0) {
            throw new BookingNotFoundException(b.getId());
        }
        return b;
    }

    @Override
    public boolean deleteById(Long id) {
        return jdbcClient.sql("DELETE FROM bookings WHERE id = :id")
                .param("id", id)
                .update() > 0;
    }
}
