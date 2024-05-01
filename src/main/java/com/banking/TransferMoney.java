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
import javax.servlet.http.HttpSession;

@WebServlet("/TransferMoney")
public class TransferMoney extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        HttpSession session = request.getSession(false);

        if (session != null) {
            String user = (String) session.getAttribute("name");
            out.println("<h1 align='center'>Welcome, " + user + ". Continue with your money transfer</h1>");

            Connection con = null;
            try {
                con = DBConnection.get();
                con.setAutoCommit(false); // Start transaction

                int fromAccount = Integer.parseInt(request.getParameter("fromAccount").trim());
                int toAccount = Integer.parseInt(request.getParameter("toAccount").trim());
                int transferAmount = Integer.parseInt(request.getParameter("transferAmount").trim());

                // Deduct amount from the sender's account
                String deductQuery = "UPDATE account SET balance = balance - ? WHERE num = ?";
                PreparedStatement deductStatement = con.prepareStatement(deductQuery);
                deductStatement.setInt(1, transferAmount);
                deductStatement.setInt(2, fromAccount);
                int rowsAffected = deductStatement.executeUpdate();

                if (rowsAffected > 0) {
                    // Add amount to the receiver's account
                    String addQuery = "UPDATE account SET balance = balance + ? WHERE num = ?";
                    PreparedStatement addStatement = con.prepareStatement(addQuery);
                    addStatement.setInt(1, transferAmount);
                    addStatement.setInt(2, toAccount);
                    addStatement.executeUpdate();

                    con.commit(); // Commit transaction
                    out.println("<h3 align='center'>Money Transfer Successful</h3>");
                } else {
                    out.println("<h3 align='center'>Insufficient Balance or Invalid Account Number</h3>");
                }
            } catch (Exception e) {
                try {
                    if (con != null) {
                        con.rollback(); // Rollback transaction if an error occurs
                    }
                } catch (SQLException ex) {
                    out.println("<h3 align='center'>Error: " + ex.getMessage() + "</h3>");
                }
                out.println("<h3 align='center'>Error: " + e.getMessage() + "</h3>");
            } finally {
                if (con != null) {
                    try {
                        con.setAutoCommit(true); // Reset auto-commit mode
                        con.close();
                    } catch (SQLException e) {
                        out.println("<h3 align='center'>Error: " + e.getMessage() + "</h3>");
                    }
                }
            }
        } else {
            out.println("<h3>You logged out from previous Session - Please Login</h3>");
            request.getRequestDispatcher("login.html").include(request, response);
        }
    }
}
