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
    private int capacity; // <-- THUỘC TÍNH MỚI

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

    public Room(int roomId, String roomNumber, String roomType, BigDecimal pricePerNight, String description, String imageUrl, boolean available, int capacity) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.description = description;
        this.imageUrl = imageUrl;
        this.available = available;
        this.capacity = capacity; // Gán giá trị capacity
    }

    // Constructor để tạo phòng mới (không có roomId, có capacity)
    public Room(String roomNumber, String roomType, BigDecimal pricePerNight, String description, String imageUrl, boolean available, int capacity) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.description = description;
        this.imageUrl = imageUrl;
        this.available = available;
        this.capacity = capacity;
    }


    // Getters and Setters
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                ", roomNumber='" + roomNumber + '\'' +
                ", roomType='" + roomType + '\'' +
                ", pricePerNight=" + pricePerNight +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", available=" + available +
                ", capacity=" + capacity +
                '}';
    }
}