package cgwap.entities;

import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;

public class Round {

    /**
     * Data Transfer Object to represent a result. It stores basic information
     * on the
     * .
     */

    private int id;
    private int userId;
    private int questionId;

    private Date start;
    private Date end;
    private int score; 
    private String pass;
    private String userComment;
    private int livesLeft;
    
    private long duration;

    public Round() {
    }

    public Round(int id) {
        this.id = id;
    }

    public Round(int userId, int questionId, Date start, Date end, int duration, int score, String pass,
            String userComment) {
        this.userId = userId;
        this.questionId = questionId;
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.score = score;
        this.pass = pass;
        this.userComment = userComment;
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

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public long getDuration() {
        DateTime startTime = new DateTime(getStart());
        DateTime endTime = new DateTime(getEnd());
        duration = (endTime.getMillis() - startTime.getMillis()) / 1000;
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public int getLivesLeft() {
        return livesLeft;
    }

    public void setLivesLeft(int livesLeft) {
        this.livesLeft = livesLeft;
    }

}
