package servlets.customer;

import com.google.gson.Gson;
import customer.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import loan.Categories;
import system.MySystem;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@WebServlet(name = "GetCategoriesServlet", urlPatterns = {"/getCategories"})
public class GetCategoriesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = SessionUtils.getUsername(request);
        //Session doesn't exist!
        if (userName == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ServletUtils.setMessageOnResponse(response.getWriter(), "No Session");
            return;
        }
        MySystem engine = ServletUtils.getEngine(getServletContext());
        Categories allCategories = engine.getCategories();
        if (allCategories.getAllCategories().size() > 0) {
            response.setContentType("application/json");
            Gson gson = new Gson();
            String json = gson.toJson(allCategories);
            response.setStatus(HttpServletResponse.SC_OK);
            ServletUtils.setMessageOnResponse(response.getWriter(), json);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ServletUtils.setMessageOnResponse(response.getWriter(), "No Categories at the moment");
        }
    }
}