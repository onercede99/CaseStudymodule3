<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    java.util.List<com.example.casestudy.model.Room> roomsJSP = (java.util.List<com.example.casestudy.model.Room>) request.getAttribute("roomList");
    String messageJSP = (String) request.getAttribute("message");
    String errorMessageJSP = (String) request.getAttribute("errorMessage");
    String warningMessageJSP = (String) request.getAttribute("warningMessage");

    System.out.println("--- rooms.jsp ---");
    if (roomsJSP == null) {
        System.out.println("rooms.jsp: roomList attribute is NULL.");
    } else {
        System.out.println("rooms.jsp: roomList attribute size: " + roomsJSP.size());
    }
    System.out.println("rooms.jsp: message attribute: " + messageJSP);
    System.out.println("rooms.jsp: errorMessage attribute: " + errorMessageJSP);
    System.out.println("rooms.jsp: warningMessage attribute: " + warningMessageJSP);
    System.out.println("--- end rooms.jsp ---");
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>