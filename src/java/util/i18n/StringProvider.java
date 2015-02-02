package cgwap.util.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;

/**
 * Provides overloaded methods to return strings out of the resource bundle in the current language.
 * 
 * 
 */
public class StringProvider {

    /**
     * Resource bundle name set in faces-config.
     */
    private static final String RESOURCE_BUNDLE_NAME = "cgwap.util.i18n.messages";

    /**
     * Returns the internationalized string associated with a specified key in the systems
     * message.properties file. The string can contain place holders, which will be replaced by the
     * given parameters.
     * 
     * @param key key in message.properties file
     * @param context FacesContext containing current locale
     * @param params parameters to replace place holders, if needed
     * @return string associated with key or default error string
     */
    public static String getString(String key, FacesContext context, String... params) {
        Locale locale = context.getViewRoot().getLocale();

        String text;
        ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, locale, new UTF8Control());

        try {
            text = bundle.getString(key);
        } catch (MissingResourceException e) {
            text = bundle.getString("keyNotFound");

            params = new String[] { key };
        }

        MessageFormat mf = new MessageFormat(text, locale);
        text = mf.format(params, new StringBuffer(), null).toString();

        return text;
    }

    /**
     * Returns the internationalized string associated with a specified key in the systems
     * message.properties file. The string can contain place holders, which will be replaced by the
     * given parameters. If current FacesContext is available getString(String, Object,
     * FacesContext) is to prefer.
     * 
     * @param key key in message.properties file
     * @param params parameters to replace place holders, if needed
     * @return string associated with key or default error string
     */
    public static String getString(String key, String... params) {
        FacesContext context = FacesContext.getCurrentInstance();
        return getString(key, context, params);
    }
}