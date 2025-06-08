package com.example.casestudy.Service;

import com.example.casestudy.model.Room;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface RoomService {
    Room getRoomById(int roomId) throws SQLException;
    List<Room> getAllRooms() throws SQLException;
    List<Room> getAllAvailableRoomsByDate(Date checkIn,Date checkOut)  throws SQLException;
    List<Room> searchRooms(String destination, Date checkIn, Date checkOut, int guests) throws SQLException;
    boolean addRoom(Room room)  throws SQLException;
    boolean updateRoom(Room room)  throws SQLException;
    boolean deleteRoom(int roomId)   throws SQLException;
    boolean isRoomAvailable(int roomId,  Date checkInDate, Date checkOutDate)  throws SQLException;

}
