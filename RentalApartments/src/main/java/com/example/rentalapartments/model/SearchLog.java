package com.example.rentalapartments.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class SearchLog {
    private final String id = UUID.randomUUID().toString().substring(0, 8);
    private final LocalDateTime createdAt = LocalDateTime.now();

    public String getId() { return id; }

    public void print(SearchCriteria criteria, int found) {
        System.out.println("[SearchLog " + id + " @ " + createdAt + "] "
                + criteria + " -> found " + found);
    }
}
