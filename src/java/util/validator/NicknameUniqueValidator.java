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
 * Checks if a nickname already exists in the system's database.
 * 
 * 
 */
@FacesValidator(value = "nicknameUniqueValidator")
public class NicknameUniqueValidator implements Validator {

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        User entity = new User();
        entity.setNickname((String) value);

     
        try {
           
            if (UserDatabaseAccess.doesNicknameExist(entity)) {
                
                    String key = StringProvider.getString("nicknameUsed", context);
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, key, null);
                    throw new ValidatorException(message);
            }
        } catch (ApplicationException e) {
            Logger.getLogger(NicknameUniqueValidator.class.getCanonicalName()).log(Level.WARNING,
                    "Checking nickname's existence failed. ", e);
            String key = StringProvider.getString("retry", context);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, key, null);
            throw new ValidatorException(message);
        }
    }
}
