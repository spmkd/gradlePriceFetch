package settings;

public class DataBaseConnectionDetails {

    private static final String URL = "jdbc:mysql://192.168.1.247:3306/PRICE_FETCH?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";
    private static final String USER = "price_fetch";
    private static final String PASSWORD = "mypass";

    public static String getURL() {
        return URL;
    }

    public static String getUSER() {
        return USER;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }
}
