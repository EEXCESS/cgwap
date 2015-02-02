package cgwap.util.validator;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;

import cgwap.data_access.UserDatabaseAccess;
import cgwap.entities.User;
import cgwap.util.exception_handler.ApplicationException;
import cgwap.util.i18n.StringProvider;
import cgwap.util.session.FacesBroker;
import cgwap.util.session.SessionBean;

/**
 * Checks if a mail address already exists in the system's database.
 * 
 * 
 */
@FacesValidator(value = "emailExistsValidator")
public class EmailExistsValidator implements Validator {

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        User entity = new User();
        entity.setEmail((String) value);

        try {

            if (UserDatabaseAccess.doesEmailExist(entity)) {
               
                    String key = StringProvider.getString("emailUsed", context);
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, key, null);
                    throw new ValidatorException(message);
                }
        } catch (ApplicationException e) {
            Logger.getLogger(EmailExistsValidator.class.getCanonicalName()).log(Level.WARNING,
                    "Checking email's existence failed. ", e);
            String key = StringProvider.getString("retry", context);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, key, null);
            throw new ValidatorException(message);
        }
    }
}
