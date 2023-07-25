package qiwi.test.com.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateChecker {
    private final String dateFormat;

    public DateChecker(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public boolean checkDate(String date) {
        DateFormat sdf = new SimpleDateFormat(this.dateFormat);
        sdf.setLenient(false);
        try {
            sdf.parse(date);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
