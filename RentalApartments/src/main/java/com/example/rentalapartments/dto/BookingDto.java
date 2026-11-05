package com.example.rentalapartments.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Schema(description = "Дані бронювання квартири")
public class BookingDto {

    @Schema(description = "Ідентифікатор бронювання (генерується сервером)", example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Потрібно вказати квартиру")
    @Positive(message = "Ідентифікатор квартири має бути додатним")
    @Schema(description = "Ідентифікатор квартири, яку бронюють", example = "4")
    private Long apartmentId;

    @NotBlank(message = "Ім’я клієнта не може бути порожнім")
    @Schema(description = "Ім’я клієнта", example = "Марія Іваненко")
    private String clientName;

    @NotNull(message = "Потрібно вказати дату початку")
    @Schema(description = "Дата початку бронювання (формат РРРР-ММ-ДД)", example = "2026-12-15")
    private LocalDate dateFrom;

    @NotNull(message = "Потрібно вказати дату завершення")
    @Schema(description = "Дата завершення бронювання (формат РРРР-ММ-ДД)", example = "2026-12-20")
    private LocalDate dateTo;

    @Schema(description = "Статус бронювання: ACTIVE або CANCELLED", example = "ACTIVE",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String status;

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
