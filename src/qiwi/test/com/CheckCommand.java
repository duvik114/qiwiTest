package qiwi.test.com;

import org.xml.sax.SAXException;
import picocli.CommandLine;
import qiwi.test.com.date.DateChecker;
import qiwi.test.com.xml.XMLParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import javax.xml.parsers.ParserConfigurationException;

@CommandLine.Command(name = "currency_rates", description = "Checks values", mixinStandardHelpOptions = true, version = "Value Client 1.0")
public class CheckCommand implements Callable<String> {

    private final String link = "https://www.cbr.ru/scripts/XML_daily.asp";
    private final DateChecker dateChecker = new DateChecker("yyyy-MM-dd");

    @CommandLine.Option(names = "--code", description = "currency code in ISO 4217 format", required = true)
    private String code;
    @CommandLine.Option(names = "--date", description = "date in format YYYY-MM-DD", required = true)
    private String date;

    public static void main(String... args) throws Exception {
        int exitCode = new CommandLine(new CheckCommand()).execute(args);
        System.exit(exitCode);
    }

    private void printHelp() {
        System.out.println("You should run command: 'currency_rates --code=<CODE> --date=<YYYY-MM-DD>'");
    }

    private String sendRequest(String link) throws IOException {
        URL url = new URL(link);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("charset", "utf-8");

        InputStream stream = connection.getInputStream();
        InputStreamReader streamReader = new InputStreamReader(stream);

        BufferedReader br = new BufferedReader(streamReader);
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        br.close();
        connection.disconnect();
        return result.toString();
    }

    @Override
    public String call() throws Exception {
        String result;
        if (!dateChecker.checkDate(date)) {
            printHelp();
            return "Failure";
        }
        String[] dateTokens = date.split("-");
        String params = String.format("?date_req=%s", String.join("/", dateTokens[2], dateTokens[1], dateTokens[0]));

        try {
            result = sendRequest(link + params);
        } catch (IOException e) {
            System.err.println("Cannot get response from server: " + e.getMessage());
            return "Failure";
        }

        try {
            XMLParser xmlParser = new XMLParser();
            String res = xmlParser.parse(code, result);
            if (res == null) {
                System.err.println("Cannot find value with code: " + code);
                return "Failure";
            } else {
                System.out.println(res);
                return "Success";
            }
        } catch (IOException | SAXException e) {
            System.err.println("Error occurred during parsing XML response: " + e.getMessage());
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            System.err.println("Cannot create XML parser: " + e.getMessage());
            e.printStackTrace();
        }

        return "Failure";
    }
}
