package com.example.rentalapartments.model;

public class SearchCriteria {
    private String city;
    private Integer rooms;
    private Integer maxPrice;
    private Double minArea;

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Integer getRooms() { return rooms; }
    public void setRooms(Integer rooms) { this.rooms = rooms; }
    public Integer getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Integer maxPrice) { this.maxPrice = maxPrice; }
    public Double getMinArea() { return minArea; }
    public void setMinArea(Double minArea) { this.minArea = minArea; }

    @Override
    public String toString() {
        return "city=" + city + ", rooms=" + rooms + ", maxPrice=" + maxPrice + ", minArea=" + minArea;
    }
}
