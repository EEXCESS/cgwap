package cgwap.entities;


public class QuestionTag implements Comparable<QuestionTag> {

    private int questionId;

    private String tag;

    public QuestionTag() {
    }
    
    public QuestionTag(int questionId, String tag) {
        this.questionId = questionId;
        this.tag = tag;
    }
    
    public QuestionTag(String tag) {
        this.tag = tag;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int question_id) {
        this.questionId = question_id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tags) {
        this.tag = tags;
    }

  


    // *************************************************
    // HELPER METHODES
    // *************************************************

    

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QuestionTag other = (QuestionTag) obj;
        if (questionId != other.questionId)
            return false;
        if (tag == null) {
            if (other.tag != null)
                return false;
        } else if (!tag.equals(other.tag))
            return false;
        return true;
    }
    
//    @Override
//    public boolean equals(Object other) {
//
//        if (getTag() == other) {
//            return true;
//        }
//        
//        if (this == other) {
//            return true;
//        }
//
//        if (other == null) {
//            return false;
//        }
//
//        if (getClass() != other.getClass()) {
//            return false;
//        }
//
//        return true;
//    }
//
    public int compareTo(QuestionTag other) {
        int result;
        if (this.equals(other)) {
            result = 0;
        }
        else {
            result = this.tag.toLowerCase().compareTo(other.tag.toLowerCase());
        }
        return result;

        // @Override
        // public String toString() {
        // StringBuilder builder = new StringBuilder();
        // builder.append("USERID: ");
        // builder.append(getId());
        // builder.append("; Email: ");
        // builder.append(getEmail());
        // return builder.toString();
    }

}
