package com.banking;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/CheckBalance")
public class CheckBalance extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter pw = response.getWriter();
        response.setContentType("text/html");
        Connection con = null;
        

        
            try {
                con = DBConnection.get();
                int num = Integer.parseInt(request.getParameter("accountNumberCheck").trim());

                String query = "SELECT balance FROM account WHERE num=?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, num);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int balance = rs.getInt("balance");
                    pw.println("<h2 align='center'>Account Balance: " + balance + "</h2>");
                } else {
                    pw.println("<h2 align='center'>Account not found</h2>");
                }
            } catch (Exception e) {
                pw.println("<h2 align='center'>Error: " + e.getMessage() + "</h2>");
            } finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        pw.println("<h2 align='center'>Error: " + e.getMessage() + "</h2>");
                    }
                }
            }
        
    }
}
