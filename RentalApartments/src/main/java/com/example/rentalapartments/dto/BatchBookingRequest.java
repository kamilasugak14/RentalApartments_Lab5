package com.example.rentalapartments.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Запит на одночасне бронювання кількох квартир. "
        + "Виконується в одній транзакції: або бронюються всі квартири, або жодна.")
public class BatchBookingRequest {

    @NotBlank(message = "Ім’я клієнта не може бути порожнім")
    @Schema(description = "Ім’я клієнта", example = "Марія Іваненко")
    private String clientName;

    @NotEmpty(message = "Потрібно вказати хоча б одну квартиру")
    @Schema(description = "Ідентифікатори квартир, які бронюють", example = "[4, 5]")
    private List<Long> apartmentIds;

    @NotNull(message = "Потрібно вказати дату початку")
    @Schema(description = "Дата початку бронювання (формат РРРР-ММ-ДД)", example = "2026-12-15")
    private LocalDate dateFrom;

    @NotNull(message = "Потрібно вказати дату завершення")
    @Schema(description = "Дата завершення бронювання (формат РРРР-ММ-ДД)", example = "2026-12-20")
    private LocalDate dateTo;

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public List<Long> getApartmentIds() { return apartmentIds; }
    public void setApartmentIds(List<Long> apartmentIds) { this.apartmentIds = apartmentIds; }
    public LocalDate getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDate dateFrom) { this.dateFrom = dateFrom; }
    public LocalDate getDateTo() { return dateTo; }
    public void setDateTo(LocalDate dateTo) { this.dateTo = dateTo; }
}
