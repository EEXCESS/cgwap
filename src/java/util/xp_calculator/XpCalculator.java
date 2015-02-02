package cgwap.util.xp_calculator;

import org.joda.time.DateTime;

import cgwap.data_access.SearchQueryDatabaseAccess;
import cgwap.entities.Question;
import cgwap.entities.Round;
import cgwap.util.exception_handler.ApplicationException;

public class XpCalculator {

    int defaultPassScore = 10;

    double queryScore;
    int numberOfQueriesPerQuestionDifficulty;
    int numberNeeded;

    double timePerQuestionDifficulty;
    double timeNeeded;
    double timeScore;

    int lifeScore;
    int livesLeft;

    int filterScore;
    int filterUsed;

    protected static int askingXp = 14;

    public XpCalculator(Question question, Round round) throws ApplicationException {

        timeNeeded = round.getDuration();
        numberNeeded = SearchQueryDatabaseAccess.getNumberOfQueries(round);

        timePerQuestionDifficulty = getDifficultyTime(question.getDifficultyRating());
        numberOfQueriesPerQuestionDifficulty = getDifficultyQueries
                (question.getDifficultyRating());

        livesLeft = round.getLivesLeft();

        filterUsed = SearchQueryDatabaseAccess.getNumberOfFilters(round);
    }

    public int calculateXp() {

        chooseLifeScore();
        chooseTimeScore();
        chooseQueryScore();
        chooseFilterScore();

        return (int) ((lifeScore + queryScore + timeScore + defaultPassScore) / 1.3);
    }

    private void chooseFilterScore() {
        filterScore = filterUsed * 3;
    }

    private void chooseQueryScore() {
        if (numberOfQueriesPerQuestionDifficulty >= numberNeeded) {
            queryScore = numberOfQueriesPerQuestionDifficulty * 2.5;
        }
        else {
            queryScore = (numberOfQueriesPerQuestionDifficulty - numberNeeded) * 2.5;
            if (queryScore < 0) {
                queryScore = 0;
            }
        }

    }

    private void chooseTimeScore() {
        if (timePerQuestionDifficulty >= timeNeeded) {
            timeScore = timePerQuestionDifficulty;
        }
        else {
            timeScore = timePerQuestionDifficulty - timeNeeded;
            if (timeScore < 0) {
                timeScore = 0;
            }
        }

    }

    private void chooseLifeScore() {

        if (livesLeft == 0) {
            lifeScore = 0;
        }

        else if (livesLeft == 1) {
            lifeScore = 5;
        }
        else if (livesLeft == 2) {
            lifeScore = 10;
        }
        else if (livesLeft == 3) {
            lifeScore = 15;
        }
    }

    private int getDifficultyQueries(float difficultyRating) {
        int difQueries = 0;
        switch ((int) difficultyRating) {
        case 1:
            difQueries = 1;
            break;
        case 2:
            difQueries = 2;
            break;
        case 3:
            difQueries = 4;
            break;
        case 4:
            difQueries = 5;
            break;
        case 5:
            difQueries = 8;
            break;
        }
        return difQueries;
    }

    /**
     * Matches the questionFifficultyRating to it's according number of seconds
     * optimally needed.
     * 
     * @param difficultyRating
     * @return
     */
    private int getDifficultyTime(float difficultyRating) {
        int difTime = 0;
        switch ((int) difficultyRating) {
        case 1:
            difTime = 10;
            break;
        case 2:
            difTime = 20;
            break;
        case 3:
            difTime = 30;
            break;
        case 4:
            difTime = 45;
            break;
        case 5:
            difTime = 55;
            break;
        }
        return difTime;

    }



    public int getDefaultPassScore() {
        return defaultPassScore;
    }

    public void setDefaultPassScore(int defaultPassScore) {
        this.defaultPassScore = defaultPassScore;
    }

    public double getQueryScore() {
        return queryScore;
    }

    public void setQueryScore(int queryScore) {
        this.queryScore = queryScore;
    }

    public int getNumberOfQueriesPerQuestionDifficulty() {
        return numberOfQueriesPerQuestionDifficulty;
    }

    public void setNumberOfQueriesPerQuestionDifficulty(int numberOfQueriesPerQuestionDifficulty) {
        this.numberOfQueriesPerQuestionDifficulty = numberOfQueriesPerQuestionDifficulty;
    }

    public int getNumberNeeded() {
        return numberNeeded;
    }

    public void setNumberNeeded(int numberNeeded) {
        this.numberNeeded = numberNeeded;
    }

    public double getTimePerQuestionDifficulty() {
        return timePerQuestionDifficulty;
    }

    public void setTimePerQuestionDifficulty(int timePerQuestionDifficulty) {
        this.timePerQuestionDifficulty = timePerQuestionDifficulty;
    }

    public double getTimeNeeded() {
        return timeNeeded;
    }

    public void setTimeNeeded(double timeNeeded) {
        this.timeNeeded = timeNeeded;
    }

    public double getTimeScore() {
        return timeScore;
    }

    public void setTimeScore(int timeScore) {
        this.timeScore = timeScore;
    }

    public int getLifeScore() {
        return lifeScore;
    }

    public void setLifeScore(int lifeScore) {
        this.lifeScore = lifeScore;
    }

    public int getLivesLeft() {
        return livesLeft;
    }

    public void setLivesLeft(int livesLeft) {
        this.livesLeft = livesLeft;
    }

    public static int getAskingXp() {
        return askingXp;
    }

    public static void setAskingXp(int askingXp) {
        XpCalculator.askingXp = askingXp;
    }

    public void setQueryScore(double queryScore) {
        this.queryScore = queryScore;
    }

    public void setTimePerQuestionDifficulty(double timePerQuestionDifficulty) {
        this.timePerQuestionDifficulty = timePerQuestionDifficulty;
    }

    public void setTimeScore(double timeScore) {
        this.timeScore = timeScore;
    }

    public int getFilterScore() {
        return filterScore;
    }

    public void setFilterScore(int filterScore) {
        this.filterScore = filterScore;
    }

    public int getFilterUsed() {
        return filterUsed;
    }

    public void setFilterUsed(int filterUsed) {
        this.filterUsed = filterUsed;
    }

}
