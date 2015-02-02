package cgwap.util.exception_handler;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

/**
 * Handles exceptions and redirects on a common error page.
 * 
 * 
 */
public class CGWAPExceptionHandler extends ExceptionHandlerWrapper {

    private static final Logger LOGGER = Logger.getLogger(CGWAPExceptionHandler.class.getCanonicalName());
    private ExceptionHandler    wrapped;

    /**
     * Constructor with parameter to set wrapped ExceptionHandler
     * 
     * @param exception - ExceptionHandler to wrap
     */
    public CGWAPExceptionHandler(ExceptionHandler exception) {
        this.wrapped = exception;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    /**
     * Handles exception by logging it into log file and navigating on error page displaying
     * exception message.
     */
    @Override
    public void handle() throws FacesException {
        final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();

        while (i.hasNext()) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

            // get the exception from context
            Throwable t = context.getException();

            final FacesContext facesContext = FacesContext.getCurrentInstance();
            final NavigationHandler nav = facesContext.getApplication().getNavigationHandler();

            // here you do what ever you want with exception
            try {
                LOGGER.log(Level.SEVERE, "Exception handeled by GGWAPExceptionHandler", t);

                // redirect to error page
                nav.handleNavigation(facesContext, null, "/error/500.html");
                facesContext.renderResponse();

            } finally {
                i.remove();
            }
        }

        // parent handle
        getWrapped().handle();
    }
}
