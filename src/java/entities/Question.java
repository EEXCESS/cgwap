package cgwap.entities;

import java.sql.Timestamp;
import java.util.Date;

public class Question {
    /**
     * Data Transfer Object to represent a question. It stores basic information
     * on the
     * .
     */

    private int id;
    private int userId; // ID of person who created this particular question.
    private String questionText;
    private Date timestamp;
    private int skipped;
    private float difficultyRating;
    private int ratingCounter;

    private int reported;
    private boolean has_follow_up_question;
    private int previous_question_id;

    public Question() {
    }

    public Question(int questionId) {
        this.id = questionId;
    }

    public Question(int id, int userId, String questionText, Date timestamp, int skipped, int rating, int reported,
            boolean is_follow_up_question, int previous_question_id) {
        this.id = id;
        this.userId = userId;
        this.questionText = questionText;
        this.timestamp = timestamp;
        this.skipped = skipped;
        this.difficultyRating = rating;
        this.reported = reported;
        this.has_follow_up_question = is_follow_up_question;
        this.previous_question_id = previous_question_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    public float getDifficultyRating() {
        return difficultyRating;
    }

    public void setDifficultyRating(float rating) {
        this.difficultyRating = rating;
    }

    public int getReported() {
        return reported;
    }

    public void setReported(int reported) {
        this.reported = reported;
    }

    public boolean isHas_follow_up_question() {
        return has_follow_up_question;
    }

    public void setHas_follow_up_question(boolean has_follow_up_question) {
        this.has_follow_up_question = has_follow_up_question;
    }

    public int getPrevious_question_id() {
        return previous_question_id;
    }

    public void setPrevious_question_id(int previous_question_id) {
        this.previous_question_id = previous_question_id;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getRatingCounter() {
        return ratingCounter;
    }

    public void setRatingCounter(int ratingCounter) {
        this.ratingCounter = ratingCounter;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Question other = (Question) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
