package cgwap.util.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import cgwap.util.i18n.StringProvider;

/**
 * Checks if the format of a mail address is valid.
 * 
 * 
 */
@FacesValidator(value = "emailValidator")
public class EmailValidator implements Validator {

    private static Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String email = (String) value;

        if (email != null) {
            if (!checkEmail(email)) {
                String key = StringProvider.getString("invalidEmail", context);
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, key, null);
                throw new ValidatorException(message);
            }
        }
    }

    /**
     * Method to check whether given email is of valid format, using regular
     * expressions. Empty mail is also possible.
     * 
     * @param email The email to be checked.
     * @return True, if the email has a valid format or is empty.
     */
    private boolean checkEmail(String email) {
        Matcher m = EmailValidator.pattern.matcher(email);
        return (m.matches() || email.equals(""));
    }
}
