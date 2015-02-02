package cgwap.backing_beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import cgwap.data_access.AnswersDatabaseAccess;
import cgwap.data_access.QuestionDatabaseAccess;
import cgwap.data_access.TagsDatabaseAccess;
import cgwap.entities.Answer;
import cgwap.entities.Question;
import cgwap.entities.QuestionTag;
import cgwap.util.exception_handler.ApplicationException;
import cgwap.util.i18n.StringProvider;
import cgwap.util.session.SessionBean;

/**
 * This managed bean handles the presentation of a question.
 * 
 */
@ManagedBean
@ViewScoped
public class QuestionAction implements Serializable {

    private static final long serialVersionUID = 3629203678414180913L;

    private static final String DEFAULT_TAG = "misc";

    @ManagedProperty(value = "#{sessionBean}")
    protected SessionBean session;

    protected List<Answer> answerUrl = new LinkedList<Answer>();
    protected Question question = new Question();

    // Change Question Facelet
    protected String newQuestionText = "";
    protected String newTag = "";
    protected String newAnswerUrl = "";

    protected List<QuestionTag> questionTags = new LinkedList<QuestionTag>();

    /**
     * Used for loading the current question.
     * 
     * @throws ApplicationException
     */
    public void initQuestion() throws ApplicationException {
        question = QuestionDatabaseAccess.getQuestionById(question);
        if (question != null) {

            answerUrl = AnswersDatabaseAccess.getAnswerUrlByQuestionId(question.getId());
            questionTags = TagsDatabaseAccess.getTagsByQuestionId(question.getId());
        }
    }

    /**
     * Used for loading a question by its id.
     * 
     * @throws ApplicationException
     */
    public String initQuestion(int questionId) throws ApplicationException {
        question.setId(questionId);
        question = QuestionDatabaseAccess.getQuestionById(question);
        answerUrl = AnswersDatabaseAccess.getAnswerUrlByQuestionId(question.getId());
        questionTags = TagsDatabaseAccess.getTagsByQuestionId(question.getId());
        return "success";
    }

    /**
     * Used for loading the most recently asked question.
     * 
     * @throws ApplicationException
     */
    public void initAskedQuestion() throws ApplicationException {
        question =
                QuestionDatabaseAccess.getAskedQuestion(session.getCurrentUserId());
        answerUrl =
                AnswersDatabaseAccess.getAnswerUrlByQuestionId(question.getId());
        questionTags =
                TagsDatabaseAccess.getTagsByQuestionId(question.getId());
    }

    /**
     * Checks whether user has the right to edit a specific question.
     * 
     * @param questionId
     *            the question the user wants to access
     * @return true if user is allowed, false otherwise
     * @throws ApplicationException
     */

    public boolean belongsToUser() throws ApplicationException {
        if (question == null) {
            return false;
        } else {
            return QuestionDatabaseAccess.doesUserOwn(session.getCurrentUserId(), question.getId());
        }
    }

    // /**
    // * Used for loading the most recently changed question.
    // *
    // * @param questionID
    // * @return
    // * whether the loading was successful
    // * @throws ApplicationException
    // *
    // *
    // */
    // public void initModifiedQuestion() throws ApplicationException {
    // question =
    // QuestionDatabaseAccess.getModifiedQuestion(session.getCurrentUserId());
    // question = QuestionDatabaseAccess.getQuestionById(question);
    // answerUrl =
    // AnswerUrlsDatabaseAccess.getAnswerUrlByQuestionId(question.getId());
    // questionTags =
    // TagsDatabaseAccess.getTagsByQuestionId(question.getId());
    // }

    /**
     * Removes a question of user's choice.
     * 
     * @param questionToRemove
     *            the entity to delete
     * @return
     *         success, if deleting was successful, otherwise fail
     * @throws ApplicationException
     */

    public String removeQuestion(Question questionToRemove) throws ApplicationException {

        String result = "fail";

        if (QuestionDatabaseAccess.delete(questionToRemove)) {
            session.setInfo(StringProvider.getString("questRem"));
            result = "success";

            // if questionToRemove was a follow up question which predecessor is
            // still an active question, the predecessor has to be updated
            if (questionToRemove.isHas_follow_up_question() && questionToRemove.getPrevious_question_id() != 0) {
                Question predecessor = QuestionDatabaseAccess.getQuestionById(new Question(questionToRemove
                        .getPrevious_question_id()));
                predecessor.setHas_follow_up_question(false);
                QuestionDatabaseAccess.update(predecessor);

            }

        } else {
            session.setInfo(StringProvider.getString("noChange"));
        }
        return result;
    }

    public String updateQuestion(String newQuestionText) throws ApplicationException {
        Question newQuestion = QuestionDatabaseAccess.getQuestionById(question);

        // if new text is an empty string or exactly the same value
        if (newQuestionText.equals("") || newQuestion.getQuestionText().equals(newQuestionText)) {
            session.setError(StringProvider.getString("noChanges"));
            session.setError(StringProvider.getString("changesReq"));

        } else {
            question.setQuestionText(newQuestionText);
            question = QuestionDatabaseAccess.update(question);
            session.setInfo(StringProvider.getString("succChangesd"));
        }
        return "success";

    }

    /**
     * Adds an answer of user's choice.
     * 
     * @param new AnswerUrl
     *        the new url
     * @return
     *         success, if adding was successful, otherwise fail
     * @throws ApplicationException
     */
    public String addAnswerUrl(String newAnswerUrl) throws ApplicationException {

        String result;

        if (!newAnswerUrl.equals("")) {
            Answer toAdd = new Answer(question.getId(), newAnswerUrl);

            answerUrl = AnswersDatabaseAccess.getAnswerUrlByQuestionId(question.getId());

            if (answerUrl.contains(toAdd)) {
                session.setError(StringProvider.getString("noChanges"));
                session.setError(StringProvider.getString("answerExists"));
                result = "fail";
            }

            else {
                toAdd = AnswersDatabaseAccess.store(toAdd);
                // update answer urls so that all new answers are displayed
                answerUrl = AnswersDatabaseAccess.getAnswerUrlByQuestionId(question.getId());
                session.setInfo(StringProvider.getString("succChanges"));
                result = "success";
            }
        } else {
            session.setError(StringProvider.getString("noChanges"));
            session.setError(StringProvider.getString("changesReq"));
            result = "fail";
        }
        return result;
    }

    /**
     * Removes an answer of user's choice.
     * 
     * @param answerToRemove
     *            the entity to delete
     * @return
     *         success, if deleting was successful, otherwise fail
     * @throws ApplicationException
     */

    public String removeAnswer(Answer answerToRemove) throws ApplicationException {

        String result = "fail";
        // if entity is the last available answer
        if (AnswersDatabaseAccess.countPerQuestion(answerToRemove.getQuestionId()) <= 1) {
            session.setError(StringProvider.getString("noChanges"));
            session.setError(StringProvider.getString("deleteAnswer"));
            result = "fail";
        } else {

            if (AnswersDatabaseAccess.delete(answerToRemove)) {
                answerUrl = AnswersDatabaseAccess.getAnswerUrlByQuestionId(question.getId());
                session.setInfo(StringProvider.getString("succChanges"));
                result = "success";
            }
        }
        return result;
    }

    /**
     * Adds a tag of user's choice.
     * 
     * @param new tag
     *        the new tag
     * @return
     *         success, if adding was successful, otherwise fail
     * @throws ApplicationException
     */
    public String addTag(String newTag) throws ApplicationException {

        String result;

        if (!newTag.equals("")) {
            QuestionTag toAdd = new QuestionTag(question.getId(), newTag.toLowerCase());

            // load tags currently in db
            questionTags = TagsDatabaseAccess.getTagsByQuestionId(question.getId());

            if (questionTags.contains(toAdd)) {
                session.setError(StringProvider.getString("noChanges"));
                session.setError(StringProvider.getString("tagExists"));
                result = "fail";
            }

            else {
                toAdd = TagsDatabaseAccess.store(toAdd);
                if (toAdd != null)
                    // update so that all new tags are displayed
                    questionTags = TagsDatabaseAccess.getTagsByQuestionId(question.getId());
                session.setInfo(StringProvider.getString("succChanges"));
                result = "success";
            }
        } else {
            session.setError(StringProvider.getString("noChanges"));
            session.setError(StringProvider.getString("changesReq"));
            result = "fail";
        }
        return result;
    }

    /**
     * Removes a tag of user's choice.
     * 
     * @param tagToRemove
     *            the entity to delete
     * @return
     *         success, if deleting was successful, otherwise fail
     * @throws ApplicationException
     */

    public String removeTag(QuestionTag tagToRemove) throws ApplicationException {

        String result = "fail";

        // if user attempts to delete default tag and no other tag is present
        if (tagToRemove.getTag().equals(DEFAULT_TAG) && TagsDatabaseAccess.countPerQuestion(question.getId()) == 1) {
            session.setError(StringProvider.getString("noChanges"));
            session.setError(StringProvider.getString("deleteTag"));
            result = "fail";

        } else {
            if (TagsDatabaseAccess.delete(tagToRemove)) {

                // if it was the last tag left, the default tag has to be stored
                if (TagsDatabaseAccess.countPerQuestion(question.getId()) == 0) {
                    TagsDatabaseAccess.store(new QuestionTag(question.getId(), DEFAULT_TAG));
                }

                questionTags = TagsDatabaseAccess.getTagsByQuestionId(question.getId());
                session.setInfo(StringProvider.getString("succChanges"));
                result = "success";
            }
        }

        return result;
    }

    /**
     * Changes a questions difficulty rating.
     * 
     * @throws ApplicationException
     */
    public void changeRating() throws ApplicationException {
        changeRating(question);
        
        //Users should not get XP for rating their own question
        if (!QuestionDatabaseAccess.doesUserOwn(session.getCurrentUserId(), question.getId())) {
            UserAction.updateXp(2, session);
            session.setInfo(StringProvider.getString("thanksForRating"));
        }
    }

    /**
     * Changes a questions difficulty rating. (simple method, could be improved
     * by storing the number for every rating value in the db)
     * 
     * @return the new Rating
     * @throws ApplicationException
     */
    public static float changeRating(Question questionToRate) throws ApplicationException {
        float newRating = Float.valueOf(FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap()
                .get("rating"));

        float oldRating = questionToRate.getDifficultyRating();
        float ratingCounter = questionToRate.getRatingCounter();

        if (oldRating < newRating & ratingCounter != 0) {
            newRating = oldRating + newRating / ratingCounter;
        }
        if (oldRating > newRating & ratingCounter != 0) {
            newRating = oldRating - newRating / ratingCounter;
        }

        questionToRate.setDifficultyRating(newRating);
        questionToRate.setRatingCounter(+1);
        questionToRate = QuestionDatabaseAccess.update(questionToRate);

        return newRating;
    }

    protected String[] splitByNewLine(String urls) {
        String lines[] = urls.split("[\\r\\n]+");
        return lines;
    }

    public SessionBean getSession() {
        return session;
    }

    public void setSession(SessionBean session) {
        this.session = session;
    }

    public List<Answer> getAnswerUrl() {
        return answerUrl;
    }

    public void setAnswerUrl(List<Answer> answerUrl) {
        this.answerUrl = answerUrl;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<QuestionTag> getQuestionTags() {
        return questionTags;
    }

    public void setQuestionTags(List<QuestionTag> questionTags) {
        this.questionTags = questionTags;
    }

    public Question getJustAskedQuestion() throws ApplicationException {
        return question;
    }

    public void setJustAskedQuestion(Question justAskedQuestion) {
        this.question = justAskedQuestion;
    }

}