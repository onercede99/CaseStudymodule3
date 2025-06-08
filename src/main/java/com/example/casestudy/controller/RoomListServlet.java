package com.example.casestudy.controller;

import com.example.casestudy.Service.RoomService;
import com.example.casestudy.Service.RoomServiceImpl;
import com.example.casestudy.model.Room;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name="RoomListServlet", urlPatterns="/rooms")
public class RoomListServlet extends HttpServlet {
    private RoomService roomService;
    @Override
    public void init(){
        roomService = new RoomServiceImpl();
    }
    @Override
    protected  void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        try{
            System.out.println("RoomListServlet - doGet - Entered");
            String destination = request.getParameter("destination");
            String checkInStr = request.getParameter("checkIn");
            String checkOutStr = request.getParameter("checkOut");
            String guestsStr = request.getParameter("guests");
            System.out.println("RoomListServlet - Params: dest=" + destination + ", checkIn=" + checkInStr + "checkOut=" + checkOutStr + ", guests=" + guestsStr);

            List<Room> roomList;
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);

            Date sqlCheckInDate = (checkInStr != null && !checkInStr.isEmpty() ? Date.valueOf(checkInStr): Date.valueOf(today));
            Date sqlCheckOutDate = (checkOutStr != null && !checkOutStr.isEmpty() ? Date.valueOf(checkOutStr): Date.valueOf(tomorrow));
            if(sqlCheckOutDate.before(sqlCheckInDate) || sqlCheckOutDate.equals(sqlCheckInDate)){
                sqlCheckOutDate = Date.valueOf(tomorrow);
                request.setAttribute("warningMessage", "Ngày trả phòng phải sau ngày nhận phòng. Đã tự động điều chỉnh.");
            }
            if((destination != null && !destination.trim().isEmpty()) || (checkInStr != null && !checkInStr.isEmpty()) || (checkOutStr != null && !checkOutStr.isEmpty())){
                int guests = 2;
                if(guestsStr != null && !guestsStr.isEmpty()){
                    try{
                        guests = Integer.parseInt(guestsStr.replaceAll("\\D+",""));
                    } catch(NumberFormatException e){
                    }
                }
                roomList = roomService.searchRooms(destination, sqlCheckInDate, sqlCheckOutDate, guests);
                if(roomList.isEmpty()){
                    request.setAttribute("message", "Không tìm thấy phòng nào phù hợp với tiêu chí của bạn.");
                }
            } else {
                roomList = roomService.getAllAvailableRoomsByDate(Date.valueOf(today), Date.valueOf(tomorrow));
            }
            request.setAttribute("roomList", roomList);
            request.setAttribute("paramDestination", destination);
            request.setAttribute("paramCheckIn", sqlCheckInDate.toString());
            request.setAttribute("paramCheckOut", sqlCheckOutDate.toString());
            request.setAttribute("paramGuests", guestsStr);
            request.getRequestDispatcher("/WEB-INF/views/rooms.jsp").forward(request, response);
        } catch (DateTimeException e){
            e.printStackTrace();
            request.setAttribute("errorMessage", "Định dạng ngày không hợp lệ. Vui lòng sử dụng YYYY-MM-DD.");
            request.setAttribute("paramDestination", request.getParameter("destination"));
            request.setAttribute("paramCheckIn", request.getParameter("checkIn"));
            request.setAttribute("paramCheckOut", request.getParameter("checkOut"));
            request.setAttribute("paramGuests", request.getParameter("guests"));
            request.getRequestDispatcher("/WEB-INF/views/rooms.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi khi truy xuất danh sách phòng: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
