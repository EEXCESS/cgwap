package cgwap.util.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import cgwap.util.i18n.StringProvider;

/**
 * Checks if the password fulfills the systems security requirements (specific length and not only
 * numbers).
 * 
 */
@FacesValidator(value = "passwordValidator")
public class PasswordValidator implements Validator {

    /**
     * Minimal allowed password length.
     */
    private static final int MIN_PASS_LENGTH = 6;

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String password = (String) value;

        if (password.length() < MIN_PASS_LENGTH) {
            String key = StringProvider.getString("shortPassword", context);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, key, null);
            throw new ValidatorException(message);
        }

        if (password.matches("[0-9]+")) {
            String key = StringProvider.getString("numericPassword", context);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, key, null);
            throw new ValidatorException(message);
        }
    }
}
