package cgwap.util.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import cgwap.util.i18n.StringProvider;

/**
 * Checks if the format of an url is valid.
 * 
 * 
 */
@FacesValidator(value = "europeanaUrlValidator")
public class EuropeanaUrlValidator implements Validator {

    private static String europeanaUrl = "http://www.europeana.eu/portal/record/";

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String url = (String) value;
        String[] urls = splitByNewLine(url);

        if (url != null) {
            for (String urlToCheck : urls) {

                if (!urlToCheck.contains(europeanaUrl)) {
                    String key = StringProvider.getString("invalidUrl", context);
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, key, null);
                    throw new ValidatorException(message);
                }
            }
        }
    }

    /**
     * Method to split urls by new line.
     * 
     * @param urls
     *            the urls to split
     * @return
     */

    public String[] splitByNewLine(String urls) {
        String lines[] = urls.split("[\\r\\n]+");
        return lines;
    }

}
