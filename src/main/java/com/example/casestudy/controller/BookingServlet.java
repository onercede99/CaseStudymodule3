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
        System.out.println("BookingServlet: init() called.");

        BookingServiceImpl tempBookingService = new BookingServiceImpl();
        RoomServiceImpl tempRoomService = new RoomServiceImpl();

        tempBookingService.setRoomService(tempRoomService);
        tempRoomService.setBookingService(tempBookingService);

        this.roomService = tempRoomService;
        this.bookingService = tempBookingService;

        if (this.roomService != null && this.bookingService != null) {
            System.out.println("BookingServlet: roomService and bookingService initialized.");
        } else {
            System.err.println("BookingServlet: FAILED to initialize services.");
        }
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
            System.out.println("--- BookingServlet - doPost START ---");
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
            if(numberOfNights < 1){
                request.setAttribute("errorMessage", "Số đêm phải lớn hơn 0.");
                request.setAttribute("room", room);
                request.setAttribute("guestName", guestName);
                request.setAttribute("guestEmail", guestEmail);
                request.setAttribute("checkInDate", checkInStr);
                request.setAttribute("checkOutDate", checkOutStr);
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
                System.out.println("--- BookingServlet - doPost - Booking SUCCESS ---");
                System.out.println("Forwarding to confirmation page.");
                System.out.println("Booking Details from Servlet: ID=" + booking.getBookingId() +
                        ", Guest=" + booking.getGuestName() +
                        ", RoomID=" + booking.getRoomId() +
                        ", CheckIn=" + booking.getCheckInDate() +
                        ", CheckOut=" + booking.getCheckOutDate() +
                        ", TotalPrice=" + booking.getTotalPrice() +
                        ", Status=" + booking.getStatus());

                System.out.println("Room Details from Servlet: ID=" + room.getRoomId() +
                        ", Number=" + room.getRoomNumber() +
                        ", Type=" + room.getRoomType() +
                        ", Price=" + room.getPricePerNight());
                System.out.println("--- End Debug Log ---");


                request.setAttribute("booking", booking);
                request.setAttribute("room", room);

                request.getRequestDispatcher("/WEB-INF/views/booking_confirmation.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("errorMessage", "Không thể tạo đặt phòng. Có thể phòng vừa được đặt hoặc có lỗi hệ thống.");
                 request.setAttribute("booking", booking);
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
