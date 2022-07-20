package loan;

import utils.AbsException;
import jaxb.generated.AbsCategories;
import utils.ABSUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Categories implements Serializable {
    private final List<String> allCategories;

    public Categories() {
        this.allCategories = new ArrayList<String>();
    }

    public Categories(List<String> allCategories) {
        this.allCategories = allCategories;
    }

    public static Categories ConvertRawAbsToCategories(AbsCategories rawVer) throws AbsException {
        List<String> resList = new ArrayList<>();
        for (String categ : rawVer.getAbsCategory()) {
            if (!resList.isEmpty()) {
                for (String toIter : resList) {
                    if (toIter.equals(categ)) {
                        throw new AbsException("We can't have two Categories with same name");
                    }
                }
            }
            resList.add(ABSUtils.sanitizeStr(categ));
        }

        return new Categories(resList);
    }

    public List<String> getAllCategories() {
        return allCategories;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("Categories are:\n");
        int i = 0;
        for (String categ : allCategories) {
            res.append(++i).append(") ").append(categ).append(".\n");
        }
        return res.toString();
    }
}
