package com.banking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UpdateName")
public class UpdateName extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        
        // Retrieve parameters from the request
        String accountNumber = request.getParameter("accountNumberUpdateName");
        String newName = request.getParameter("newName");

        Connection con = null;
        try {
            con = DBConnection.get();
            
            // Update the name in the database
            String query = "UPDATE account SET name = ? WHERE num = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, newName);
            ps.setString(2, accountNumber);
            
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                out.println("<h1>Name updated successfully</h1>");
            } else {
                out.println("<h1>Failed to update name</h1>");
            }
        } catch (SQLException e) {
            out.println("<h1>Error: " + e.getMessage() + "</h1>");
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                out.println("<h1>Error: " + e.getMessage() + "</h1>");
            }
        }
    }
}
