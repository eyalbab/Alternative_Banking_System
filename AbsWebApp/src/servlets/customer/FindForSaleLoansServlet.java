package servlets.customer;

import com.google.gson.Gson;
import customer.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import loan.Categories;
import loan.Loan;
import loan.Loans;
import loan.OfferedLoans;
import system.MySystem;
import utils.AbsException;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@WebServlet(name = "FindForSaleLoansServlet", urlPatterns = {"/findLoansForSale"})
public class FindForSaleLoansServlet extends HttpServlet {

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
        OfferedLoans loansForSale = engine.getLoansForSale(userName);
        if (loansForSale.getOfferedLoans().size() >= 0) {
            response.setContentType("application/json");
            Gson gson = new Gson();
            String jsonLoans = gson.toJson(loansForSale);
            ServletUtils.setMessageOnResponse(response.getWriter(), jsonLoans);
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}