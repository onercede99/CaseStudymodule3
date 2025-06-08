package com.example.casestudy.controller;

import com.example.casestudy.Service.BookingService;
import com.example.casestudy.Service.BookingServiceImpl;
import com.example.casestudy.Service.RoomService;
import com.example.casestudy.Service.RoomServiceImpl;
import com.example.casestudy.model.Booking;
import com.example.casestudy.model.Room;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/booking")
public class BookingServlet extends HttpServlet {
    private RoomService roomService;
    private BookingService bookingService;

    @Override
    public void init() {
        roomService = new RoomServiceImpl();
        bookingService = new BookingServiceImpl();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String roomIdStr = request.getParameter("roomId");
        System.out.println("BookingServlet - doGet - Received roomIdStr: " + roomIdStr);

        if(roomIdStr == null || roomIdStr.isEmpty()){
            response.sendRedirect(request.getContextPath() + "/rooms");
            return;
        }
        try{
            int roomId = Integer.parseInt(roomIdStr);
            System.out.println("BookingServlet - doGet - Parsed roomId: " + roomId);
            Room room = roomService.getRoomById(roomId);
            if(room == null){
                System.out.println("BookingServlet - doGet - Room NOT found for ID: " + roomId);
                request.setAttribute("errorMessage", "Không tìm thấy phòng.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            System.out.println("BookingServlet - doGet - Room FOUND: " + room.getRoomType() + ", Price: " + room.getPricePerNight());
            String checkInParam = request.getParameter("checkInDate");
            String checkOutParam = request.getParameter("checkOutDate");
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);
            String defaultCheckIn = (checkInParam != null && !checkInParam.isEmpty()) ? checkInParam : today.toString();
            String defaultCheckOut = (checkOutParam != null && !checkOutParam.isEmpty()) ? checkOutParam : tomorrow.toString();
            request.setAttribute("room", room);
            request.setAttribute("checkInDate", defaultCheckIn);
            request.setAttribute("checkOutDate", defaultCheckOut);
            System.out.println("BookingServlet - doGet - 'room' attribute set.");
            request.getRequestDispatcher("/WEB-INF/views/booking_form.jsp").forward(request, response);
            return;
        } catch (NumberFormatException e){
            System.out.println("BookingServlet - doGet - Invalid Room ID format: " + roomIdStr);
            request.setAttribute("errorMessage", "ID Phòng không hợp lệ.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }catch (SQLException e) {
            System.out.println("BookingServlet - doGet - SQL Error: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            return;
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Room room = null;
        try {
            request.setCharacterEncoding("UTF-8");
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String guestName = request.getParameter("guestName");
            String guestEmail = request.getParameter("guestEmail");
            String checkInStr = request.getParameter("checkInDate");
            String checkOutStr = request.getParameter("checkOutDate");
            room = roomService.getRoomById(roomId);
            if(room == null){
                request.setAttribute("errorMessage", "Không tìm thầy phòng.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            if(guestName == null || guestName.trim().isEmpty() || checkInStr == null || checkOutStr == null || checkInStr.isEmpty()){
                request.setAttribute("errorMessage", "Vui lòng điền đầy đủ các thông tin bắt buộc (*).");
                request.setAttribute("room", room);
                request.setAttribute("guestName", guestName);
                request.setAttribute("guestEmail", guestEmail);
                request.setAttribute("checkInDate", checkInStr);
                request.setAttribute("checkOutDate", checkOutStr);
                request.getRequestDispatcher("/WEB-INF/views/booking_form.jsp").forward(request, response);
                return;
            }
            LocalDate checkInDateLocal;
            LocalDate checkOutDateLocal;
            try {
                checkInDateLocal = LocalDate.parse(checkInStr);
                checkOutDateLocal = LocalDate.parse(checkOutStr);
            } catch (DateTimeParseException e){
                request.setAttribute("errorMessage", "Định dạng ngày không hợp lệ vui lòng dùng YYYY-MM-DD.");
                request.setAttribute("room", room);
                request.getRequestDispatcher("/WEB-INF/views/booking_form.jsp").forward(request, response);
                return;
            }
            Date sqlCheckInDate = Date.valueOf(checkInDateLocal);
            Date sqlCheckOutDate = Date.valueOf(checkOutDateLocal);
            if(!roomService.isRoomAvailable(roomId, sqlCheckInDate, sqlCheckOutDate)){
                request.setAttribute("errorMessage", "Xin lỗi, phòng này không còn trống trong khoảng thời gian bạn chọn, Vui lòng chọn ngày khác hoặc phòng khác.");
                request.setAttribute("room", room);
                request.setAttribute("guestName", guestName);
                request.setAttribute("guestEmail", guestEmail);
                request.setAttribute("checkInDate", sqlCheckInDate);
                request.setAttribute("checkOutDate", sqlCheckOutDate);
                request.getRequestDispatcher("/WEB-INF/views/booking_form.jsp").forward(request, response);
                return;
            }
            long numberOfNights = BookingServiceImpl.calculateNights(checkInDateLocal, checkOutDateLocal);
            if(numberOfNights <= 0){
                request.setAttribute("errorMessage", "Số đêm phải lớn hơn 0.");
                request.setAttribute("room", room);
                request.getRequestDispatcher("/WEB-INF/views/booking_form.jsp").forward(request, response);
                return;
            }
            BigDecimal totalPrice = room.getPricePerNight().multiply(new BigDecimal(numberOfNights));
            Booking booking = new Booking();
            booking.setRoomId(roomId);
            booking.setGuestName(guestName);
            booking.setGuestEmail(guestEmail);
            booking.setCheckInDate(sqlCheckInDate);
            booking.setCheckOutDate(sqlCheckOutDate);
            booking.setTotalPrice(totalPrice);
            booking.setStatus("confirmed");
            boolean success = bookingService.createBooking(booking);
            if(success){
                request.setAttribute("booking", booking);
                request.setAttribute("room", room);
                request.getRequestDispatcher("/WEB-INF/views/booking_confirmation.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("errorMessage", "Không thể tạo đặt phòng. Có thể phòng vừa được đặt hoặc có lỗi hệ thống.");
                request.setAttribute("room", room);
                request.setAttribute("guestName", guestName);
                request.setAttribute("guestEmail", guestEmail);
                request.setAttribute("checkInDate", checkInStr);
                request.setAttribute("checkOutDate", checkOutStr);
                request.getRequestDispatcher("/WEB-INF/views/booking_form.jsp").forward(request, response);
                return;

            }

        } catch (NumberFormatException e) {
          request.setAttribute("errorMessage", "Dữ liệu không hợp lệ (ví dụ: ID phòng).");
          if (room != null) {
            request.setAttribute("room", room);
              request.getRequestDispatcher("/WEB-INF/views/booking_form.jsp").forward(request, response);
              return;
          }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu khi đặt phòng: " + e.getMessage());
            if (room != null) {
                request.setAttribute("room", room);
                request.getRequestDispatcher("/WEB-INF/views/booking_form.jsp").forward(request, response);
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            if (room != null) {
                request.setAttribute("room", room);
                request.getRequestDispatcher("/WEB-INF/views/booking_form.jsp").forward(request, response);
                return;
            }
        }
    }
}
