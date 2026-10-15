package com.example.rentalapartments.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "Дані квартири, що передаються клієнту або надходять від нього")
public class ApartmentDto {

    @Schema(description = "Ідентифікатор квартири (генерується сервером)", example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Назва не може бути порожньою")
    @Schema(description = "Назва оголошення", example = "Затишна однокімнатна")
    private String title;

    @Schema(description = "Опис квартири", example = "Біля метро, з ремонтом")
    private String description;

    @NotBlank(message = "Місто не може бути порожнім")
    @Schema(description = "Місто, у якому розташована квартира", example = "Київ")
    private String city;

    @Min(value = 1, message = "Кількість кімнат має бути не менше 1")
    @Schema(description = "Кількість кімнат", example = "2")
    private int rooms;

    @Positive(message = "Площа має бути більшою за 0")
    @Schema(description = "Площа квартири, м²", example = "62.0")
    private double area;

    @Positive(message = "Ціна має бути більшою за 0")
    @Schema(description = "Ціна оренди, грн/міс", example = "22000")
    private int price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public int getRooms() { return rooms; }
    public void setRooms(int rooms) { this.rooms = rooms; }
    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}
