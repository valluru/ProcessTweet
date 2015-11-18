package org.data.model;


public class TweetText {

    private String tweetText;
    private boolean hasUniCodes;

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public boolean isHasUniCodes() {
        return hasUniCodes;
    }

    public void setHasUniCodes(boolean hasUniCodes) {
        this.hasUniCodes = hasUniCodes;
    }
}
