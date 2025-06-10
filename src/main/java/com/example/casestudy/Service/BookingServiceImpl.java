package com.example.casestudy.Service;

import com.example.casestudy.model.Booking;
import com.example.casestudy.model.Room;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BookingServiceImpl implements BookingService {
    private RoomService roomService;

    public void setRoomService(RoomService roomService) {
        System.out.println("BookingServiceImpl: setRoomService called with " + (roomService != null ? "valid RoomService" : "NULL RoomService"));
        this.roomService = roomService;
    }

    public BookingServiceImpl() {
        System.out.println("BookingServiceImpl: Constructor called.");
    }

    @Override
    public boolean createBooking(Booking booking) throws SQLException {
        if(!this.roomService.isRoomAvailable(booking.getRoomId(),booking.getCheckInDate(),booking.getCheckOutDate())){
            System.err.println("Attempt to book an unavailable room or overlapping dates for room_id: " + booking.getRoomId());
            return false;
        }
        String sql = "insert into bookings (room_id, guest_name, guest_email, check_in_date, check_out_date, total_price, status)" +  "values (?, ?, ?, ?, ?, ?, ?)";
        try(Connection conn = DatabaseUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setInt(1, booking.getRoomId());
            stmt.setString(2, booking.getGuestName());
            stmt.setString(3, booking.getGuestEmail());
            stmt.setDate(4, new java.sql.Date(booking.getCheckInDate().getTime()));
            stmt.setDate(5, new java.sql.Date(booking.getCheckOutDate().getTime()));
            stmt.setBigDecimal(6, booking.getTotalPrice());
            stmt.setString(7, booking.getStatus() != null ? booking.getStatus() : "confirmed");
            int affectedRows = stmt.executeUpdate();
            if(affectedRows > 0){
                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if(rs.next()){
                        booking.setBookingId(rs.getInt(1));
                    } else {
                        System.err.println("Booking created but failed to retrieve generated ID.");
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Booking getBookingById(int bookingId) throws SQLException {
        Booking booking = null;
        String sql = "select * from bookings where booking_id = ?";
        try(Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, bookingId);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    booking = mapResultSetToBooking(rs);
                    Room room  = roomService.getRoomById((booking.getRoomId()));
                    booking.setRoom(room);
                }
            }
        }
        return booking;
    }

    @Override
    public List<Booking> getBookingByGuestEmail(String guestEmail) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "select * from bookings where guest_email = ? order by check_in_date desc";
        try(Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, guestEmail);
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    Booking booking = mapResultSetToBooking(rs);
                    Room room  = roomService.getRoomById((booking.getRoomId()));
                    booking.setRoom(room);
                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }

    @Override
    public List<Booking> getBookingByRoomId(int roomId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "select * from bookings where room_id = ? order by check_in_date desc";
        try(Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, roomId);
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        }
        return bookings;
    }

    @Override
    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "select * from bookings order by check_in_date desc";
        try(Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                Booking booking = mapResultSetToBooking(rs);
                Room room  = roomService.getRoomById((booking.getRoomId()));
                booking.setRoom(room);
                bookings.add(booking);
            }
        }
        return bookings;
    }

    @Override
    public boolean updateBooking(Booking booking) throws SQLException {
        String sql = "UPDATE bookings SET guest_name = ?, guest_email = ?, check_in_date = ?, check_out_date = ?, total_price = ?, status = ? " +
                "WHERE booking_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, booking.getGuestName());
            stmt.setString(2, booking.getGuestEmail());

            java.util.Date utilCheckInDate = booking.getCheckInDate();
            java.sql.Date sqlCheckInDate = null;
            if (utilCheckInDate != null) {
                sqlCheckInDate = new java.sql.Date(utilCheckInDate.getTime());
            }
            stmt.setDate(3, sqlCheckInDate);

            java.util.Date utilCheckOutDate = booking.getCheckOutDate();
            java.sql.Date sqlCheckOutDate = null;
            if (utilCheckOutDate != null) {
                sqlCheckOutDate = new java.sql.Date(utilCheckOutDate.getTime());
            }
            stmt.setDate(4, sqlCheckOutDate);

            stmt.setBigDecimal(5, booking.getTotalPrice());
            String statusToSet = booking.getStatus();
            if (statusToSet == null || statusToSet.trim().isEmpty()) {
                statusToSet = "CONFIRMED";
            }
            stmt.setString(6, statusToSet);

            stmt.setInt(7, booking.getBookingId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateBookingStatus(int bookingId, String status) throws SQLException {
        String sql = "update bookings set status = ? where booking_id = ?";
        try(Connection conn = DatabaseUtil.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setRoomId(rs.getInt("room_id"));
        booking.setGuestName(rs.getString("guest_name"));
        booking.setGuestEmail(rs.getString("guest_email"));
        booking.setCheckInDate(rs.getDate("check_in_date"));
        booking.setCheckOutDate(rs.getDate("check_out_date"));
        booking.setTotalPrice(rs.getBigDecimal("total_price"));
        booking.setBookingDate(rs.getTimestamp("booking_date"));
        booking.setStatus(rs.getString("status"));
        return booking;
    }
    public static long calculateNights(LocalDate checkIn, LocalDate checkOut){
        if(checkIn == null || checkOut == null ){
            System.err.println("calculateNights: Check-in or check-out date is null.");
            return 0;
        }
        if (checkOut.isBefore(checkIn)) {
            System.err.println("calculateNights: Check-out date is before check-in date.");
            return 0;
        }
        if (checkOut.isEqual(checkIn)) {
            return 1;
        }
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    @Override
    public void deleteBookingById(int bookingId) throws SQLException {
        String sql = "DELETE FROM bookings WHERE booking_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Đã xóa thành công đặt phòng với ID: " + bookingId);
            } else {
                System.out.println("Không tìm thấy đặt phòng nào với ID: " + bookingId + " để xóa, hoặc xóa không thành công.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa đặt phòng: " + e.getMessage());
            throw e;
        }
    }
    @Override
    public boolean checkRoomCurrentlyBooked(int roomId, java.util.Date todayUtil) throws SQLException {
        if (todayUtil == null) {
            return false;
        }
        java.sql.Date todaySql = new java.sql.Date(todayUtil.getTime());

        String sql = "SELECT COUNT(*) FROM bookings " +
                "WHERE room_id = ? " +
                "AND status NOT IN ('CANCELLED', 'CHECKED_OUT') " +
                "AND ? >= check_in_date " +
                "AND ? < check_out_date";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setDate(2, todaySql);
            stmt.setDate(3, todaySql);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
