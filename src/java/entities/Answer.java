package cgwap.entities;


public class Answer {
    /**
     * Data Transfer Object to represent an answer. It stores basic information
     * on the
     * .
     */

    private int questionId;

    private String answerUrl;

    public Answer() {
    }

    /**
     * Constructor with parameter.
     * 
     * @param id
     *            - the answer's identifier
     */
    public Answer(int id) {
        this.questionId = id;
    }

    /**
     * Constructor with all parameters.
     * 
     */
    public Answer(int id, String url) {
        this.questionId = id;
        this.answerUrl = url;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int question_id) {
        this.questionId = question_id;
    }

    public String getAnswerUrl() {
        return answerUrl;
    }

    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }

    // *************************************************
    // HELPER METHODES
    // *************************************************
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((answerUrl == null) ? 0 : answerUrl.hashCode());
        result = prime * result + questionId;
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
        Answer other = (Answer) obj;
        if (answerUrl == null) {
            if (other.answerUrl != null)
                return false;
        } else if (!answerUrl.equals(other.answerUrl))
            return false;
        if (questionId != other.questionId)
            return false;
        return true;
    }

}
