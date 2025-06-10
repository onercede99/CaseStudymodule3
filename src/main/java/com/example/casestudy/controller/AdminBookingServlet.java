package com.example.casestudy.controller; // Thay đổi package cho phù hợp

import com.example.casestudy.Service.BookingService;
import com.example.casestudy.Service.BookingServiceImpl;
import com.example.casestudy.Service.RoomService;
import com.example.casestudy.Service.RoomServiceImpl;
import com.example.casestudy.model.Booking;
import com.example.casestudy.model.Room;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(name = "AdminBookingServlet", urlPatterns = "/admin/bookings")
public class AdminBookingServlet extends HttpServlet {
    private BookingService bookingService;
    private RoomService roomService;

    @Override
    public void init() throws ServletException {
        System.out.println("AdminBookingServlet: init() called.");

        BookingServiceImpl tempBookingService = new BookingServiceImpl();
        RoomServiceImpl tempRoomService = new RoomServiceImpl();

        System.out.println("AdminBookingServlet: Setting RoomService into BookingService...");
        tempBookingService.setRoomService(tempRoomService);

        System.out.println("AdminBookingServlet: Setting BookingService into RoomService...");
        tempRoomService.setBookingService(tempBookingService);

        this.bookingService = tempBookingService;
        this.roomService = tempRoomService;

        if (this.bookingService != null && this.roomService != null) {
            System.out.println("AdminBookingServlet: bookingService and roomService initialized.");

        } else {
            System.err.println("AdminBookingServlet: FAILED to initialize services.");
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "new":
                    showNewBookingForm(request, response);
                    break;
                case "edit":
                    showEditBookingForm(request, response);
                    break;
                case "delete":
                    deleteBooking(request, response);
                    break;
                default:
                    listBookings(request, response);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/error_admin.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/bookings"); // Nếu không có action, quay lại danh sách
            return;
        }
        request.setCharacterEncoding("UTF-8");

        try {
            switch (action) {
                case "insert":
                    insertBooking(request, response);
                    break;
                case "update":
                    updateBooking(request, response);
                    break;
                case "delete":
                    deleteBooking(request, response);
                    break;
                default:
                    listBookings(request, response);
                    break;
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi xử lý đặt phòng: " + e.getMessage());
            try {
                if ("insert".equals(action) || "update".equals(action)) {

                    loadFormData(request);
                    request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_form_admin.jsp").forward(request, response);
                } else {
                    listBookings(request, response);
                }
            } catch (SQLException | ServletException | IOException ex) {
                ex.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi nghiêm trọng khi xử lý yêu cầu.");
            }
        }
    }

    private void listBookings(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        List<Booking> listBooking = bookingService.getAllBookings();
        request.setAttribute("listBooking", listBooking);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_list_admin.jsp");
        dispatcher.forward(request, response);
    }

    private void loadFormData(HttpServletRequest request) throws SQLException {
        List<Room> listRoom = roomService.getAllRooms();
        request.setAttribute("listRoom", listRoom);

    }


    private void showNewBookingForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        loadFormData(request);
        request.setAttribute("booking", new Booking());
        request.setAttribute("formAction", "insert");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_form_admin.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditBookingForm(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Booking existingBooking = bookingService.getBookingById(id);
        if (existingBooking == null) {
            response.sendRedirect(request.getContextPath() + "/admin/bookings?error=notFound");
            return;
        }
        loadFormData(request);
        request.setAttribute("booking", existingBooking);
        request.setAttribute("formAction", "update");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_form_admin.jsp");
        dispatcher.forward(request, response);
    }

    private void insertBooking(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ParseException, ServletException {

        System.out.println("--- ADMIN insertBooking START ---");

        String roomIdStr = request.getParameter("roomId");
        System.out.println("Admin insertBooking - roomIdStr from request: [" + roomIdStr + "]"); // LOG 1

        String checkInDateStr = request.getParameter("checkInDate");
        System.out.println("Admin insertBooking - checkInDateStr from request: [" + checkInDateStr + "]");

        String checkOutDateStr = request.getParameter("checkOutDate");
        System.out.println("Admin insertBooking - checkOutDateStr from request: [" + checkOutDateStr + "]");

        String totalPriceStr = request.getParameter("totalPrice");
        System.out.println("Admin insertBooking - totalPriceStr from request: [" + totalPriceStr + "]");

        String status = request.getParameter("status");
        System.out.println("Admin insertBooking - status from request: [" + status + "]");

        String guestName = request.getParameter("guestName");
        System.out.println("Admin insertBooking - guestName from request: [" + guestName + "]");

        String guestEmail = request.getParameter("guestEmail");
        System.out.println("Admin insertBooking - guestEmail from request: [" + guestEmail + "]");


        BigDecimal totalPrice = null;
        if (totalPriceStr != null && !totalPriceStr.trim().isEmpty()) {
            try {
                totalPrice = new BigDecimal(totalPriceStr.trim().replace(",", "")); // Thêm replace(",", "") nếu người dùng có thể nhập dấu phẩy
                if (totalPrice.compareTo(BigDecimal.ZERO) < 0) {
                    request.setAttribute("errorMessage", "Tổng tiền không thể là số âm.");
                    loadFormDataAndForwardForError(request, response, roomIdStr, guestName, guestEmail, checkInDateStr, checkOutDateStr, totalPriceStr, status, "insert");
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Định dạng Tổng Tiền không hợp lệ. Vui lòng nhập số.");
                loadFormDataAndForwardForError(request, response, roomIdStr, guestName, guestEmail, checkInDateStr, checkOutDateStr, totalPriceStr, status, "insert");
                return;
            }
        } else {
            request.setAttribute("errorMessage", "Vui lòng nhập Tổng Tiền.");
            loadFormDataAndForwardForError(request, response, roomIdStr, guestName, guestEmail, checkInDateStr, checkOutDateStr, totalPriceStr, status, "insert");
            return;
        }

        // Validate và parse roomId
        int roomId = -1; // Giá trị mặc định không hợp lệ
        if (roomIdStr == null || roomIdStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng chọn một phòng.");
            loadFormDataAndForwardForError(request, response, roomIdStr, guestName, guestEmail, checkInDateStr, checkOutDateStr, totalPriceStr, status, "insert");
            return;
        }
        try {
            roomId = Integer.parseInt(roomIdStr.trim());
            System.out.println("Admin insertBooking - Parsed roomId: " + roomId); // LOG 2
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Mã phòng không hợp lệ.");
            loadFormDataAndForwardForError(request, response, roomIdStr, guestName, guestEmail, checkInDateStr, checkOutDateStr, totalPriceStr, status, "insert");
            return;
        }

        // Validate checkInDateStr và checkOutDateStr (đã có)
        if (checkInDateStr == null || checkInDateStr.trim().isEmpty() ||
                checkOutDateStr == null || checkOutDateStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Ngày nhận và trả phòng là bắt buộc.");
            loadFormDataAndForwardForError(request, response, roomIdStr, guestName, guestEmail, checkInDateStr, checkOutDateStr, totalPriceStr, status, "insert");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date utilCheckInDate; // Đổi tên để rõ ràng là java.util.Date
        java.util.Date utilCheckOutDate;
        try {
            utilCheckInDate = dateFormat.parse(checkInDateStr);
            utilCheckOutDate = dateFormat.parse(checkOutDateStr);
            System.out.println("Admin insertBooking - Parsed utilCheckInDate: " + utilCheckInDate);
            System.out.println("Admin insertBooking - Parsed utilCheckOutDate: " + utilCheckOutDate);
        } catch (ParseException e) {
            request.setAttribute("errorMessage", "Định dạng ngày không hợp lệ (yyyy-MM-dd).");
            loadFormDataAndForwardForError(request, response, roomIdStr, guestName, guestEmail, checkInDateStr, checkOutDateStr, totalPriceStr, status, "insert");
            return;
        }

        // Validate if checkOutDate is after checkInDate (đã có)
        if (!utilCheckOutDate.after(utilCheckInDate)) {
            request.setAttribute("errorMessage", "Ngày trả phòng phải sau ngày nhận phòng.");
            loadFormDataAndForwardForError(request, response, roomIdStr, guestName, guestEmail, checkInDateStr, checkOutDateStr, totalPriceStr, status, "insert");
            return;
        }

        // Tạo đối tượng Booking
        Booking newBooking = new Booking();

        // Lấy thông tin phòng và kiểm tra
        Room room = roomService.getRoomById(roomId);
        System.out.println("Admin insertBooking - Room from service for ID " + roomId + ": " + (room != null ? room.getRoomType() : "NOT FOUND")); // LOG 3
        if (room == null) {
            request.setAttribute("errorMessage", "Phòng với ID " + roomId + " không tồn tại. Vui lòng chọn phòng hợp lệ.");
            loadFormDataAndForwardForError(request, response, roomIdStr, guestName, guestEmail, checkInDateStr, checkOutDateStr, totalPriceStr, status, "insert");
            return;
        }
        newBooking.setRoomId(roomId);

        // Set các thuộc tính cho newBooking
        // Các setter trong Booking.java phải chấp nhận java.util.Date nếu bạn truyền utilCheckInDate
        newBooking.setCheckInDate(utilCheckInDate);
        newBooking.setCheckOutDate(utilCheckOutDate);
        newBooking.setTotalPrice(totalPrice);
        newBooking.setStatus(status != null && !status.isEmpty() ? status : "PENDING"); // Thêm kiểm tra status rỗng
        newBooking.setGuestName(guestName);
        newBooking.setGuestEmail(guestEmail);

        System.out.println("Admin insertBooking - Booking object to be created: RoomID=" + newBooking.getRoomId() +
                ", Guest=" + newBooking.getGuestName() +
                ", CheckIn=" + newBooking.getCheckInDate() +
                ", CheckOut=" + newBooking.getCheckOutDate() +
                ", TotalPrice=" + newBooking.getTotalPrice() +
                ", Status=" + newBooking.getStatus()); // LOG 4

        boolean success = bookingService.createBooking(newBooking);
        System.out.println("Admin insertBooking - bookingService.createBooking result: " + success); // LOG 5
        System.out.println("Admin insertBooking - Booking ID after create (if updated by service): " + newBooking.getBookingId()); // LOG 6


        if (success) {
            System.out.println("Admin insertBooking - SUCCESS. Redirecting...");
            response.sendRedirect(request.getContextPath() + "/admin/bookings?success=add");
        } else {
            System.out.println("Admin insertBooking - FAILED. Forwarding back to form.");
            request.setAttribute("errorMessage", "Không thể tạo đặt phòng. Vui lòng thử lại hoặc liên hệ quản trị viên.");
            loadFormDataAndForwardForError(request, response, roomIdStr, guestName, guestEmail, checkInDateStr, checkOutDateStr, totalPriceStr, status, "insert", newBooking);
        }
        System.out.println("--- ADMIN insertBooking END ---");
    }

    private void loadFormDataAndForwardForError(HttpServletRequest request, HttpServletResponse response,
                                                String roomIdStr, String guestName, String guestEmail,
                                                String checkInDateStr, String checkOutDateStr,
                                                String totalPriceStr, String status, String formAction)
            throws ServletException, IOException, SQLException {
        loadFormData(request);
        request.setAttribute("submittedRoomId", roomIdStr);
        request.setAttribute("submittedGuestName", guestName);
        request.setAttribute("submittedGuestEmail", guestEmail);
        request.setAttribute("submittedCheckInDate", checkInDateStr);
        request.setAttribute("submittedCheckOutDate", checkOutDateStr);
        request.setAttribute("submittedTotalPrice", totalPriceStr);
        request.setAttribute("submittedStatus", status);
        request.setAttribute("formAction", formAction); // "insert" hoặc "update"

        request.getRequestDispatcher("/WEB-INF/jsp/admin/booking_form_admin.jsp").forward(request, response);
    }

    private void loadFormDataAndForwardForError(HttpServletRequest request, HttpServletResponse response,
                                                String roomIdStr, String guestName, String guestEmail,
                                                String checkInDateStr, String checkOutDateStr,
                                                String totalPriceStr, String status, String formAction, Booking bookingInProgress)
            throws ServletException, IOException, SQLException {
        request.setAttribute("booking", bookingInProgress); // Truyền đối tượng booking đang xử lý
        loadFormDataAndForwardForError(request, response, roomIdStr, guestName, guestEmail, checkInDateStr, checkOutDateStr, totalPriceStr, status, formAction);
    }

    private void updateBooking(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ParseException {
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));

        int roomId = Integer.parseInt(request.getParameter("roomId"));
        String checkInDateStr = request.getParameter("checkInDate");
        String checkOutDateStr = request.getParameter("checkOutDate");
         BigDecimal totalPrice = new BigDecimal(request.getParameter("totalPrice"));
         String status = request.getParameter("status");
         String guestName = request.getParameter("guestName");
         String guestEmail = request.getParameter("guestEmail");


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date checkInDate = (Date) dateFormat.parse(checkInDateStr);
        Date checkOutDate = (Date) dateFormat.parse(checkOutDateStr);

        Booking bookingToUpdate = bookingService.getBookingById(bookingId);
        if (bookingToUpdate == null) {
            response.sendRedirect(request.getContextPath() + "/admin/bookings?error=updateNotFound");
            return;
        }

        Room room = new Room();
        room.setRoomId(roomId);
        bookingToUpdate.setRoom(room);


        bookingToUpdate.setCheckInDate(checkInDate);
        bookingToUpdate.setCheckOutDate(checkOutDate);



        // TODO: Validate dữ liệu
        // TODO: Tính toán lại totalPrice nếu cần

        bookingService.updateBooking(bookingToUpdate);
        response.sendRedirect(request.getContextPath() + "/admin/bookings?success=update");
    }

    private void deleteBooking(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        try {
            bookingService.deleteBookingById(id);
            response.sendRedirect(request.getContextPath() + "/admin/bookings?success=delete");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Không thể xóa đặt phòng này. Lỗi: " + e.getMessage());
            listBookings(request, response);
        }
    }
}