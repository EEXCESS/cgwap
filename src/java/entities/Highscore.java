package cgwap.entities;

import java.io.Serializable;

/**
 * 
 * Data Transfer Object to represent a Highscore, which is used in representing
 * the fastest rounds or best players in CGWAP. (Possible to add a "registered_since" in users.)
 * 
 */
public class Highscore {

    /**
     * 
     */
    private static final long serialVersionUID = -6412480951694012206L;
    
    
    private int userId;
    private String nickname;
    private int roundId;
    private int duration;
    private int questionId;
    private String text;
    private String level;
    private int xp;

    /**
     * Boolean indicating whether active user is allowed to see the how-to of a
     * certain highscore entry.
     */
    private boolean allowedToSee;

    public Highscore() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoundId() {
        return roundId;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isAllowedToSee() {
        return allowedToSee;
    }

    public void setAllowedToSee(boolean allowed) {
        this.allowedToSee = allowed;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }


}
