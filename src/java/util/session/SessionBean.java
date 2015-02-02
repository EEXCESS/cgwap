package cgwap.util.session;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 * SessionBean manages all methods to interact with the session. For every
 * logged in user it stores
 * only the userId in Session.
 */
@ManagedBean
@ViewScoped
public class SessionBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2681990698041167482L;

    @ManagedProperty("#{facesBroker}")
    private FacesBroker facesBroker;

    /**
     * The key under which the users' id is stored in session to remember login
     * state.
     */
    public final static String SESSION_USER_KEY = "current-user";

    // -------------------------------------------------------------------------
    // Getter / Setter
    // -------------------------------------------------------------------------

    /**
     * @return FacesBroker for UnitTesting
     */
    public FacesBroker getBroker() {
        return this.facesBroker;
    }

    /**
     * @param facesBroker
     *            - FacesBroker for UnitTesting
     */
    public void setFacesBroker(FacesBroker facesBroker) {
        this.facesBroker = facesBroker;
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------
    /**
     * Adds a levelUp message to be displayed to the user.
     * 
     * @param error
     *            - error message to declare in faces context
     */
    public void setLevelUp(String level) {
        FacesContext facesContext = facesBroker.getFacesContext();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, level, null));
    }

    /**
     * Adds an error message to be displayed to the user.
     * 
     * @param error
     *            - error message to declare in faces context
     */
    public void setError(String error) {
        FacesContext facesContext = facesBroker.getFacesContext();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, error, null));
    }

    /**
     * Adds an info message to be displayed to the user.
     * 
     * @param info
     *            - info message to declare in faces context
     */
    public void setInfo(String info) {
        FacesContext facesContext = facesBroker.getFacesContext();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, info, null));
    }

    /**
     * Check if error messages are present to present.
     * 
     * @return true, if error messages are present
     */
    public boolean isHasErrorMessage() {
        FacesContext context = facesBroker.getFacesContext();
        return !context.getMessageList().isEmpty()
                &&
                context.getMaximumSeverity().equals(FacesMessage.SEVERITY_ERROR);
    }

    /**
     * Check if levelUp messages are present to present.
     * 
     * @return true, if messages are present
     */
    public boolean isHasLevelMessage() {
        FacesContext context = facesBroker.getFacesContext();
        return !context.getMessageList().isEmpty()
                &&
                context.getMaximumSeverity().equals(FacesMessage.SEVERITY_WARN);
    }

    /**
     * Check if info messages are present to present.
     * 
     * @return true, if info messages are present
     */
    public boolean isHasInfoMessage() {
        FacesContext context = facesBroker.getFacesContext();
        return !context.getMessageList().isEmpty()
                &&
                context.getMaximumSeverity().equals(FacesMessage.SEVERITY_INFO);
    }

    /**
     * Reads the user id which is stored in the session.
     * 
     * @return userId, otherwise 0
     */
    public int getCurrentUserId() {
        Object sessionUser = this.getSessionAttribute(SessionBean.SESSION_USER_KEY);

        if (sessionUser != null && sessionUser instanceof Integer) {
            return (Integer) sessionUser;
        } else {
            return 0;
        }
    }

    /**
     * Sets the user id in the session, while creating a new session for that to
     * prevent session
     * fixation.
     * 
     * @param userId
     *            - the user id to store
     */
    public void setUserLoggedIn(int userId) {
        // prevent session fixation
        HttpSession session = this.getSession();
        synchronized (session) {
            session.invalidate();
        }

        this.setSessionAttribute(SessionBean.SESSION_USER_KEY, userId);
    }

    /**
     * Checks if a user-id is stored in the session.
     * 
     * @return true, if a user id is set
     */
    public boolean isLoggedIn() {
        Object sessionUser = this.getSessionAttribute(SessionBean.SESSION_USER_KEY);
        return sessionUser != null && sessionUser instanceof Integer;
    }

    /**
     * Removes the user id from session and invalidates the session to logout a
     * user.
     */
    public void logout() {
        HttpSession session = this.getSession();

        synchronized (session) {
            session.removeAttribute(SessionBean.SESSION_USER_KEY);
            session.invalidate();
        }
    }

    /**
     * Removes the user id from session the session to logout a user, but keeps
     * the other session
     * data.
     */
    public void logoutWithoutInvalidation() {
        HttpSession session = this.getSession();

        synchronized (session) {
            session.removeAttribute(SessionBean.SESSION_USER_KEY);
        }
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------
    /**
     * Uses the FacesBroker to return the faces context.
     * 
     * @return the current faces context
     */
    public FacesContext getFacesContext() {
        return facesBroker.getFacesContext();
    }

    /**
     * Get any value stored for a key in the session.
     * 
     * @param key
     *            - the key to search the value for
     * @return the found value, otherwise null
     */
    private Object getSessionAttribute(String key) {
        HttpSession session = this.getSession();
        Object attribute = null;

        synchronized (session) {
            attribute = session.getAttribute(key);
        }

        return attribute;
    }

    /**
     * Set any value named by a key in the session.
     * 
     * @param key
     *            - the key to save the value for
     * @param obj
     *            - the value
     */
    private void setSessionAttribute(String key, Object obj) {
        HttpSession session = this.getSession();

        synchronized (session) {
            session.setAttribute(key, obj);
        }
    }

    /**
     * Returns the session using the facesBroker.
     * 
     * @return the current session
     */
    private HttpSession getSession() {
        HttpSession session = (HttpSession) facesBroker.getFacesContext().getExternalContext().getSession(true);
        return session;
    }

}
