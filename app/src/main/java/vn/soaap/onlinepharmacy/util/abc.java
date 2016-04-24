package vn.soaap.onlinepharmacy.util;

/**
 * Created by sapui on 4/21/2016.
 */
public class abc {
    private static abc ourInstance = new abc();

    public static abc getInstance() {
        return ourInstance;
    }

    private abc() {
    }
}
