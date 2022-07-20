package servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import system.MySystem;
import utils.ServletUtils;

import java.io.IOException;

@WebServlet(name = "SaveStateServlet", urlPatterns = {"/saveState"})
public class SaveStateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MySystem engine = ServletUtils.getEngine(getServletContext());
        String yazToSave = request.getParameter("yazToSave");
        engine.addNewState(Integer.parseInt(yazToSave));
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
