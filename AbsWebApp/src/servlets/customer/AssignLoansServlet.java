package servlets.customer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import customer.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import loan.Categories;
import loan.Loan;
import loan.Loans;
import system.MySystem;
import utils.AbsException;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet(name = "AssignLoansServlet", urlPatterns = {"/assignLoans"})
public class AssignLoansServlet extends HttpServlet {

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
            String jsonChosenLoans = request.getParameter("jsonChosenLoans");
            String sumToInvest = request.getParameter("sumToInvest");
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            List<String> chosenLoansString = gson.fromJson(jsonChosenLoans, listType);
            List<Loan> chosenLoans = engine.getLoans().getAllLoans().stream()
                    .filter(loan -> chosenLoansString.contains(loan.getId())).collect(Collectors.toList());
            if (chosenLoans != null) {
                engine.assignLoans(customer.get(), chosenLoans, Integer.parseInt(sumToInvest));
                response.setStatus(HttpServletResponse.SC_OK);
                ServletUtils.setMessageOnResponse(response.getWriter(), "assigned loans");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                ServletUtils.setMessageOnResponse(response.getWriter(), "couldn't assign loans");
            }
        }
    }
}