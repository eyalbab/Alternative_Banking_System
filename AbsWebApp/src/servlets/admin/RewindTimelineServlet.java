package servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import system.MySystem;
import utils.ServletUtils;

import java.io.IOException;

@WebServlet(name = "RewindTimelineServlet", urlPatterns = {"/rewindTimelineServlet"})
public class RewindTimelineServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        MySystem engine = ServletUtils.getEngine(getServletContext());
        String toYaz = request.getParameter("toYaz");
        String isPresent = request.getParameter("isPresent");
        engine.setRewind(!Boolean.valueOf(isPresent));
        /**NOTIFY IS PRESENT**/
        engine.loadYazState(Integer.parseInt(toYaz));
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
