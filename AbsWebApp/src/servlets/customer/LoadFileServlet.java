package servlets.customer;

import constants.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import system.MySystem;
import utils.AbsException;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Scanner;

import static constants.Constants.IS_ADMIN;
import static constants.Constants.USERNAME;

@WebServlet(name = "LoadFile", urlPatterns = {"/loadFile"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LoadFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String usernameFromSession = SessionUtils.getUsername(request);
        MySystem engine = ServletUtils.getEngine(getServletContext());

        if (usernameFromSession == null) { //user is not logged in yet
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        response.setContentType("text/plain");
        Collection<Part> parts = request.getParts();
        if (parts.size() > 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        StringBuilder fileContent = new StringBuilder();
        for (Part part : parts) {
            if(part.getSize() == 0){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            fileContent.append(readFromInputStream(part.getInputStream()));
        }
        InputStream iStream = new ByteArrayInputStream(fileContent.toString().getBytes(StandardCharsets.UTF_8));
        try {
            engine.loadFile(iStream, usernameFromSession);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (JAXBException e) {
            String errorMessage = "Something wrong with file schema";
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            ServletUtils.setMessageOnResponse(response.getWriter(), "Bad File Schema");
        } catch (AbsException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            ServletUtils.setMessageOnResponse(response.getWriter(), e.getErrorMsg());
        }
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}
