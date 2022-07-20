package servlets.customer;

import com.google.gson.Gson;
import customer.Customer;
import customer.CustomerInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import system.MySystem;
import utils.ServletUtils;
import utils.SessionUtils;


import java.io.IOException;
import java.util.Optional;


@WebServlet(name = "CustomerPullInformationServlet", urlPatterns = {"/customerInfoRefresh"})
public class CustomerInfoPullServlet extends HttpServlet {
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
        Optional<Customer> customer = engine.getAllCustomers().getCustomerByName(userName);
//        if(customer.isPresent()) {
        response.setContentType("application/json");
        Gson gson = new Gson();
        CustomerInfo custInfo = new CustomerInfo(customer.orElseGet(null), engine.getYaz(), engine.getInRewind());
        String json = gson.toJson(custInfo);
        response.setStatus(HttpServletResponse.SC_OK);
        ServletUtils.setMessageOnResponse(response.getWriter(), json);
//        } else{
//            response.setStatus(HttpServletResponse.SC_OK);
//            ServletUtils.setMessageOnResponse(response.getWriter(), "Something went wrong");
//        }
    }
}