package id.ac.ui.cs.advprog.authjwt.config;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class GeneralUtils {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String INT_MAX_12_REGEX = "^\\d{1,12}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern INT_PATTERN = Pattern.compile(INT_MAX_12_REGEX);

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidInt(String number) {
        if(number == null) return false;
        Matcher matcher = INT_PATTERN.matcher(number);
        return matcher.matches();
    }
}