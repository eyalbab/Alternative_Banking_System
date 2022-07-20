package utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import system.MySystem;

import java.io.PrintWriter;

import static constants.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

	private static final String MY_SYSTEM_ATTRIBUTE_NAME = "mySystem";

	/*
	Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
	the actual fetch of them is remained un-synchronized for performance POV
	 */
	private static final Object mySystemLock = new Object();

	public static MySystem getEngine(ServletContext servletContext) {

		synchronized (mySystemLock) {
			if (servletContext.getAttribute(MY_SYSTEM_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(MY_SYSTEM_ATTRIBUTE_NAME, new MySystem());
			}
		}
		return (MySystem) servletContext.getAttribute(MY_SYSTEM_ATTRIBUTE_NAME);
	}

	public static void setMessageOnResponse(PrintWriter out, String data){
		out.println(data);
		out.flush();
		out.close();
	}

	public static int getIntParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException numberFormatException) {
			}
		}
		return INT_PARAMETER_ERROR;
	}
}
