package cgwap.util.session;

import java.io.ObjectStreamException;
import java.io.Serializable;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

/**
 * FacesBroker allows injection of FacesContext for UnitTesting. Managed Bean are able to get the
 * FacesBroker injected by JSF. So they will get access to the current FacesContext. UnitTesting is
 * possible without changes in the actual code because it is possible to set a mocked FacesBroker
 * using the setter where it is needed.
 * 
 * @see <a href="http://illegalargumentexception.blogspot.de/2011/12/jsf-mocking-facescontext-for-unit-tests.html">
 *   http://illegalargumentexception.blogspot.de/2011/12/jsf-mocking-facescontext-for-unit-tests.html
 * </a>
 */
@ManagedBean
@ApplicationScoped
public class FacesBroker implements Serializable {

    private static final long        serialVersionUID = 8606250182378022664L;

    private static final FacesBroker INSTANCE         = new FacesBroker();

    /**
     * Return the FacesContext instance for the request that is being processed by the current
     * thread. If called during application initialization or shutdown, any method documented as
     * "valid to call this method during application startup or shutdown" must be supported during
     * application startup or shutdown time. The result of calling a method during application
     * startup or shutdown time that does not have this designation is undefined.
     * 
     * @return the current FacesContext
     */
    public FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }

}
