package com.example.rentalapartments.model;

public class Apartment {
    private Long id;
    private String title;
    private String description;
    private String city;
    private int rooms;
    private double area;
    private int price;

    public Apartment() {}

    public Apartment(Long id, String title, String description,
                     String city, int rooms, double area, int price) {
        this.id = id; this.title = title; this.description = description;
        this.city = city; this.rooms = rooms; this.area = area; this.price = price;
    }

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