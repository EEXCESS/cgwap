package cgwap.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

/**
 * Holds system's config parameters. The Parameters are loaded from the
 * config.properties file.
 * 
 */
public class Config {

    private static final Logger LOGGER = Logger.getLogger(Config.class.getName());

    /**
     * File name of the error log.
     */
    public static String ERROR_LOG_FILE_NAME;

    /**
     * Database host.
     */
    public static String DB_HOST;

    /**
     * Database port.
     */
    public static String DB_PORT;

    /**
     * Database name.
     */
    public static String DB_NAME;

    /**
     * Database username.
     */
    public static String DB_USER;

    /**
     * Database password.
     */
    public static String DB_PASS;

    /**
     * Minimal number of connections in the connection-pool. This number of
     * connections is loaded
     * at start-up.
     */
    public static int MIN_CONNECTIONS;

    /**
     * Maximal number of connections in the connection-pool. When more
     * connections are needed and
     * get generated dynamically, this is the absolute maximum of possible
     * connections.
     */
    public static int MAX_CONNECTIONS;

    /**
     * Maximal seconds to wait until requests will fail.
     */
    public static int DB_TIMEOUT = 20;

    /**
     * Locations where to store images.
     */
    public static String IMAGE_STORING_LOCATION;

    /**
     * Locations where to store css files.
     */
    public static String CSS_STORING_LOCATION;

    // /**
    // * Locations where to store logs.
    // */
    // public static String LOG_STORING_LOCATION;

    /**
     * Email address for initializing creation of the administrator account.
     */
    public static String ADMIN_EMAIL_ADDRESS;

    /**
     * Nickname for initializing creation of the administrator account.
     */
    public static String ADMIN_NICKNAME;

    /**
     * The URL for an API request.
     */
    public static String REQUEST_URL;

    /**
     * The API Key.
     */
    public static String REQUEST_KEY;

    /**
     * The API Action
     */
    public static String API_ACTION;

    /**
     * The Number of Rows the Request should return.
     */
    public static String REQUEST_ROWS;

    /**
     * The keywords the API Action should search for.
     */
    public static String REQUEST_QUERY;

    /**
     * The requested profile i.e. which metadata fields should be returned.
     */
    public static String REQUEST_PROFILE;
    public static String PROFILE_STANDARD;
    public static String PROFILE_MINIMAL;
    public static String PROFILE_PARAMS;
    public static String CHOSEN_PROFILE;
    
    /**
     * The parameter indicating that a filter was used and the available filter.
     */
    public static String REQUEST_FILTER;
    public static String FILTER_TYPE;
    public static String FILTER_LANGUAGE;
    public static String TYPE_TEXT;
    public static String TYPE_IMAGE;
    public static String TYPE_VIDEO;
    public static String TYPE_SOUND;
    
    public static String RESPONSE_ITEMS;
    
    /**
     * The default tag which is used when all other question tags are deleted.
     */
    public static String DEFAULT_TAG;

    /**
     * Number of guesses the user has to make a match.
     */
    public static int NUMBER_OF_GUESSES;

    /**
     * Number of results that are presented to the user after each search.
     */
    public static int NUMBER_OF_RESULTS;

    /**
     * The fields which are chosen to be displayed with the results.
     */
    public static String ITEM_DESCRIPTION;
    public static String ITEM_PREVIEW;
    public static String ITEM_TITLE;
    public static String ITEM_TYPE;
    public static String ITEM_ID;
    /**
     * The record link of the result.
     */
    public static String ITEM_LINK;

    /**
     * OWASP HTML Sanitizer policy - defines what kind of HTML code will be
     * accepted. In this case,
     * block elements like {@literal <p />}, {@literal <h1 />}, ... will be
     * allowed, as well as
     * formatting elements
     * (e.g. {@literal <b />}, {@literal <i />}, ...). Moreover,
     * {@literal <img />}-tags are
     * allowed, with HTTP, HTTPS
     * and relative sources, links ({@literal <a />}-tags) using the HTTP, HTTPS
     * and MAILTO
     * protocols plus relative
     * links are allowed, too. Additionally also certain safe style definitions
     * in
     * style="..."-attributes are allowed.
     * Finally HTML tables (using HTML tags {@literal <table />},
     * {@literal <tr />}, {@literal <td />}, ...) are
     * accepted using this Policy.
     */
    public static final PolicyFactory HTML_SANITIZER_POLICY = Sanitizers.BLOCKS
            .and(Sanitizers.FORMATTING)
            .and(Sanitizers.IMAGES)
            .and(Sanitizers.LINKS)
            .and(Sanitizers.STYLES)
            .and(new HtmlPolicyBuilder()
                    .allowElements("table", "thead", "tbody",
                            "tfoot", "tr", "th", "td")
                    .allowAttributes("class")
                    .onElements("table", "thead", "tbody",
                            "tfoot", "tr", "th", "td")
                    .toFactory());

    /**
     * Initialize constants with loaded data from file.
     */
    public static void init() {
        Properties prop = new Properties();
        ServletContext sc = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String path = sc.getRealPath("/WEB-INF/config.properties");

        File file = new File(path);
        try {
            prop.load(new FileInputStream(file));
            // initialize constants with loaded data

            DB_HOST = prop.getProperty("DB_HOST");
            DB_PORT = prop.getProperty("DB_PORT");
            DB_NAME = prop.getProperty("DB_NAME");
            DB_USER = prop.getProperty("DB_USER");
            DB_PASS = prop.getProperty("DB_PASS");

            MIN_CONNECTIONS = Integer.valueOf(prop.getProperty("MIN_CONNECTIONS"));
            MAX_CONNECTIONS = Integer.valueOf(prop.getProperty("MAX_CONNECTIONS"));

            REQUEST_URL = prop.getProperty("REQUEST_URL");
            REQUEST_KEY = prop.getProperty("REQUEST_KEY");
            API_ACTION = prop.getProperty("API_ACTION");
            REQUEST_ROWS = prop.getProperty("REQUEST_ROWS");
            REQUEST_QUERY = prop.getProperty("REQUEST_QUERY");
            REQUEST_PROFILE = prop.getProperty("REQUEST_PROFILE");
            PROFILE_PARAMS = prop.getProperty("REQUEST_PARAMS");
            PROFILE_STANDARD = prop.getProperty("REQUEST_STANDARD");
            PROFILE_MINIMAL = prop.getProperty("REQUEST_MINIMAL");
            CHOSEN_PROFILE = prop.getProperty("CHOSEN_PROFILE");
            REQUEST_FILTER = prop.getProperty("REQUEST_FILTER");
            FILTER_TYPE = prop.getProperty("FILTER_TYPE");
            FILTER_LANGUAGE = prop.getProperty("FILTER_LANGUAGE");
            TYPE_TEXT = prop.getProperty("TYPE_TEXT");
            TYPE_IMAGE = prop.getProperty("TYPE_IMAGE");
            TYPE_VIDEO = prop.getProperty("TYPE_VIDEO");
            TYPE_SOUND = prop.getProperty("TYPE_SOUND");
            RESPONSE_ITEMS = prop.getProperty("RESPONSE_ITEMS");
            ITEM_PREVIEW = prop.getProperty("ITEM_PREVIEW");
            ITEM_TITLE = prop.getProperty("ITEM_TITLE");
            ITEM_DESCRIPTION = prop.getProperty("ITEM_DESCRIPTION");
            ITEM_TYPE = prop.getProperty("ITEM_TYPE");
            ITEM_LINK = prop.getProperty("ITEM_LINK");
            ITEM_ID = prop.getProperty("ITEM_ID");
            NUMBER_OF_RESULTS = Integer.valueOf(prop.getProperty("NUMBER_OF_RESULTS"));
            NUMBER_OF_GUESSES = Integer.valueOf(prop.getProperty("NUMBER_OF_GUESSES"));
            DEFAULT_TAG = prop.getProperty("NO_TAG");

            IMAGE_STORING_LOCATION = prop.getProperty("IMAGE_STORING_LOCATION");
            if (!new File(IMAGE_STORING_LOCATION).exists()) {
                IMAGE_STORING_LOCATION = System.getProperty("java.io.tmpdir");
            }
            CSS_STORING_LOCATION = prop.getProperty("CSS_STORING_LOCATION");
            if (!new File(CSS_STORING_LOCATION).exists()) {
                CSS_STORING_LOCATION = System.getProperty("java.io.tmpdir");
            }
            ERROR_LOG_FILE_NAME = prop.getProperty("ERROR_LOG_FILE_NAME");

            ADMIN_EMAIL_ADDRESS = prop.getProperty("ADMIN_EMAIL_ADDRESS");
            ADMIN_NICKNAME = prop.getProperty("ADMIN_NICKNAME");

        } catch (NumberFormatException e) {
            // SYSTEM IS NOT WORKING IN THIS CASE
            LOGGER.severe("NumberFormatException in Config: Please check all values which need a integer");
        } catch (FileNotFoundException e) {
            // SYSTEM IS NOT WORKING IN THIS CASE
            LOGGER.severe("Config File not found! System doesn't work");
        } catch (IOException e) {
            // SYSTEM IS NOT WORKING IN THIS CASE
            LOGGER.severe("IOException in Config! System doesn't work");
        }

    }
}
