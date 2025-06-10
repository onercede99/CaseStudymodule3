//package com.example.casestudy.Service;
//
//public class ServiceManager {
//    private static RoomService roomServiceInstance;
//    private static BookingService bookingServiceInstance;
//
//    static {
//        RoomServiceImpl rs = new RoomServiceImpl();
//        BookingServiceImpl bs = new BookingServiceImpl();
//
//        rs.setBookingService(bs);
//        bs.setRoomService(rs);
//
//        roomServiceInstance = rs;
//        bookingServiceInstance = bs;
//    }
//
//    public static RoomService getRoomService() {
//        return roomServiceInstance;
//    }
//
//    public static BookingService getBookingService() {
//        return bookingServiceInstance;
//    }
//}
