package cgwap.enums;

/**
 * All possible levels.
 * 
 */
public enum UserLevel {

    /**
     * beginner
     */
    Questling(0),

    /**
     * amateur
     */
    Questionnaire(100),

    /**
     * gifted amateur
     */
    QuestionMark(300),

    /**
     * pro
     */
    BachelorOfQuestions(550),

    /**
     * veteran
     */
    MasterOfQuestions(800);
    private int xp;

    private UserLevel(int xp) {
        this.setXp(xp);
    }

    private UserLevel() {
    }

    @Override
    public String toString() {
        String str = "";
        switch (this) {
        case Questling:
            str = "Questling";
            break;
        case Questionnaire:
            str = "Questionnaire";
            break;
        case QuestionMark:
            str = "Question Mark";
            break;
        case BachelorOfQuestions:
            str = "Bachelor of Questions";
        case MasterOfQuestions:
            str = "Master of Questions";
        }
        return str;
    }

    /**
     * 
     * @param strValue
     *            the String to get the appropriate TicketState to
     * @return the appropriate TicketState or null if nothing found
     * @see TicketState#valueOf(String)
     */
    public static UserLevel valueOfString(String strValue) {
        if (strValue.equals(Questling.toString())) {
            return Questling;
        } else if (strValue.equals(Questionnaire.toString())) {
            return Questionnaire;
        } else if (strValue.equals(QuestionMark.toString())) {
            return QuestionMark;
        } else if (strValue.equals(BachelorOfQuestions.toString())) {
            return BachelorOfQuestions;
        } else if (strValue.equals(MasterOfQuestions.toString())) {
            return MasterOfQuestions;
        } else {
            return null;
        }

    }

    /**
     * returns the next in line
     * 
     * @param current
     * @return
     */
    public static UserLevel getNext(UserLevel current) {
        return UserLevel.values()[current.ordinal() + 1];

    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
}
