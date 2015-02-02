package cgwap.entities;

public class Result {

    /**
     * Data Transfer Object to represent a result. It stores basic information
     * on the search result.
     */

    private String title;
    private String preview;
    private String description;
    private boolean isImage;
    private boolean isText;
    private boolean isSound;
    private boolean isVideo;
//    private String link;
    private String id;

    public Result() {
    }

    public Result(String title, String preview) {
        this.title = title;
        this.preview = preview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean isImage) {
        this.isImage = isImage;
    }

    public boolean isText() {
        return isText;
    }

    public void setText(boolean isText) {
        this.isText = isText;
    }

    public boolean isSound() {
        return isSound;
    }

    public void setSound(boolean isSound) {
        this.isSound = isSound;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean isVideo) {
        this.isVideo = isVideo;
    }

//    public String getLink() {
//        return link;
//    }
//
//    public void setLink(String link) {
//        this.link = link;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}