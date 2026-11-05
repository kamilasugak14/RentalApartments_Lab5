package com.example.rentalapartments.repository;

import com.example.rentalapartments.exception.ApartmentNotFoundException;
import com.example.rentalapartments.model.Apartment;
import com.example.rentalapartments.model.SearchCriteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTemplateApartmentRepository implements ApartmentRepository {

    private static final String SELECT =
            "SELECT id, title, description, city, rooms, area, price FROM apartments";

    private static final RowMapper<Apartment> ROW_MAPPER = (rs, rowNum) -> new Apartment(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("city"),
            rs.getInt("rooms"),
            rs.getDouble("area"),
            rs.getInt("price"));

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateApartmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Apartment> findAll() {
        return jdbcTemplate.query(SELECT + " ORDER BY id", ROW_MAPPER);
    }

    @Override
    public Optional<Apartment> findById(Long id) {
        List<Apartment> result = jdbcTemplate.query(SELECT + " WHERE id = ?", ROW_MAPPER, id);
        return result.stream().findFirst();
    }

    @Override
    public List<Apartment> search(SearchCriteria c) {
        StringBuilder sql = new StringBuilder(SELECT + " WHERE 1 = 1");
        List<Object> params = new ArrayList<>();

        if (c.getCity() != null && !c.getCity().isBlank()) {
            sql.append(" AND LOWER(city) = LOWER(?)");
            params.add(c.getCity().trim());
        }
        if (c.getRooms() != null) {
            sql.append(" AND rooms = ?");
            params.add(c.getRooms());
        }
        if (c.getMaxPrice() != null) {
            sql.append(" AND price <= ?");
            params.add(c.getMaxPrice());
        }
        if (c.getMinArea() != null) {
            sql.append(" AND area >= ?");
            params.add(c.getMinArea());
        }
        sql.append(" ORDER BY id");

        return jdbcTemplate.query(sql.toString(), ROW_MAPPER, params.toArray());
    }

    @Override
    public Apartment save(Apartment a) {
        if (a.getId() == null) {
            return create(a);
        }
        int updated = jdbcTemplate.update(
                "UPDATE apartments SET title = ?, description = ?, city = ?, "
                        + "rooms = ?, area = ?, price = ? WHERE id = ?",
                a.getTitle(), a.getDescription(), a.getCity(),
                a.getRooms(), a.getArea(), a.getPrice(), a.getId());
        if (updated == 0) {
            throw new ApartmentNotFoundException(a.getId());
        }
        return a;
    }

    private Apartment create(Apartment a) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO apartments (title, description, city, rooms, area, price) "
                            + "VALUES (?, ?, ?, ?, ?, ?)",
                    new String[]{"id"});
            ps.setString(1, a.getTitle());
            ps.setString(2, a.getDescription());
            ps.setString(3, a.getCity());
            ps.setInt(4, a.getRooms());
            ps.setDouble(5, a.getArea());
            ps.setInt(6, a.getPrice());
            return ps;
        }, keyHolder);
        a.setId(keyHolder.getKey().longValue());
        return a;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM apartments WHERE id = ?", id);
    }
}
