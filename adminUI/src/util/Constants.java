package util;


import com.google.gson.Gson;

public class Constants {

    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String JHON_DOE = "<Anonymous>";
    public final static int REFRESH_RATE = 1000;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";

    // fxml locations
    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/components/adminApp/AdminApp.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/components/loginPage/LoginPage.fxml";
    public final static String CHAT_ROOM_FXML_RESOURCE_LOCATION = "/chat/client/component/chatroom/chat-room-main.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/AbsWebApp";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String LOANS_LIST = FULL_SERVER_PATH + "/allLoansList";
    public final static String CUSTOMERS_LIST = FULL_SERVER_PATH + "/allCustomersList";
    public final static String CONTINUE_TIMELINE = FULL_SERVER_PATH + "/continueTimelineServlet";
    public final static String REWIND_TIMELINE = FULL_SERVER_PATH + "/rewindTimelineServlet";
    public final static String SAVE_STATE = FULL_SERVER_PATH + "/saveState";

    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
