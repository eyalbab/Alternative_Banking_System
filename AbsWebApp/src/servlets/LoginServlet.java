package servlets;

import constants.Constants;
import jakarta.servlet.annotation.WebServlet;
import system.MySystem;
import utils.ServletUtils;
import utils.SessionUtils;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static constants.Constants.IS_ADMIN;
import static constants.Constants.USERNAME;

@WebServlet(name = "Login", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        String usernameFromSession = SessionUtils.getUsername(request);
        MySystem engine = ServletUtils.getEngine(getServletContext());

        if (usernameFromSession == null) { //user is not logged in yet

            String usernameFromParameter = request.getParameter(USERNAME);
            Boolean isAdmin = Boolean.valueOf(request.getParameter(IS_ADMIN));
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } else {
                usernameFromParameter = usernameFromParameter.trim();
                synchronized (this) {
                    if (isAdmin) {
                        if (engine.isAdminLogged()) {
                            String errorMessage = "Admin already logged in.";
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getOutputStream().print(errorMessage);
                        } else {
                            engine.setAdminLogged();
                            request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
                            response.setStatus(HttpServletResponse.SC_OK);
                        }
                    } else {
                        if (engine.isUserExists(usernameFromParameter)) {
                            String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getOutputStream().print(errorMessage);
                        } else {
                            engine.addUser(usernameFromParameter);
                            request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
                            response.setStatus(HttpServletResponse.SC_OK);
                        }
                    }
                }
            }

        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

}