package cgwap.backing_beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import cgwap.data_access.QuestionDatabaseAccess;
import cgwap.data_access.RoundsDatabaseAccess;
import cgwap.data_access.SearchQueryDatabaseAccess;
import cgwap.data_access.UserDatabaseAccess;
import cgwap.entities.Highscore;
import cgwap.entities.Question;
import cgwap.entities.Round;
import cgwap.entities.SearchQuery;
import cgwap.entities.User;
import cgwap.util.exception_handler.ApplicationException;
import cgwap.util.session.SessionBean;

@ManagedBean
@ViewScoped
public class HighscoreAction implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5550679597412636805L;

    // last query important?
    protected Highscore highscore = new Highscore();

    @ManagedProperty(value = "#{sessionBean}")
    protected SessionBean session;

    protected boolean lookAtQueries = false;

    protected List<SearchQuery> queries;

    protected Question currentQuestion;

    /**
     * Retrieves Highscore based on fastest rounds.
     * 
     * @return
     *         list of 10 best highscores
     * @throws ApplicationException
     */
    public List<Highscore> getBestRounds() throws ApplicationException {
        List<Highscore> highscores = new LinkedList<Highscore>();

        List<Round> rounds = RoundsDatabaseAccess.getFastestRounds();

        for (Round round : rounds) {
            Question highscoreQuestion = QuestionDatabaseAccess.getQuestionById(new Question(round.getQuestionId()));
            User highscoreUser = UserDatabaseAccess.getById(new User(round.getUserId()));
            Highscore highscoreEntry = new Highscore();
            highscoreEntry.setDuration((int) round.getDuration());
            highscoreEntry.setNickname(highscoreUser.getNickname());
            highscoreEntry.setQuestionId(highscoreQuestion.getId());
            highscoreEntry.setText(highscoreQuestion.getQuestionText());
            highscoreEntry.setRoundId(round.getId());
            highscoreEntry.setXp(highscoreUser.getXp());
            highscoreEntry.setLevel(highscoreUser.getLevel());
            highscoreEntry.setUserId(highscoreUser.getId());

            highscoreEntry.setAllowedToSee(allowedToSee(highscoreEntry));

            highscores.add(highscoreEntry);

        }

        return highscores;

    }

    /**
     * Retrieves Highscore based on best/most experienced players.
     * 
     * @return
     *         list of 10 best players
     * @throws ApplicationException
     */
    public List<Highscore> getBestPlayers() throws ApplicationException {
        List<Highscore> highscores = new LinkedList<Highscore>();

        List<User> users = UserDatabaseAccess.getBestPlayers();

        for (User user : users) {
            User highscoreUser = UserDatabaseAccess.getById(new User(user.getId()));
            Highscore highscoreEntry = new Highscore();
            highscoreEntry.setNickname(highscoreUser.getNickname());
            highscoreEntry.setXp(highscoreUser.getXp());
            highscoreEntry.setLevel(highscoreUser.getLevel());
            highscoreEntry.setUserId(highscoreUser.getId());

            highscores.add(highscoreEntry);

        }

        return highscores;

    }

    /**
     * Checks whether active user is allowed to see the "how" column.
     * 
     * @return
     *         if he/she is allowed
     * @throws ApplicationException
     */
    public boolean allowedToSee(Highscore highscoreEntry) throws ApplicationException {
        boolean allowed = false;

        // if player owns this question
        if (QuestionDatabaseAccess.doesUserOwn(session.getCurrentUserId(), highscoreEntry.getQuestionId())) {
            allowed = true;
        }
        else {

            boolean needToBePass = false;

            List<Integer> playedRoundIds = RoundsDatabaseAccess.getRoundIdsByUserId(session.getCurrentUserId(),
                    needToBePass);
            // if player hasn't answered any questions, not allowed to say any
            // how tos
            if (playedRoundIds.isEmpty()) {
                allowed = false;
            } else {
                List<Round> playedRounds = RoundsDatabaseAccess.getRoundsByUserId(session.getCurrentUserId(), needToBePass);

                for (Round round : playedRounds) {

                    if (round.getQuestionId() == highscoreEntry.getQuestionId()) {
                        allowed = true;
                    }
                }
            }

        }

        return allowed;

    }

    public void getQueries(Highscore highscore) throws ApplicationException {
        lookAtQueries = true;

        currentQuestion = QuestionDatabaseAccess.getQuestionById(new Question(highscore.getQuestionId()));
        queries = SearchQueryDatabaseAccess.fetch(new Round(highscore.getRoundId()));

    }

    public Highscore getHighscore() {
        return highscore;
    }

    public void setHighscore(Highscore highscore) {
        this.highscore = highscore;
    }

    public SessionBean getSession() {
        return session;
    }

    public void setSession(SessionBean session) {
        this.session = session;
    }

    public boolean isLookAtQueries() {
        return lookAtQueries;
    }

    public void setLookAtQueries(boolean lookAtQueries) {
        this.lookAtQueries = lookAtQueries;
    }

    public List<SearchQuery> getQueries() {
        return queries;
    }

    public void setQueries(List<SearchQuery> queries) {
        this.queries = queries;
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }
}
