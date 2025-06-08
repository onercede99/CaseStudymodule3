package com.example.casestudy.model;

import java.math.BigDecimal;

public class Room {
    private int roomId;
    private String roomNumber;
    private String roomType;
    private BigDecimal pricePerNight;
    private String description;
    private String imageUrl;
    private boolean available;

    public Room() {
    }

    public Room(String roomNumber, String roomType, int roomId, BigDecimal pricePerNight, String description, String imageUrl, boolean available) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.roomId = roomId;
        this.pricePerNight = pricePerNight;
        this.description = description;
        this.imageUrl = imageUrl;
        this.available = available;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

}
