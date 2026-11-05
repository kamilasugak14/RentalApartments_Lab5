package com.example.rentalapartments.model;

import java.time.LocalDate;

public class Booking {
    private Long id;
    private Long apartmentId;
    private String clientName;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String status = "ACTIVE";

    public Booking() {}

    public Booking(Long id, Long apartmentId, String clientName,
                   LocalDate dateFrom, LocalDate dateTo, String status) {
        this.id = id;
        this.apartmentId = apartmentId;
        this.clientName = clientName;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getApartmentId() { return apartmentId; }
    public void setApartmentId(Long apartmentId) { this.apartmentId = apartmentId; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public LocalDate getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDate dateFrom) { this.dateFrom = dateFrom; }
    public LocalDate getDateTo() { return dateTo; }
    public void setDateTo(LocalDate dateTo) { this.dateTo = dateTo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
