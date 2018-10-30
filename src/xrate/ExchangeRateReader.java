package xrate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.io.*;

/**
 * Provide access to basic currency exchange rate services.
 * 
 * @author PUT YOUR TEAM NAME HERE
 */
public class ExchangeRateReader {
    private String baseURL;
    private String accessKey;

    /**
     * Construct an exchange rate reader using the given base URL. All requests
     * will then be relative to that URL. If, for example, your source is Xavier
     * Finance, the base URL is http://api.finance.xaviermedia.com/api/ Rates
     * for specific days will be constructed from that URL by appending the
     * year, month, and day; the URL for 25 June 2010, for example, would be
     * http://api.finance.xaviermedia.com/api/2010/06/25.xml
     * 
     * @param baseURL
     *            the base URL for requests
     */
    public ExchangeRateReader(String baseURL) throws IOException {
        //sets up baseURL
        this.baseURL = baseURL;

        try {
            readAccessKeys();
        } catch (IOException e) {
            System.out.println("Cannot read access key! Now terminating program! ");
            System.exit(1);
        }
    }

    /**
     * This reads the `fixer_io` access key from `etc/access_keys.properties`
     * and assigns it to the field `accessKey`.
     *
     * @throws IOException if there is a problem reading the properties file
     */
    private void readAccessKeys() throws IOException {
        Properties properties = new Properties();
        FileInputStream in = null;
        try {
            // Don't change this filename unless you know what you're doing.
            // It's crucial that we don't commit the file that contains the
            // (private) access keys. This file is listed in `.gitignore` so
            // it's safe to put keys there as we won't accidentally commit them.
            in = new FileInputStream("etc/access_keys.properties");
        } catch (FileNotFoundException e) {
            /*
             * If this error gets generated, make sure that you have the desired
             * properties file in your project's `etc` directory. You may need
             * to rename the file ending in `.sample` by removing that suffix.
             */
            System.err.println("Couldn't open etc/access_keys.properties; have you renamed the sample file?");
            throw(e);
        }
        properties.load(in);
        // This assumes we're using Fixer.io and that the desired access key is
        // in the properties file in the key labelled `fixer_io`.
        accessKey = properties.getProperty("fixer_io");
    }


    /**
     * Get the exchange rate for the specified currency against the base
     * currency (the Euro) on the specified date.
     * 
     * @param currencyCode
     *            the currency code for the desired currency
     * @param year
     *            the year as a four digit integer
     * @param month
     *            the month as an integer (1=Jan, 12=Dec)
     * @param day
     *            the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException
     */
    public float getExchangeRate(String currencyCode, String year, String month, String day) throws IOException {
        //Sets url with date and accessKey, accessKey is ignored if not necessary
        URL url = new URL(baseURL + year + "-" + month + "-" + day + "?access_key=" + accessKey);

        //Sets up input stream and input reader
        InputStream input = url.openStream();
        JsonReader reader = new JsonReader(new InputStreamReader(input));

        //Creates jsonObject from reader
        JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();

        //Finds the rates object in the JSON data, goes to the specified currency code and returns the exchange rate
        return jsonObject.getAsJsonObject("rates").get(currencyCode).getAsFloat();
    }

    /**
     * Get the exchange rate of the first specified currency against the second
     * on the specified date.
     * 
     * @param fromCurrency
     *            the currency code we're exchanging *from*
     * @param toCurrency
     *            the currency code we're exchanging *to*
     * @param year
     *            the year as a four digit integer
     * @param month
     *            the month as an integer (1=Jan, 12=Dec)
     * @param day
     *            the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException
     */
    public float getExchangeRate(String fromCurrency, String toCurrency, String year, String month, String day) throws IOException {
        //Sets url with date and accessKey, accessKey is ignored if not necessary
        URL url = new URL(baseURL + year + "-" + month + "-" + day + "?access_key=" + accessKey);

        //Sets up input stream and input reader
        InputStream input = url.openStream();
        JsonReader reader = new JsonReader(new InputStreamReader(input));

        //Creates jsonObject from reader
        JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();

        //Finds the exchange rates for the two currency codes provided and divides them to find the exchange rate between the two currencies
        return jsonObject.getAsJsonObject("rates").get(fromCurrency).getAsFloat()/jsonObject.getAsJsonObject("rates").get(toCurrency).getAsFloat();

    }
}