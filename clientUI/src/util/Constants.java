package util;


import com.google.gson.Gson;

public class Constants {

    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String JHON_DOE = "<Anonymous>";
    public final static int REFRESH_RATE = 1000;

    // fxml locations
    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/components/adminApp/AdminApp.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/components/loginPage/LoginPage.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/AbsWebApp";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;
    //Common
    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    //Admin
    public final static String LOANS_LIST = FULL_SERVER_PATH + "/allLoansList";
    //Customer
    public final static String LOAD_FILE = FULL_SERVER_PATH + "/loadFile";
    public final static String CUSTOMER_INFO_REFRESH = FULL_SERVER_PATH + "/customerInfoRefresh";
    public final static String DEPOSIT = FULL_SERVER_PATH + "/deposit";
    public final static String WITHDRAW = FULL_SERVER_PATH + "/withdraw";
    public final static String NEW_LOAN = FULL_SERVER_PATH + "/newLoan";
    public final static String GET_CATEGORIES = FULL_SERVER_PATH + "/getCategories";
    public final static String FIND_INVEST_LOANS = FULL_SERVER_PATH + "/findInvestLoans";
    public final static String ASSIGN_LOANS = FULL_SERVER_PATH + "/assignLoans";
    public final static String PAYMENT = FULL_SERVER_PATH + "/payment";
    public final static String OFFER_LOAN = FULL_SERVER_PATH + "/offerLoanForSale";
    public final static String FIND_LOANS_FOR_SALE = FULL_SERVER_PATH + "/findLoansForSale";
    public final static String BUY_LOAN = FULL_SERVER_PATH + "/buyLoan";


    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
