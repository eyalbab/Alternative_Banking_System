package servlets.admin;

import com.google.gson.Gson;
import customer.Customers;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import loan.Loans;
import system.MySystem;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

@WebServlet(name = "AllCustomersServlet", urlPatterns = {"/allCustomersList"})
public class AllCustomersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MySystem engine = ServletUtils.getEngine(getServletContext());
        Customers allCustomers = engine.getAllCustomers();
        if (allCustomers.getAllCustomers().size() > 0) {
            response.setContentType("application/json");
            Gson gson = new Gson();
            String json = gson.toJson(allCustomers);
            ServletUtils.setMessageOnResponse(response.getWriter(), json);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
