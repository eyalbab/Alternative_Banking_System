package servlets.customer;

import com.google.gson.Gson;
import customer.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import loan.Categories;
import loan.Loan;
import loan.Loans;
import system.MySystem;
import utils.AbsException;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@WebServlet(name = "FindInvestLoansServlet", urlPatterns = {"/findInvestLoans"})
public class FindInvestLoansServlet extends HttpServlet {

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
        if (customer.isPresent()) {
            response.setContentType("application/json");
            String amountToInvest = request.getParameter("amountToInvest");
            String minInterest = request.getParameter("minInterest");
            String minTotalYaz = request.getParameter("minTotalYaz");
            String maxAliveOwnerLoans = request.getParameter("maxAliveOwnerLoans");
            String jsonCategories = request.getParameter("jsonCategories");
            Gson gson = new Gson();
            Categories chosenCategories = gson.fromJson(jsonCategories, Categories.class);
            try {
                Loans foundLoans = new Loans(engine.showSuggestedLoans(customer.get(), amountToInvest,
                        chosenCategories.getAllCategories(), minInterest, minTotalYaz, maxAliveOwnerLoans));
                if (foundLoans != null) {
                    if (foundLoans.getAllLoans().size() > 0) {
                        String jsonLoans = gson.toJson(foundLoans);
                        response.setStatus(HttpServletResponse.SC_OK);
                        ServletUtils.setMessageOnResponse(response.getWriter(), jsonLoans);
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        ServletUtils.setMessageOnResponse(response.getWriter(), "No relevant loans");
                    }
                }
            } catch (AbsException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ServletUtils.setMessageOnResponse(response.getWriter(), e.getErrorMsg());
            }

        }
    }
}