package org.data.process;

import org.apache.commons.lang.StringEscapeUtils;
import org.data.model.Tweet;
import org.data.model.TweetText;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TweetProcessor {

    private final static JSONParser parser = new JSONParser();

    private static TweetText getCleanedTweetText(String inputText) {

        TweetText cleanedTweetText = new TweetText();


        try {
            Pattern pattern = Pattern.compile("\\\\u(\\p{XDigit}{4})", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputText);

            //int count = matcher.groupCount();
            cleanedTweetText.setHasUniCodes(matcher.find());
            cleanedTweetText.setTweetText(StringEscapeUtils.unescapeJava(matcher.replaceAll("")));

        }catch (Exception ex) {
            ex.printStackTrace();
        }

        return cleanedTweetText;
    }

    private static Set<String> getHashTags(String text) {
        Set<String> hashTags = new HashSet<String>();

        if (text != null && text.length() > 0) {
            Matcher matcher = Pattern.compile("#\\w+").matcher(text);
            while (matcher.find()) {
                String token = matcher.group();
                hashTags.add(token.substring(1));
            }

        }
        return hashTags;
    }

    private static  String getTweetComponent(String tweet, String startWith, String endWith) {
        String subString = null;

        if (tweet.contains(startWith) && tweet.contains(endWith)) {

            int beginIndex = tweet.indexOf(startWith)+startWith.length()+1;
            int endIndex = tweet.indexOf(endWith)-2;

            subString = tweet.substring(beginIndex, endIndex);
        }

        return subString;
    }

    public static Tweet parseTweet(String rawTweet) throws Exception {

        Tweet tweet = null;

        if (rawTweet != null &&  rawTweet.length() > 0) {
            tweet = new Tweet();

            JSONObject jsonObject = (JSONObject)parser.parse(rawTweet);

            String rawTweetText  = getTweetComponent(rawTweet,"\"text\":", "\"source\":");
            if(rawTweetText!=null){
                TweetText tweetText  = getCleanedTweetText(rawTweetText);

                tweet.setTweetText(tweetText);
                tweet.setHashTags(getHashTags(tweetText.getTweetText()));
                tweet.setCreatedDate((String)jsonObject.get("created_at"));
                tweet.setTimeInMS(Long.parseLong((String)jsonObject.get("timestamp_ms")));
            }
        }

        return tweet;
    }
}
