package com.example.casestudy.controller;

import com.example.casestudy.Service.BookingServiceImpl;
import com.example.casestudy.Service.RoomService;
import com.example.casestudy.Service.RoomServiceImpl;
import com.example.casestudy.model.Room;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@WebServlet(name="RoomListServlet", urlPatterns="/room_list")
public class RoomListServlet extends HttpServlet {
    private RoomService roomService;

    @Override
    public void init(){
        System.out.println("RoomListServlet: init() called."); // Thêm log để biết init có chạy không

        BookingServiceImpl tempBookingService = new BookingServiceImpl();
        RoomServiceImpl tempRoomService = new RoomServiceImpl();

        System.out.println("RoomListServlet: Setting RoomService into BookingService...");
        tempBookingService.setRoomService(tempRoomService);

        System.out.println("RoomListServlet: Setting BookingService into RoomService...");
        tempRoomService.setBookingService(tempBookingService); // << DÒNG QUAN TRỌNG NHẤT CHO LỖI NÀY

        this.roomService = tempRoomService;

        if (this.roomService != null) {
            System.out.println("RoomListServlet: this.roomService initialized.");

        } else {
            System.err.println("RoomListServlet: FAILED to initialize this.roomService.");
        }    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String destinationParam = request.getParameter("destination");
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        String guestsStr = request.getParameter("guests");

        System.out.println("RoomListServlet - doGet - destinationParam: " + destinationParam);
        System.out.println("RoomListServlet - doGet - checkInStr: " + checkInStr);
        System.out.println("RoomListServlet - doGet - checkOutStr: " + checkOutStr);
        System.out.println("RoomListServlet - doGet - guestsStr: " + guestsStr);

        Date checkInDate = null;
        Date checkOutDate = null;
        int numberOfGuests = 2;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        try {
            if (checkInStr != null && !checkInStr.trim().isEmpty()) {
                checkInDate = sdf.parse(checkInStr.trim());
            }
            if (checkOutStr != null && !checkOutStr.trim().isEmpty()) {
                checkOutDate = sdf.parse(checkOutStr.trim());
            }

            if (checkInDate != null && checkOutDate != null && checkInDate.after(checkOutDate)) {
                request.setAttribute("errorMessage", "Ngày nhận phòng không được sau ngày trả phòng.");
                forwardToRoomListPage(request, response, new ArrayList<>()); // Gửi danh sách rỗng
                return;
            }

            if (guestsStr != null && !guestsStr.trim().isEmpty()) {
                try {
                    String numericGuestsStr = guestsStr.replaceAll("\\D+", "");
                    if (!numericGuestsStr.isEmpty()) {
                        numberOfGuests = Integer.parseInt(numericGuestsStr);
                        if (numberOfGuests <= 0) {
                            numberOfGuests = 1;
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Lỗi parse số khách: " + guestsStr + ". Sử dụng giá trị mặc định.");
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Định dạng ngày không hợp lệ. Vui lòng sử dụng yyyy-MM-dd.");

            try {
                List<Room> allRooms = roomService.getAllRooms();
                forwardToRoomListPage(request, response, allRooms);
            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
                request.setAttribute("errorMessage", "Lỗi truy vấn cơ sở dữ liệu khi lấy danh sách phòng.");
                forwardToRoomListPage(request, response, new ArrayList<>());
            }
            return;
        }

        List<Room> rooms = new ArrayList<>();
        try {
            System.out.println("RoomListServlet - Calling searchRooms with destination: " + destinationParam +
                    ", checkIn: " + checkInDate + ", checkOut: " + checkOutDate + ", guests: " + numberOfGuests);

            if (destinationParam != null || checkInDate != null || checkOutDate != null ) {
                rooms = roomService.searchRooms(destinationParam, checkInDate, checkOutDate, numberOfGuests);
                System.out.println("RoomListServlet - Number of rooms retrieved from service: " + (rooms != null ? rooms.size() : "null list"));
            } else {
                rooms = roomService.getAllRooms();
                request.setAttribute("message", "Hiển thị tất cả các phòng có sẵn. Vui lòng sử dụng bộ lọc để tìm kiếm chi tiết hơn.");
            }

            if (rooms.isEmpty() && (destinationParam != null || checkInDate != null || checkOutDate != null)) {
                request.setAttribute("warningMessage", "Không tìm thấy phòng nào phù hợp với tiêu chí tìm kiếm của bạn.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi tìm kiếm phòng: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi tham số tìm kiếm: " + e.getMessage());
        }
        System.out.println("RoomListServlet - Number of rooms to forward: " + (rooms != null ? rooms.size() : "null list"));
        if (rooms != null && !rooms.isEmpty()) {
            for (Room r : rooms) {
                System.out.println("RoomListServlet - Forwarding room: ID=" + r.getRoomId() + ", Type=" + r.getRoomType() + ", Number=" + r.getRoomNumber() + ", ImageURL=[" + r.getImageUrl() + "]");
            }
        }

        request.setAttribute("paramDestination", destinationParam);
        request.setAttribute("paramCheckIn", checkInStr);
        request.setAttribute("paramCheckOut", checkOutStr);
        request.setAttribute("paramGuests", guestsStr != null ? guestsStr : String.valueOf(numberOfGuests));

        if (rooms != null && !rooms.isEmpty()) {
            for (Room r : rooms) {
                System.out.println("RoomListServlet - Forwarding room: ID=" + r.getRoomId() +
                        ", ImageURL=[" + r.getImageUrl() + "]");
            }
        }

        forwardToRoomListPage(request, response, rooms);
    }

    private void forwardToRoomListPage(HttpServletRequest request, HttpServletResponse response, List<Room> rooms)
            throws ServletException, IOException {
        System.out.println("RoomListServlet - forwardToRoomListPage - Setting listRoom attribute with size: " + (rooms != null ? rooms.size() : "null"));
        request.setAttribute("listRoom", rooms);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/room_list.jsp");
        dispatcher.forward(request, response);
    }
}