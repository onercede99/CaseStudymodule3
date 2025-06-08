package com.example.casestudy.Service;

import com.example.casestudy.model.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public  class RoomServiceImpl implements RoomService {
    @Override
    public Room getRoomById(int roomId) throws SQLException {
        Room room = null;
        String sql = "select * from rooms where room_id = ?";
        try(Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, roomId);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    room = mapResultSetToRoom(rs);
                }
            }
        }
        return room;
    }

    @Override
    public List<Room> getAllAvailableRoomsByDate(Date checkIn, Date checkOut) throws SQLException {
        List<Room> availableRooms = new ArrayList<>();
        String sql = "select r.* from rooms r " + "where r.is_available = true and r.room_id not in (" + "select b.room_id from bookings b " +
                "where b.status <> 'cancelled' and " + "(b.check_in_date < ? and b.check_out_date >?)" + ") order by r.price_per_night";
        try(Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setDate(1, (java.sql.Date) checkOut);
            stmt.setDate(2, (java.sql.Date) checkIn);
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    availableRooms.add(mapResultSetToRoom(rs));
                }
            }
        }
        return availableRooms;
    }

    @Override
    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "select * from rooms ORDER BY room_number";
        try(Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                rooms.add(mapResultSetToRoom(rs));
            }
        }
        return rooms;
    }

    @Override
    public List<Room> searchRooms(String destination, Date checkIn, Date checkOut, int guests) throws SQLException {
        List<Room> allAvailableForDates = getAllAvailableRoomsByDate(checkIn, checkOut);
        List<Room> result = new ArrayList<>();
        String destLower =  null;
        if (destination!=null && destination.trim().isEmpty()){
            destLower = destination.toLowerCase().trim();
        }
        for(Room room : allAvailableForDates){
            boolean matchesDestination = true;
            if (destination != null){
                matchesDestination = true;
            }
            if (!matchesDestination & room.getDescription() != null && room.getDescription().toLowerCase().contains(destLower )){
                matchesDestination = true;
            }
            if (!matchesDestination & "khách sạn".contains(destLower )){
                if(destination.equals("khách sạn")){
                    matchesDestination = true;
                }
                if(matchesDestination){
                    result.add(room);
                }
            }
        }
        return result;
    }

    @Override
    public boolean addRoom(Room room) throws SQLException {
        String sql = "insert into rooms (room_number, room_type, price_per_night, description, image_url, is_available) values (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType());
            stmt.setBigDecimal(3, room.getPricePerNight());
            stmt.setString(4, room.getDescription());
            stmt.setString(5, room.getImageUrl());
            stmt.setBoolean(6, room.isAvailable());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateRoom(Room room) throws SQLException {
            String sql = "update rooms set room_number = ?, room_type = ?, price_per_night = ?, description = ?, image_url = ?, is_available = ? where room_id = ?";
            try(Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, room.getRoomNumber());
                stmt.setString(2, room.getRoomType());
                stmt.setBigDecimal(3, room.getPricePerNight());
                stmt.setString(4, room.getDescription());
                stmt.setString(5, room.getImageUrl());
                stmt.setBoolean(6, room.isAvailable());
                stmt.setInt(7, room.getRoomId());
                return stmt.executeUpdate() > 0;
            }
    }

    @Override
    public boolean deleteRoom(int roomId) throws SQLException {
        String checkBookingSql = "select count(*) from bookings where room_id = ? and status <> 'cancelled' and check_out_date >= curdate()";
        try(Connection conn = DatabaseUtil.getConnection();
        PreparedStatement checkStmt = conn.prepareStatement(checkBookingSql)){
            checkStmt.setInt(1, roomId);
            try(ResultSet rs = checkStmt.executeQuery()){
                if(rs.next() && rs.getInt(1) > 0){
                    throw new SQLException("Không thể xóa phòng. Phòng này có các đặt phòng chưa hoàn thành.");
                }
            }
        }
        String sql = "delete from rooms where room_id = ?";
        try(Connection conn = DatabaseUtil.getConnection();
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
        try (Connection conn = DatabaseUtil.getConnection();
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
        return room;
    }
}
