package cgwap.util.validator;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import cgwap.data_access.QuestionDatabaseAccess;
import cgwap.entities.Question;
import cgwap.util.exception_handler.ApplicationException;
import cgwap.util.i18n.StringProvider;

/**
 * Checks if a mail address already exists in the system's database.
 * 
 * 
 */
@FacesValidator(value = "questionExistsValidator")
public class QuestionExistsValidator implements Validator {

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        Question entity = new Question();
        entity.setQuestionText((String) value);

        try {

            if (QuestionDatabaseAccess.countQuestionByText(entity) == 1) {
                String key = StringProvider.getString("questionUsed", context);
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, key, null);
                throw new ValidatorException(message);
            }
        } catch (ApplicationException e) {
            Logger.getLogger(QuestionExistsValidator.class.getCanonicalName()).log(Level.WARNING,
                    "Checking question'ss existence failed. ", e);
            String key = StringProvider.getString("retry", context);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, key, null);
            throw new ValidatorException(message);
        }
    }
}
