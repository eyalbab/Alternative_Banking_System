package servlets.admin;

import customer.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import loan.Loans;
import system.MySystem;
import utils.ServletUtils;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet(name = "AllLoansServlet", urlPatterns = {"/allLoansList"})
public class AllLoansServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MySystem engine = ServletUtils.getEngine(getServletContext());
        Loans loans = engine.getLoans();
        if (loans.getAllLoans().size() > 0) {
            response.setContentType("application/json");
            Gson gson = new Gson();
            String json = gson.toJson(loans);
            ServletUtils.setMessageOnResponse(response.getWriter(), json);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
