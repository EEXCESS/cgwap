package cgwap.backing_beans;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import cgwap.data_access.AnswersDatabaseAccess;
import cgwap.data_access.QuestionDatabaseAccess;
import cgwap.data_access.RoundsDatabaseAccess;
import cgwap.data_access.SearchQueryDatabaseAccess;
import cgwap.data_access.TagsDatabaseAccess;
import cgwap.entities.Answer;
import cgwap.entities.Question;
import cgwap.entities.QuestionTag;
import cgwap.entities.Result;
import cgwap.entities.Round;
import cgwap.entities.SearchQuery;
import cgwap.entities.User;
import cgwap.util.Config;
import cgwap.util.exception_handler.ApplicationException;
import cgwap.util.i18n.StringProvider;
import cgwap.util.session.SessionBean;
import cgwap.util.xp_calculator.XpCalculator;

/**
 * This managed bean handles the answer functionality.
 * 
 */
@ManagedBean
@ViewScoped
public class SearchAction implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6320961375202648144L;

    @ManagedProperty(value = "#{sessionBean}")
    protected SessionBean session;

    protected Question question = new Question();
    protected QuestionTag questionTags = new QuestionTag();
    protected String tagToChooseQuestionBy = "";
    protected int currentQuestionId = 0;
    protected boolean questionIsLoaded = false;
    protected boolean isQuestionHasFollowUpQuestion = false;
    protected List<Result> matches = new LinkedList<Result>();
    protected List<Answer> answers = new LinkedList<Answer>();

    // For playing.
    protected SearchQuery searchQuery = new SearchQuery();
    protected Round currentRound = new Round();
    protected User currentUser = new User();
    protected static final int NUMBER_OF_RESULTS = Config.NUMBER_OF_RESULTS;
    protected Result[] results = new Result[NUMBER_OF_RESULTS];
    protected boolean useFilter = false;
    protected boolean hasResults = false;
    protected boolean isMatch = false;

    // let's users only rate once
    protected boolean rated = false;

    protected long timer = 0;
    protected static final int NUMBER_OF_GUESSES = Config.NUMBER_OF_GUESSES;
    protected int livesLeft = NUMBER_OF_GUESSES;
    // statuses on which a round can be ended. Passed, Failed, Skipped or
    // Reported.
    protected String pass = "pass";
    protected String fail = "fail";
    protected String skipp = "skipp";
    protected String repo = "repo";

    // Displaying a match.
    protected Result matchingResult = new Result();

    // Request Parameters
    protected static final String URL = Config.REQUEST_URL;
    protected static final String KEY = Config.REQUEST_KEY;
    protected static final String PROFILE = Config.REQUEST_PROFILE;
    protected static final String FILTER = Config.REQUEST_FILTER;
    protected static final String ROWS = Config.REQUEST_ROWS;
    protected static final String QUERY = Config.REQUEST_QUERY;

    // Response Parameters
    protected static final String ITEMS = Config.RESPONSE_ITEMS;
    protected static final String PREVIEW = Config.ITEM_PREVIEW;
    protected static final String TITLE = Config.ITEM_TITLE;
    protected static final String TYPE = Config.ITEM_TYPE;
    protected static final String LINK = Config.ITEM_LINK;
    protected static final String ID = Config.ITEM_ID;
    protected static final String DESCRIPTION = Config.ITEM_DESCRIPTION;
    protected static final String TEXT = Config.TYPE_TEXT;
    protected static final String IMAGE = Config.TYPE_IMAGE;
    protected static final String VIDEO = Config.TYPE_VIDEO;
    protected static final String SOUND = Config.TYPE_SOUND;

    protected String countryFilter = "";
    protected String typeFilter = "";
    protected String languageFilter = "";

    protected final String[] types = { "Text", "Image", "Video", "Sound", "3D Image" };
    protected final String[] countries = { "France",
            "Germany",
            "Netherlands",
            "Spain",
            "Sweden",
            "Italy",
            "United Kingdom",
            "Norway",
            "Poland",
            "Ireland",
            "Austria",
            "Europe",
            "Denmark",
            "Belgium",
            "Hungary",
            "Czech Republic",
            "Greece",
            "Estonia",
            "Portugal",
            "Slovenia",
            "Switzerland",
            "Finland",
            "Latvia",
            "Lithuania",
            "Slovakia",
            "Luxembourg",
            "Bulgaria",
            "Malta",
            "Romania",
            "Russia",
            "Serbia",
            "Turkey",
            "Croatia",
            "Cyprus",
            "Iceland",
            "Ukraine" };

    protected final String[] languages = { "Deutsch",
            "Français",
            "Nederlands",
            "Multilingue",
            "Español",
            "English",
            "Italiano",
            "Svenska",
            "Norsk",
            "Polski",
            "Dansk",
            "Magyar",
            "Čeština",
            "Ελληνικά",
            "Eesti",
            "Português",
            "Slovenščina",
            "Suomi",
            "Latviešu",
            "Català",
            "Lietuvių",
            "Slovenský",
            "Български",
            "Swedish",
            "Русский",
            "Română",
            "Serbian",
            "Turkish",
            "Hrvatski",
            "Malti",
            "Yiddish",
            "Íslenska",
            "Gaeilge",
            "Українська" };

    /**
     * loads a list all of tags available in system
     * 
     * @return the list of tags
     * @throws ApplicationException
     */
    public List<QuestionTag> loadTags() throws ApplicationException {

       
        List<QuestionTag> result = TagsDatabaseAccess.fetch(true);
        java.util.Collections.sort(result);
        result.add(0, new QuestionTag(Config.DEFAULT_TAG));
        return result;
    }

    /**
     * loads random question, checks if user wants to get question per tag.
     * 
     * @throws ApplicationException
     */
    public void loadRandomQuestion() throws ApplicationException {

        rated = false;
        timer = 0;
        matchingResult = new Result();
        livesLeft = NUMBER_OF_GUESSES;
        currentRound = new Round();
        isMatch = false;
        results = new Result[NUMBER_OF_RESULTS];
        hasResults = false;
        searchQuery = new SearchQuery();

        Random randomizer = new Random();
        Question randomQuestion = new Question();

        // if tag is selected, select question accordingly (disregarding the
        // ones entered or already answered by the current player, or the ones
        // reported more than 3 times, deleted or followUps)
        
        
        if (!(tagToChooseQuestionBy.equals(Config.DEFAULT_TAG) || tagToChooseQuestionBy.equals(""))) {

            // get ids for tag
            List<QuestionTag> idsToChooseFrom = TagsDatabaseAccess.getQuestionIdsByTag(tagToChooseQuestionBy,
                    session.getCurrentUserId());
            idsToChooseFrom = filterUserQuestions(idsToChooseFrom);
            if (!idsToChooseFrom.isEmpty()) {
                QuestionTag random = idsToChooseFrom.get(randomizer.nextInt(idsToChooseFrom.size()));
                randomQuestion = new Question(random.getQuestionId());
                randomQuestion = QuestionDatabaseAccess.getQuestionById(randomQuestion);
            }
            // no tag, select random question (disregarding the ones entered or
            // already answered by
            // the current player, or the ones reported more than 3 times,
            // deleted or followUps)
        } else {
            List<Question> questionsToChooseFrom = QuestionDatabaseAccess.fetch(session.getCurrentUserId());
            if (!questionsToChooseFrom.isEmpty()) {
                randomQuestion = questionsToChooseFrom.get(randomizer.nextInt(questionsToChooseFrom.size()));
            }
        }

        if (randomQuestion.getId() != 0) {

            setQuestion(randomQuestion);
            setCurrentQuestionId(randomQuestion.getId());

            // in case of anonymous player
            // if (session.getCurrentUserId() == 0) {
            // currentRound = storeAnonymousPlayer(currentRound);
            // } else {

            currentRound.setUserId(session.getCurrentUserId());
            // }

            // store new Round in Database
            currentRound.setQuestionId(randomQuestion.getId());
            currentRound = RoundsDatabaseAccess.store(currentRound);

            questionIsLoaded = true;

            if (randomQuestion.isHas_follow_up_question()) {
                isQuestionHasFollowUpQuestion = true;
            }

            // preload answers for later use
            answers = AnswersDatabaseAccess.getAnswerUrlByQuestionId(randomQuestion.getId());
        }
        else {
            questionIsLoaded = false;
            session.setError(StringProvider.getString("noQuestionsAvailable"));
        }
    }

    /**
     * 
     * loads followUp question.
     * 
     * @throws ApplicationException
     */
    public void loadFollowUpQuestion() throws ApplicationException {

        timer = 0;
        matchingResult = new Result();
        isMatch = false;
        results = new Result[NUMBER_OF_RESULTS];
        hasResults = false;
        searchQuery = new SearchQuery();

        question = QuestionDatabaseAccess.getFollowUpQuestion(question);

        if (question != null) {
            setCurrentQuestionId(question.getId());

            // in case of anonymous player
            // if (session.getCurrentUserId() == 0) {
            // currentRound = storeAnonymousPlayer(currentRound);
            // } else {

            currentRound.setUserId(session.getCurrentUserId());
            // }

            // store new Round in Database
            currentRound.setQuestionId(currentQuestionId);
            currentRound = RoundsDatabaseAccess.store(currentRound);

            questionIsLoaded = true;

            if (question.isHas_follow_up_question()) {
                isQuestionHasFollowUpQuestion = true;
            } else {
                isQuestionHasFollowUpQuestion = false;
            }

            // preload answers for later use
            answers = AnswersDatabaseAccess.getAnswerUrlByQuestionId(currentQuestionId);
        }
        else {
            questionIsLoaded = false;
            session.setError(StringProvider.getString("noQuestionsAvailable"));
        }
    }

    /**
     * Method to filter questions which were entered or answered by the player.
     * 
     * @param idsToChooseFrom
     *            the possible questions containing a certain tag
     * 
     * @return
     *         the possible questions save the ones entered or answered by the
     *         current player
     * @throws ApplicationException
     */
    private List<QuestionTag> filterUserQuestions(List<QuestionTag> idsToChooseFrom) throws ApplicationException {
        for (QuestionTag questionTag : idsToChooseFrom) {

            if (QuestionDatabaseAccess.doesUserOwn(session.getCurrentUserId(), questionTag.getQuestionId())) {
                idsToChooseFrom.remove(questionTag);
            }

        }

        return idsToChooseFrom;
    }

    // /**
    // * Stores an anonymous player in session and databases.
    // *
    // * @param currentRound
    // * @return
    // * @throws ApplicationException
    // */
    // private Round storeAnonymousPlayer(Round currentRound) throws
    // ApplicationException {
    //
    // User anon = new User();
    // anon.setLevel(UserLevel.Questling);
    // anon = UserDatabaseAccess.store(anon);
    // anon.setNickname("Anonymous" + anon.getId());
    // UserDatabaseAccess.update(anon);
    // currentRound.setUserId(anon.getId());
    // return currentRound;
    // }

    // /**
    // * loads random question specified by tag
    // *
    // * @return true, if question could be loaded
    // * @throws ApplicationException
    // */
    // public void loadRandomQuestionByTag() throws ApplicationException {
    // boolean loaded = false;
    //
    // if (tagToChooseQuestionBy != "") {
    // // get ids for tag
    // List<QuestionTag> idsToChooseFrom =
    // TagsDatabaseAccess.getQuestionIdsByTag(tagToChooseQuestionBy);
    // // choose random question
    // Random randomizer = new Random();
    // QuestionTag random =
    // idsToChooseFrom.get(randomizer.nextInt(idsToChooseFrom.size()));
    //
    // Question randomQuestion = new Question(random.getQuestionId());
    // randomQuestion = QuestionDatabaseAccess.getQuestionById(randomQuestion);
    //
    // if (randomQuestion != null) {
    // setQuestion(randomQuestion);
    // loaded = true;
    // setQuestionIsLoaded(loaded);
    // }
    // } else {
    // session.setError(StringProvider.getString("chooseTag"));
    //
    // }
    //
    // }

    /**
     * Sends a HTTP Request to Europeana according to search query entered by
     * the user.
     * 
     * @throws ApplicationException
     */

    public void search() throws ApplicationException {

        // making sure no old results will be represented
        results = new Result[NUMBER_OF_RESULTS];

        // set timer according to start time of round
        DateTime startTime = new DateTime(currentRound.getStart());
        timer = (new DateTime().getMillis() - startTime.getMillis()) / 100;

        URL url;
        String urlParameters = buildUrlParameters();

        // store Query
        searchQuery.setRoundId(currentRound.getId());
        searchQuery = SearchQueryDatabaseAccess.store(searchQuery);

        HttpURLConnection connection = null;
        try {
            // Create connection
            url = new URL(URL + KEY + PROFILE + urlParameters);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            // transform to JSON
            JSONObject json = new JSONObject(response.toString());

            if (json.has(ITEMS)) {
                JSONArray items = json.getJSONArray(ITEMS);
                initResults(items);

                hasResults = true;

            } else {
                hasResults = false;
                session.setError(StringProvider.getString("tryAgain"));
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
        clearParameter();
    }

    /**
     * Checks whether selected one of users results is the correct one.
     * 
     * @return whether it was the correct link
     * @throws ApplicationException
     */

    public void check(Result result) throws ApplicationException {

        String idToCheck = result.getId();
        for (Answer answer : answers) {

            String answerString = answer.getAnswerUrl();
            if (answerString.contains(idToCheck)) {
                isMatch = true;

                // set new round parameters
                currentRound.setPass(pass);
                currentRound.setLivesLeft(livesLeft);
                currentRound.setEnd(new Timestamp(new Date().getTime()));
                Seconds seconds = Seconds.secondsBetween(new DateTime(currentRound.getStart()), new DateTime(
                        currentRound.getEnd()));

                // could use some time conversion stuff to minutes and hours
                currentRound.setDuration(seconds.getSeconds());

                // calculate and set score
                XpCalculator calculator = new XpCalculator(question, currentRound);
                int score = calculator.calculateXp();
                currentRound.setScore(score);
                // update round & user
                UserAction.updateXp(score, session);
                RoundsDatabaseAccess.update(currentRound);
                matchingResult = result;
            }
        }

        if (question.isHas_follow_up_question()) {
            matches.add(matchingResult);
        }
        // no matches -> decreasing livesLeft
        if (!isMatch) {
            livesLeft -= 1;
            currentRound.setLivesLeft(livesLeft);
        }

        // set timer according to start time of round
        DateTime startTime = new DateTime(currentRound.getStart());
        timer = (new DateTime().getMillis() - startTime.getMillis()) / 100;

    }

    /**
     * Helper method to clear information which is no longer needed or wanted in
     * the next search.
     */
    private void clearParameter() {

        countryFilter = "";
        typeFilter = "";
        languageFilter = "";

    }

    private void initResults(JSONArray items) throws JSONException {

        int timesToIterate = NUMBER_OF_RESULTS;

        // check if items is long enough to contain the default number of
        // results
        if (items.length() <= NUMBER_OF_RESULTS) {
            timesToIterate = items.length();
            // reinitialize array
            results = new Result[items.length()];
        }
        for (int i = 0; i < timesToIterate; i++) {
            results[i] = new Result();
            String toTrim = items.getJSONObject(i).get(TITLE).toString();
            // trim brackets
            results[i].setTitle(toTrim.substring(2, toTrim.length() - 2));

            if (items.getJSONObject(i).has(PREVIEW)) {
                toTrim = items.getJSONObject(i).get(PREVIEW).toString();
                results[i].setPreview(toTrim.substring(2, toTrim.length() - 2));
            } else {
                results[i].setPreview("");
            }

            if (items.getJSONObject(i).has(ID)) {
                toTrim = items.getJSONObject(i).get(ID).toString();
                results[i].setId(toTrim);
            } else {
                results[i].setId("no id available");
            }


            if (items.getJSONObject(i).has(TYPE)) {
                toTrim = items.getJSONObject(i).get(TYPE).toString();

                if (toTrim.equals(IMAGE)) {
                    results[i].setImage(true);
                }
                else if (toTrim.equals(TEXT)) {
                    results[i].setText(true);
                }
                else if (toTrim.equals(VIDEO)) {
                    results[i].setVideo(true);
                }
                else if (toTrim.equals(SOUND)) {
                    results[i].setSound(true);
                }
            }

        }
    }

    /**
     * Helper method to build url parameters and specify SearchQuery DTO.
     * 
     * @return the url parameters
     */
    private String buildUrlParameters() {

        // build parameters
        String urlParameters = QUERY + searchQuery.getQuery();

        if (useFilter) {
            searchQuery.setFilterUsed(useFilter);

            if (countryFilter != "") {
                urlParameters = urlParameters + FILTER + countryFilter;
                searchQuery.setFilterProvider(countryFilter);
            }
            if (languageFilter != "") {
                urlParameters = urlParameters + FILTER + languageFilter;
                searchQuery.setFilterLanguage(languageFilter);
            }
            if (typeFilter != "") {
                urlParameters = urlParameters + FILTER + typeFilter.toUpperCase();
                searchQuery.setFilterType(typeFilter.toUpperCase());
            }
        }
        return urlParameters;
    }

    /**
     * Increases a questions reported counter and 'deletes' question if neccessary.
     * 
     * @return the page to redirect to
     * @throws ApplicationException
     */
    public String report() throws ApplicationException {
        question.setReported(question.getReported() + 1);
        if(question.getReported() >= 3){
            QuestionDatabaseAccess.delete(question);
        }
        question = QuestionDatabaseAccess.update(question);
        currentRound.setPass(repo);
        currentRound = RoundsDatabaseAccess.update(currentRound);
        session.setInfo(StringProvider.getString("thanks"));
        return "answering";
    }

    /**
     * Increases a questions skipped counter.
     * 
     * @return
     * @throws ApplicationException
     */
    public String cancel() throws ApplicationException {
        question.setSkipped(question.getSkipped() + 1);
        question = QuestionDatabaseAccess.update(question);
        currentRound.setPass(skipp);
        currentRound = RoundsDatabaseAccess.update(currentRound);
        return "answering";
    }

    /**
     * Changes a questions difficulty rating. (simple method, could be improved
     * by storing the number for every rating value in the db)
     * 
     * @throws ApplicationException
     */
    public void changeRating() throws ApplicationException {
        QuestionAction.changeRating(question);

        rated = true;

        // Users should not get XP for rating their own question
        if (!QuestionDatabaseAccess.doesUserOwn(session.getCurrentUserId(), question.getId())) {
            UserAction.updateXp(2, session);
            session.setInfo(StringProvider.getString("thanksForRating"));
        }
    }

    public SessionBean getSession() {
        return session;
    }

    public void setSession(SessionBean session) {
        this.session = session;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public int getCurrentQuestionId() {
        return currentQuestionId;
    }

    public void setCurrentQuestionId(int currentQuestionId) {
        this.currentQuestionId = currentQuestionId;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public QuestionTag getQuestionTags() {
        return questionTags;
    }

    public void setQuestionTags(QuestionTag questionTags) {
        this.questionTags = questionTags;
    }

    public String getTagToChooseQuestionBy() {
        return tagToChooseQuestionBy;
    }

    public void setTagToChooseQuestionBy(String tagToChooseQuestionBy) {
        this.tagToChooseQuestionBy = tagToChooseQuestionBy;
    }

    public boolean isQuestionIsLoaded() {
        return questionIsLoaded;
    }

    public void setQuestionIsLoaded(boolean questionIsLoaded) {
        this.questionIsLoaded = questionIsLoaded;
    }

    public String[] getTypes() {
        return types;
    }

    public String[] getCountries() {
        return countries;
    }

    public String[] getLanguages() {
        return languages;
    }

    public boolean isUseFilter() {
        return useFilter;
    }

    public void setUseFilter(boolean useFilters) {
        this.useFilter = useFilters;
    }

    public String getCountryFilter() {
        return countryFilter;
    }

    public void setCountryFilter(String countryFilter) {
        this.countryFilter = countryFilter;
    }

    public String getTypeFilter() {
        return typeFilter;
    }

    public void setTypeFilter(String typeFilter) {
        this.typeFilter = typeFilter;
    }

    public String getLanguageFilter() {
        return languageFilter;
    }

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean isRated) {
        this.rated = isRated;
    }

    public void setLanguageFilter(String languageFilter) {
        this.languageFilter = languageFilter;
    }

    public SearchQuery getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(SearchQuery searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Round currentRound) {
        this.currentRound = currentRound;
    }

    public Result[] getResults() {
        return results;
    }

    public void setResults(Result[] results) {
        this.results = results;
    }

    public boolean isHasResults() {
        return hasResults;
    }

    public void setHasResults(boolean hasResults) {
        this.hasResults = hasResults;
    }

    public boolean isMatch() {
        return isMatch;
    }

    public boolean isQuestionHasFollowUpQuestion() {
        return isQuestionHasFollowUpQuestion;
    }

    public void setQuestionHasFollowUpQuestion(boolean questionHasFollowUpQuestion) {
        this.isQuestionHasFollowUpQuestion = questionHasFollowUpQuestion;
    }

    public void setMatch(boolean isMatch) {
        this.isMatch = isMatch;
    }

    public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }

    public int getLivesLeft() {
        return livesLeft;
    }

    public void setLivesLeft(int livesLeft) {
        this.livesLeft = livesLeft;
    }

    public Result getMatchingResult() {
        return matchingResult;
    }

    public void setMatchingResult(Result matchingResult) {
        this.matchingResult = matchingResult;
    }

}
