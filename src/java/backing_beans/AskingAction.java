package cgwap.backing_beans;

import java.io.Serializable;

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
import cgwap.util.xp_calculator.XpCalculator;

/**
 * This managed bean handles the ask functionality.
 * 
 */
@ManagedBean
@ViewScoped
public class AskingAction implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7290675709440919709L;

    @ManagedProperty(value = "#{sessionBean}")
    protected SessionBean session;

    protected Answer answer = new Answer();
    protected Question question = new Question();
    protected Question followUp = new Question();
    protected QuestionTag questionTags = new QuestionTag();

    // Default value if user didn't enter any tags.
    protected static final String NO_TAGS_GIVEN = "misc";

    /**
     * Used for loading the current question.
     * 
     * @throws ApplicationException
     */
    public void initQuestion() throws ApplicationException {
        question = QuestionDatabaseAccess.getQuestionById(question);
    }

    /**
     * Stores a new question. Forwards according to navigation rules.
     * 
     * @return success, if storing was successful, otherwise fail
     * @throws ApplicationException
     */
    public String store(String askFollowUps) throws ApplicationException {
        String result = "fail";

        // specify user
        question.setUserId(session.getCurrentUserId());

        question.setDifficultyRating(Integer.valueOf(FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap()
                .get("rating")));

        // store Question, Answer and Tags

        if (askFollowUps.equals("hasFollowUps")) {
            question.setHas_follow_up_question(true);
        }

        question = QuestionDatabaseAccess.store(question);
        storeTags();
        storeAnswerUrls();

        if (answer != null && question != null) {
            result = "success";
            UserAction.updateXp(XpCalculator.getAskingXp(), session);
            session.setInfo(StringProvider.getString("askedQuestionSuccess") + XpCalculator.getAskingXp() + " XP!");

            if (askFollowUps.equals("hasFollowUps")) {
                result = askFollowUps;
            }
        }

        return result;
    }

    /**
     * Stores a new follow up question. Forwards according to navigation rules.
     * 
     * @return success, if storing was successful, otherwise fail
     * @throws ApplicationException
     */
    public String storeFollowers(String askFollowUps) throws ApplicationException {
        String result = "fail";

        followUp.setUserId(session.getCurrentUserId());
        followUp.setPrevious_question_id(question.getId());

        if (askFollowUps.equals("hasFollowUps")) {
            followUp.setHas_follow_up_question(true);
        }

        // store Question, Answer and Tags
        question = QuestionDatabaseAccess.store(followUp);
        storeTags();
        storeAnswerUrls();

        if (answer != null && question != null) {
            result = "success";
            session.setInfo(StringProvider.getString("askedQuestionSuccess"));

            if (askFollowUps.equals("hasFollowUps")) {
                result = askFollowUps;
                followUp = question;
            }
        }

        return result;
    }

    /**
     * Stores new tags.
     * 
     * @throws ApplicationException
     */
    private void storeTags() throws ApplicationException {
        QuestionTag singleTag = new QuestionTag();

        singleTag.setQuestionId(question.getId());

        // only store tags if there are any, otherwise enter "misc"
        if (!questionTags.getTag().trim().isEmpty()) {
            String urls[] = splitByNewLine(questionTags.getTag());

            // iterate over tags, store as long as there are any
            for (int i = 0; i < urls.length; i++) {

                // trim and lower case tag to avoid apparent duplicates
                singleTag.setTag(urls[i].trim().toLowerCase());
                TagsDatabaseAccess.store(singleTag);

            }
        } else {
            singleTag.setTag(NO_TAGS_GIVEN);
            TagsDatabaseAccess.store(singleTag);

        }
    }

    /**
     * Stores new answer urls.
     * 
     * @throws ApplicationException
     */
    private void storeAnswerUrls() throws ApplicationException {
        Answer singleUrlAnswer = new Answer();

        singleUrlAnswer.setQuestionId(question.getId());

        String urls[] = splitByNewLine(answer.getAnswerUrl());

        // iterate over urls, store as long as there are any
        for (int i = 0; i < urls.length; i++) {
            singleUrlAnswer.setAnswerUrl(urls[i]);
            AnswersDatabaseAccess.store(singleUrlAnswer);
        }

    }

    // *************************************************
    // HELPER METHODES
    // *************************************************

    /**
     * Method to split urls by new line.
     * 
     * @param urls
     *            the urls to split
     * @return
     */

    public String[] splitByNewLine(String urls) {
        String lines[] = urls.split("[\\r\\n]+");
        return lines;
    }

    public SessionBean getSession() {
        return session;
    }

    public void setSession(SessionBean session) {
        this.session = session;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public QuestionTag getQuestionTags() {
        return questionTags;
    }

    public void setQuestionTags(QuestionTag questionTags) {
        this.questionTags = questionTags;
    }

    public Question getFollowUp() {
        return followUp;
    }

    public void setFollowUp(Question followUp) {
        this.followUp = followUp;
    }

}