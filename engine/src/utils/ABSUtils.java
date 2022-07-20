package utils;

import com.sun.xml.internal.ws.util.StringUtils;
import jaxb.generated.AbsDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

public class ABSUtils {

    private final static String JAXB_XML_ABS_PACKAGE = "jaxb.generated";

    public static Integer tryParseIntAndValidateRange(String value, Integer min, Integer max) {
        try {
            Integer intValue = Integer.parseInt(value);
            if (intValue > max || intValue < min)
                return -1;
            return intValue;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static Double tryParseDoubleAndValidateRange(String value, Double min, Double max) {
        try {
            Double DoubleValue = Double.parseDouble(value);
            if (DoubleValue > max || DoubleValue < min)
                return -1.0;
            return DoubleValue;
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }

    public static AbsDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_ABS_PACKAGE);
        Unmarshaller u = jc.createUnmarshaller();
        return (AbsDescriptor) u.unmarshal(in);
    }

    public static String sanitizeStr(String toSanit) {
        if (toSanit != null) {
            String res = toSanit.trim();
            res = res.toLowerCase();
            res = StringUtils.capitalize(res);
            return res;
        } else return null;
    }

}
