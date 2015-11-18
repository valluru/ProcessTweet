package org.data.model;

import java.util.Set;


public class Tweet {

    private TweetText tweetText;
    private Set<String> hashTags;
    private String createdDate;
    private Long   timeInMS;

    public TweetText getTweetText() {
        return tweetText;
    }

    public void setTweetText(TweetText tweetText) {
        this.tweetText = tweetText;
    }

    public Set<String> getHashTags() {
        return hashTags;
    }

    public void setHashTags(Set<String> hashTags) {
        this.hashTags = hashTags;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Long getTimeInMS() {
        return timeInMS;
    }

    public void setTimeInMS(Long timeInMS) {
        this.timeInMS = timeInMS;
    }
}
