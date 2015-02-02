package cgwap.backing_beans;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import cgwap.data_access.QuestionDatabaseAccess;
import cgwap.data_access.RoundsDatabaseAccess;
import cgwap.data_access.TagsDatabaseAccess;
import cgwap.data_access.UserDatabaseAccess;
import cgwap.entities.Question;
import cgwap.entities.QuestionTag;
import cgwap.entities.User;
import cgwap.enums.UserLevel;
import cgwap.util.exception_handler.ApplicationException;
import cgwap.util.i18n.StringProvider;
import cgwap.util.session.SessionBean;

/**
 * This managed bean contains all user-related action methods and business
 * logic. The User attribute of this class is automatically loaded if the bean
 * is used and the User of the system is logged in.
 * 
 * 
 */

@ManagedBean
@RequestScoped
public class UserAction {

    private User user;
    private User userToManage = new User();
    private float percentToNextLevel;
    private int xpToNextLevel;
    private boolean hasAskedQuestions;
    private boolean hasAnsweredQuestions;

    List<Question> usersAnsweredQuestions = new LinkedList<Question>();
    List<Question> usersAskedQuestions = new LinkedList<Question>();

    private static final String ERROR_500_PATH = "/error/500.html";

    @ManagedProperty(value = "#{sessionBean}")
    protected SessionBean session;

    /**
     * When a User is logged-in this method loads the Userdata from the database
     * when this managed bean is used on a Facelet.
     */
    @PostConstruct
    public void init() {
        user = new User();
        // tokenEntity = new Token();

        if (session.isLoggedIn() && session.getCurrentUserId() == 0) {
            session.logout();
        }
        if (session.isLoggedIn()) {
            try {
                User currentUser = new User(session.getCurrentUserId());
                User newUser = UserDatabaseAccess.getById(currentUser);
                setUser(newUser);

                usersAnsweredQuestions = getUsersAnsweredQuestions();
                usersAskedQuestions = getUsersQuestions();

            } catch (ApplicationException e) {
                // redirect to error-page
                FacesContext context = session.getBroker().getFacesContext();
                try {
                    context.getExternalContext().redirect(ERROR_500_PATH);
                } catch (IOException e1) {
                    // cannot react practical on this Exception.
                    // This Exception may not be thrown, because otherwise this
                    // method is not called
                    // by JSF.
                }
            }
        }
    }

    // /**
    // * loads a list all of Users of the system
    // *
    // * @return the list of users
    // * @throws ApplicationException
    // */
    // public List<User> loadUsers() throws ApplicationException {
    // List<User> result = UserDatabaseAccess.fetch();
    // java.util.Collections.sort(result);
    // return result;
    // }

    /**
     * loads specified user by email
     * 
     * @param entity
     *            the user to load by email
     * @return success, if loading was successful, otherwise fail
     * @throws ApplicationException
     */
    public User loadUser(User entity) throws ApplicationException {
        User loadedUser = UserDatabaseAccess.getByEmail(entity);

        if (loadedUser != null) {
            return user;
        } else {
            session.setError("Email doesn't map to a user.");
            return null;
        }
    }

    /**
     * Logs in a User and redirects him according to the navigation-rules.
     * 
     * @param redirect
     *            where to redirect after login
     * @return success if login was successful, otherwise fail
     * @throws ApplicationException
     */
    public String login(String redirect) throws ApplicationException {
        String result = login();
        if (result == "success") {
            result = redirect;
        }
        return result;
    }

    /**
     * Logs in a User and redirects him according to the navigation-rules.
     * 
     * @return success if login was successful, otherwise fail
     * @throws ApplicationException
     */
    public String login() throws ApplicationException {
        user = UserDatabaseAccess.getByLogin(user);
        if (user != null && user.isActive()) {
            session.setUserLoggedIn(user.getId());
            return "success";
        }

        else {
            String error = StringProvider.getString("loginFailed",
                    session.getFacesContext());
            session.setError(error);
            return "fail";
        }
    }

    /**
     * Logs out the current User.
     * 
     * @return success, when user is logged out
     */
    public String logout() {
        session.logout();
        return "success";
    }

    /**
     * Registers the User to the system.
     * 
     * @return success, if User is registered successful.
     * @throws ApplicationException
     */
    public String register() throws ApplicationException {
        String registered = "fail";

        user.setLevel(UserLevel.Questling);
        user = UserDatabaseAccess.store(user);

        if (user.getId() > 0) {

            // provide success message to user

            session.setUserLoggedIn(user.getId());

            session.setInfo(StringProvider.getString("signedUpSuccess"));

            registered = "success";
        }
        return registered;
    }

    /**
     * Stores the changes and forwards according to navigation rules.
     * 
     * @return success, if updating was successful, otherwise fail
     * @throws ApplicationException
     */
    public String update() throws ApplicationException {
        if (UserDatabaseAccess.update(user)) {
            return "success";
        } else {
            return "fail";
        }
    }

    /**
     * Updates XP and level, if necessary.
     * 
     * @param score
     */
    public static void updateXp(int score, SessionBean session) throws ApplicationException {
        User currentUser = UserDatabaseAccess.getById(new User(session.getCurrentUserId()));
        currentUser.setXp(currentUser.getXp() + score);
        UserLevel level = currentUser.getUserLevel();

        UserLevel nextLevel = UserLevel.values()[level.ordinal() + 1];
        // user levels up if his new xp value exceeds the one of the next level
        if (nextLevel.getXp() < currentUser.getXp()) {

            currentUser.setLevel(UserLevel.values()[level.ordinal() + 1]);
            level = currentUser.getUserLevel();
            session.setInfo(StringProvider.getString("levelUp") + UserLevel.values()[level.ordinal() - 1]
                    + " " + StringProvider.getString("levelUp1") + currentUser.getLevel()
                    + StringProvider.getString("levelUp2") + UserLevel.values()[level.ordinal() + 1] + ".");
        }
        UserDatabaseAccess.update(currentUser);

    }

    /**
     * Allows to change the user's admin role.
     * 
     * @return "success" if change was successful, otherwise "fail"
     * @throws ApplicationException
     */
    public String updateUserIsAdmin() throws ApplicationException {
        boolean success = false;

        // swap to handle overwritten userToManage
        boolean shouldBeAdmin = userToManage.getIsAdmin();
        userToManage = UserDatabaseAccess.getByEmail(userToManage);
        userToManage.setAdmin(shouldBeAdmin);

        if (userToManage != null) {
            if (UserDatabaseAccess.update(userToManage)) {
                session.setInfo(StringProvider
                        .getString("succDataChanged"));
                success = true;
            }
        }

        if (success) {
            return "success";
        } else
            return "fail";
    }

    /**
     * Deletes the current account from the system.
     * 
     * @return success, if deleting was successful, otherwise fail
     * @throws ApplicationException
     */
    public String deleteAccount() throws ApplicationException {
        String result = "fail";

        if (user == null || 0 >= user.getId()) { // only possible if
                                                 // logged in
            session.setError(StringProvider
                    .getString("NotLoggedInDeletingFail"));
        } else {
            if (UserDatabaseAccess.remove(user)) {
                // log out the current user (as the logged in account no longer
                // exists)
                session.logoutWithoutInvalidation();

                // provide success message to user
                session.setInfo(StringProvider
                        .getString("DeletingSuccess"));
                result = "success";
            } else {
                session.setError(StringProvider.getString("DeletingFail"));
            }
        }

        return result;
    }

    /**
     * Gets all questions the user has asked.
     * 
     * @return the user's asked Questions
     * @throws ApplicationException
     */
    public List<Question> getUsersQuestions() throws ApplicationException {
        List<Question> userQuestions = QuestionDatabaseAccess.getQuestionsByUserId(getUser().getId());
        if (!userQuestions.isEmpty()) {
            hasAskedQuestions = true;
        } else {
            hasAskedQuestions = false;
        }
        return userQuestions;
    }

    /**
     * Gets all questions the user has answered.
     * 
     * @return the user's asked Questions
     * @throws ApplicationException
     */
    public List<Question> getUsersAnsweredQuestions() throws ApplicationException {
        List<Integer> questionIds = RoundsDatabaseAccess.getRoundIdsByUserId(getUser().getId(), true);

        List<Question> questions = new LinkedList<Question>();
        if (!questionIds.isEmpty()) {
            hasAnsweredQuestions = true;
            for (Integer id : questionIds) {
                Question question = new Question(id);
                questions.add(QuestionDatabaseAccess.getQuestionById(question));
            }
        } else {
            hasAnsweredQuestions = false;
        }

        return questions;
    }

    /**
     * Gets all tags according to the chosen question.
     * 
     * @return the tags as String
     * @throws ApplicationException
     */
    public String getTagsPerQuestions() throws ApplicationException {
        String tags = "";

        tags = tagsAsString(TagsDatabaseAccess.fetch(false));

        return tags;

    }

    private String tagsAsString(List<QuestionTag> questionTagList) {
        String tagString = "";

        for (QuestionTag questionTag : questionTagList) {
            tagString.concat(questionTag.getTag() + ", ");

        }

        return tagString;
    }

    /**
     * Calculates how much xp the user needs to reach the next level.
     * 
     * @return
     *         the xp
     */
    public int getXpToGo() {
        UserLevel currentLevel = user.getUserLevel();
        return UserLevel.getNext(currentLevel).getXp() - user.getXp();
    }

    /**
     * 
     * @return
     *         the users next reachable level
     */
    public UserLevel getNextLevel() {
        return UserLevel.getNext(user.getUserLevel());
    }

    /**
     * @return the user
     */
    public User getUser() {

        return user;
    }

    /**
     * @param user
     *            the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the session
     */
    public SessionBean getSession() {
        return session;
    }

    /**
     * @param session
     *            the session to set
     */
    public void setSession(SessionBean session) {
        this.session = session;
    }

    public int getPercentToNextLevel() {
        float currentXp = user.getXp();
        float nextXp = UserLevel.getNext(user.getUserLevel()).getXp();
        percentToNextLevel = (currentXp / nextXp) * 100;
        return (int) percentToNextLevel;

    }

    public void setPercentToNextLevel(float percentToNextLevel) {
        this.percentToNextLevel = percentToNextLevel;
    }

    public int getXpToNextLevel() {
        xpToNextLevel = (UserLevel.getNext(user.getUserLevel()).getXp()) - user.getXp();
        return xpToNextLevel;
    }

    public void setXpToNextLevel(int xpToNextLevel) {
        this.xpToNextLevel = xpToNextLevel;
    }

    public boolean isHasAskedQuestions() {
        return hasAskedQuestions;
    }

    public void setHasAskedQuestions(boolean hasAskedQuestions) {
        this.hasAskedQuestions = hasAskedQuestions;
    }

    public boolean isHasAnsweredQuestions() {
        return hasAnsweredQuestions;
    }

    public void setHasAnsweredQuestions(boolean hasAnsweredQuestions) {
        this.hasAnsweredQuestions = hasAnsweredQuestions;
    }

    public List<Question> getUsersAskedQuestions() {
        return usersAskedQuestions;
    }

    public void setUsersAskedQuestions(List<Question> usersAskedQuestions) {
        this.usersAskedQuestions = usersAskedQuestions;
    }

    public void setUsersAnsweredQuestions(List<Question> usersAnsweredQuestions) {
        this.usersAnsweredQuestions = usersAnsweredQuestions;
    }

}
