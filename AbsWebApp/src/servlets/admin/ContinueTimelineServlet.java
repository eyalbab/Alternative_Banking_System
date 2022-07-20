package servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import system.MySystem;
import utils.ServletUtils;

import java.io.IOException;

@WebServlet(name = "ContinueTimelineServlet", urlPatterns = {"/continueTimelineServlet"})
public class ContinueTimelineServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MySystem engine = ServletUtils.getEngine(getServletContext());
        engine.continueTimeline();
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
