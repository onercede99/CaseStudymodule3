package com.example.casestudy.Service;

import com.example.casestudy.model.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.casestudy.Service.DatabaseUtil.getConnection;

public  class RoomServiceImpl implements RoomService {
    private BookingService bookingService;

    public void setBookingService(BookingService bookingService) {
        System.out.println("RoomServiceImpl: setBookingService called with " + (bookingService != null ? "valid BookingService" : "NULL BookingService"));
        this.bookingService = bookingService;
    }

    public RoomServiceImpl() {
        System.out.println("RoomServiceImpl: Constructor called.");
    }

    @Override
    public Room getRoomById(int roomId) throws SQLException {
        Room room = null;
        String sql = "select * from rooms where room_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    room = mapResultSetToRoom(rs);
                }
            }
        }
        return room;
    }

    @Override
    public List<Room> getAllAvailableRoomsByDate(java.util.Date checkIn, java.util.Date checkOut) throws SQLException {
        List<Room> availableRooms = new ArrayList<>();
        String sql = "SELECT r.* FROM rooms r " +
                "LEFT JOIN bookings b ON r.room_id = b.room_id " +
                "AND b.status NOT IN ('CANCELLED', 'CHECKED_OUT') " +
                "AND b.check_in_date < ? " +
                "AND b.check_out_date > ? " +
                "WHERE r.is_available = TRUE AND b.booking_id IS NULL";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            java.sql.Date sqlSearchCheckOut = null;
            if (checkOut != null) {
                sqlSearchCheckOut = new java.sql.Date(checkOut.getTime());
            }

            java.sql.Date sqlSearchCheckIn = null;
            if (checkIn != null) {
                sqlSearchCheckIn = new java.sql.Date(checkIn.getTime());
            }
            stmt.setDate(1, sqlSearchCheckOut);
            stmt.setDate(2, sqlSearchCheckIn);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    availableRooms.add(mapResultSetToRoom(rs));
                }
            }
        }
        System.out.println("getAllAvailableRoomsByDate: Found " + availableRooms.size() + " rooms available between " + checkIn + " and " + checkOut);
        return availableRooms;
    }

    @Override
    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "select * from rooms ORDER BY room_number";
        System.out.println("RoomServiceImpl - getAllRooms - Executing SQL: " + sql);
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
                count++;
            }
            System.out.println("RoomServiceImpl - getAllRooms - Rows retrieved from DB: " + count);
        } catch (SQLException e) {
            System.err.println("RoomServiceImpl - getAllRooms - SQLException: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        System.out.println("RoomServiceImpl - getAllRooms - Returning rooms list with size: " + rooms.size());
        if (this.bookingService == null) {
            System.err.println("RoomServiceImpl.getAllRooms: FATAL - this.bookingService is NULL!");
            throw new SQLException("BookingService not initialized in RoomServiceImpl.");
        }
        if (rooms != null && !rooms.isEmpty()) {
            java.util.Date today = new java.util.Date();
            for (Room room : rooms) {
                boolean configuredAsAvailable = room.isAvailable();

                if (configuredAsAvailable) {
                    boolean isBookedToday = bookingService.checkRoomCurrentlyBooked(room.getRoomId(), today);
                    if (isBookedToday) {
                        System.out.println("RoomServiceImpl - Room ID: " + room.getRoomId() + " is booked today. Setting available to false.");
                        room.setAvailable(false);
                    } else {
                        System.out.println("RoomServiceImpl - Room ID: " + room.getRoomId() + " is NOT booked today. Keeping available as true.");
                        room.setAvailable(true);
                    }
                } else {
                    System.out.println("RoomServiceImpl - Room ID: " + room.getRoomId() + " is configured as not available. Keeping available as false.");
                }
            }
        }
        return rooms;
    }

    @Override
    public List<Room> searchRooms(String destination, java.util.Date checkIn, java.util.Date checkOut, int guests) throws SQLException {
        System.out.println("RoomServiceImpl - searchRooms called with: destination=" + destination +
                ", checkIn=" + checkIn + ", checkOut=" + checkOut + ", guests=" + guests);

        List<Room> roomsMatchingSearchCriteria;

        if (checkIn != null && checkOut != null) {
            roomsMatchingSearchCriteria = getAllAvailableRoomsByDate(checkIn, checkOut);
            System.out.println("searchRooms: Rooms available for date range [" + checkIn + " - " + checkOut + "]: " + roomsMatchingSearchCriteria.size());
        } else {
            roomsMatchingSearchCriteria = new ArrayList<>();
            String sqlAllConfiguredAvailable = "SELECT * FROM rooms WHERE is_available = TRUE ORDER BY room_number";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlAllConfiguredAvailable);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roomsMatchingSearchCriteria.add(mapResultSetToRoom(rs));
                }
            }
            System.out.println("searchRooms: All rooms initially configured as available: " + roomsMatchingSearchCriteria.size());
        }

        List<Room> finalFilteredRooms = new ArrayList<>();
        String destLower = null;
        if (destination != null && !destination.trim().isEmpty()) {
            destLower = destination.toLowerCase().trim();
        }

        for (Room room : roomsMatchingSearchCriteria) {
            boolean matchesDestination = true;
            if (destLower != null) {
                matchesDestination = false;
                if (room.getRoomNumber() != null && room.getRoomNumber().toLowerCase().contains(destLower)) {
                    matchesDestination = true;
                }
                if (!matchesDestination && room.getRoomType() != null && room.getRoomType().toLowerCase().contains(destLower)) {
                    matchesDestination = true;
                }
                if (!matchesDestination && room.getDescription() != null && room.getDescription().toLowerCase().contains(destLower)) {
                    matchesDestination = true;
                }
            }

            boolean matchesCapacity = (room.getCapacity() >= guests);

            if (matchesDestination && matchesCapacity) {
                finalFilteredRooms.add(room);
            }
        }
        System.out.println("searchRooms: Rooms after destination/capacity filter: " + finalFilteredRooms.size());

        if (finalFilteredRooms != null && !finalFilteredRooms.isEmpty()) {
            java.util.Date today = new java.util.Date();
            for (Room room : finalFilteredRooms) {
                boolean isCurrentlyConsideredAvailable = room.isAvailable();

                if (isCurrentlyConsideredAvailable) {
                    boolean isBookedToday = bookingService.checkRoomCurrentlyBooked(room.getRoomId(), today);
                    if (isBookedToday) {
                        System.out.println("RoomServiceImpl - searchRooms - Room ID: " + room.getRoomId() + " IS BOOKED TODAY. Setting display available to false.");
                        room.setAvailable(false);
                    } else {
                        System.out.println("RoomServiceImpl - searchRooms - Room ID: " + room.getRoomId() + " is NOT booked today. Keeping display available as true.");
                        room.setAvailable(true);
                    }
                } else {
                    System.out.println("RoomServiceImpl - searchRooms - Room ID: " + room.getRoomId() + " was already not available. Keeping display available as false.");
                }
            }
        }
        System.out.println("searchRooms: Final rooms with today's availability status for display: " + finalFilteredRooms.size());
        return finalFilteredRooms;

    }



    @Override
    public boolean addRoom(Room room) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, room_type, price_per_night, description, image_url, is_available, capacity) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType());
            stmt.setBigDecimal(3, room.getPricePerNight());
            stmt.setString(4, room.getDescription());
            stmt.setString(5, room.getImageUrl());
            stmt.setBoolean(6, room.isAvailable());
            stmt.setInt(7, room.getCapacity());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateRoom(Room room) throws SQLException {
        String sql = "UPDATE rooms SET room_number = ?, room_type = ?, price_per_night = ?, description = ?, image_url = ?, is_available = ?, capacity = ? WHERE room_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType());
            stmt.setBigDecimal(3, room.getPricePerNight());
            stmt.setString(4, room.getDescription());
            stmt.setString(5, room.getImageUrl());
            stmt.setBoolean(6, room.isAvailable());
            stmt.setInt(7, room.getCapacity());
            stmt.setInt(8, room.getRoomId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteRoom(int roomId) throws SQLException {
        String checkBookingSql = "select count(*) from bookings where room_id = ? and status <> 'cancelled' and check_out_date >= curdate()";
        try(Connection conn = getConnection();
        PreparedStatement checkStmt = conn.prepareStatement(checkBookingSql)){
            checkStmt.setInt(1, roomId);
            try(ResultSet rs = checkStmt.executeQuery()){
                if(rs.next() && rs.getInt(1) > 0){
                    throw new SQLException("Không thể xóa phòng. Phòng này có các đặt phòng chưa hoàn thành.");
                }
            }
        }
        String sql = "delete from rooms where room_id = ?";
        try(Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, roomId);
            return stmt.executeUpdate() > 0;
        }
    }


    @Override
    public boolean isRoomAvailable(int roomId, java.util.Date checkInDate, java.util.Date checkOutDate) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings b " +
                "WHERE b.room_id = ? " +
                "AND b.status NOT IN ('CANCELLED', 'CHECKED_OUT') " +
                "AND b.check_in_date < ? " +
                "AND b.check_out_date > ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);

            java.sql.Date sqlCheckOutDateForQuery = null;
            if (checkOutDate != null) {
                sqlCheckOutDateForQuery = new java.sql.Date(checkOutDate.getTime());
            }

            java.sql.Date sqlCheckInDateForQuery = null;
            if (checkInDate != null) {
                sqlCheckInDateForQuery = new java.sql.Date(checkInDate.getTime());
            }

            stmt.setDate(2, sqlCheckOutDateForQuery);
            stmt.setDate(3, sqlCheckInDateForQuery);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        }
        return false;
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomType(rs.getString("room_type"));
        room.setPricePerNight(rs.getBigDecimal("price_per_night"));
        room.setDescription(rs.getString("description"));
        room.setImageUrl(rs.getString("image_url"));
        room.setAvailable(rs.getBoolean("is_available"));
        room.setCapacity(rs.getInt("capacity"));
        return room;
    }

    @Override
    public boolean checkRoomNumberExists(String roomNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rooms WHERE room_number = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
