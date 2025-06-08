package com.example.casestudy.Service;

import com.example.casestudy.model.Booking;

import java.sql.SQLException;
import java.util.List;

public interface BookingService {
    boolean createBooking(Booking booking) throws SQLException;
    Booking getBookingById(int bookingId) throws SQLException;
    List<Booking> getBookingByGuestEmail(String guestEmail) throws SQLException;
    List<Booking> getBookingByRoomId(int roomId) throws SQLException;
    List<Booking> getAllBookings() throws SQLException;
    boolean updateBooking(Booking booking) throws SQLException;
    boolean updateBookingStatus(int bookingId, String status) throws SQLException;
    void deleteBookingById(int bookingId) throws SQLException;
}
