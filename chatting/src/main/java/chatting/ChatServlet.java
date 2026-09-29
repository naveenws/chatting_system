package chatting;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/ChatServlet")
public class ChatServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("send".equals(action)) {
            sendMessage(request, response);
        }
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("get".equals(action)) {
            getMessages(request, response);
        }
    }

    private void sendMessage(HttpServletRequest request,
                             HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();

        String sender =
                (String) session.getAttribute("username");

        String receiver =
                request.getParameter("receiver");

        String message =
                request.getParameter("message");

        String sql =
                "INSERT INTO messages(sender, receiver, message) VALUES (?, ?, ?)";

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setString(3, message);

            ps.executeUpdate();

            ps.close();
            con.close();

            response.getWriter().print("success");

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().print(
                "Error: " + e.getMessage()
            );
        }
    }

    private void getMessages(HttpServletRequest request,
                             HttpServletResponse response)
            throws IOException {

        HttpSession session =
                request.getSession();

        String user =
                (String) session.getAttribute("username");

        String receiver =
                request.getParameter("receiver");

        response.setContentType("text/html");

        PrintWriter out =
                response.getWriter();

        String sql =
            "SELECT sender, receiver, message, message_time " +
            "FROM messages " +
            "WHERE (sender=? AND receiver=?) " +
            "OR (sender=? AND receiver=?) " +
            "ORDER BY message_time ASC";

        try {

            Connection con =
                    DBConnection.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, user);
            ps.setString(2, receiver);
            ps.setString(3, receiver);
            ps.setString(4, user);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                out.println("<div class='message'>");

                out.println(
                    "<b>" +
                    rs.getString("sender") +
                    "</b> → " +
                    rs.getString("receiver")
                );

                out.println("<br>");

                out.println(
                    rs.getString("message")
                );

                out.println("<br>");

                out.println(
                    "<span class='time'>" +
                    rs.getTimestamp("message_time") +
                    "</span>"
                );

                out.println("</div>");
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            out.println(
                "<p>Unable to load messages</p>"
            );
        }
    }
}