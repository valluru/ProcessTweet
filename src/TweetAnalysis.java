package org.data.process;

import org.data.model.Tweet;

import java.io.*;
import java.util.*;

/**
 *
 */
public class TweetAnalysis {

    /**
     * This method performed followint tasks
     *  1. Extract tweet text from tweetline and created_at time
     *  2. Clean the tweet text by removing unicodes and replacing non-ascii characters
     *  3. Persist cleaned tweet text and time into file
     *
     * @param cleanTweetFileWriter
     * @param tweet
     * @throws Exception
     */
    private void persistCleantedTweet(BufferedWriter cleanTweetFileWriter, Tweet tweet) throws Exception {

        if (cleanTweetFileWriter != null && tweet != null) {

            // Format the tweet text and created date
            String formattedTweetText = tweet.getTweetText().getTweetText() + " ("+tweet.getCreatedDate()+")";

            // Persist formatted text into file and append new line.
            cleanTweetFileWriter.write(formattedTweetText);
            cleanTweetFileWriter.newLine();
        }
    }

    /**
     * This method iterates through tweets and removes it from the list if its created timestamp is older
     * than 60seconds from latest tweet
     *
     * @param tweetList
     * @param latestTweet
     */
    private List<Tweet> getLast60SecondsTweets(List<Tweet> tweetList, Tweet latestTweet) {

        List<Tweet> last60SecondsTweetList = new ArrayList<Tweet>(20);

        for (Tweet tweet : tweetList) {

            if ( (latestTweet.getTimeInMS() - tweet.getTimeInMS())/1000 <= 60 ) {
                last60SecondsTweetList.add(tweet);
            }
        }

        last60SecondsTweetList.add(latestTweet);

        return last60SecondsTweetList;
    }

    /**
     * This method construct hashTag graph to calculate the degree
     *
     * @param tweetList
     * @return
     */
    private HashMap<String, Set<String>> getHashTagGraph(List<Tweet> tweetList) {

        HashMap<String, Set<String>> hashTagGraph = new HashMap<String, Set<String>>(20);

        // Iterate through each tweet's hastags to construct the graph
        for (Tweet tweet : tweetList) {

            Set<String> hashTags = tweet.getHashTags();

            if (hashTags.size() >= 2) {
                for (String hashTag : hashTags) {
                    for (String innerHashTag : hashTags) {
                        if (!hashTag.equalsIgnoreCase(innerHashTag)) {
                            Set<String> entries = hashTagGraph.get(hashTag);

                            if (entries == null) {
                                entries = new HashSet<String>();
                            }

                            entries.add(innerHashTag);

                            hashTagGraph.put(hashTag, entries);
                        }
                    }
                }
            }
        }

        return hashTagGraph;
    }

    private double calculateAverageDegree(HashMap<String, Set<String>> hashTagGraph) {

        double averageDegree = 0;
        int    totalNodes    = 0;
        int    degreeTotal   = 0;

        if (hashTagGraph != null) {
            totalNodes = hashTagGraph.size();

            // To get total of each node's degree
            for (String key : hashTagGraph.keySet()) {
                degreeTotal += hashTagGraph.get(key).size();
            }

            if (totalNodes > 0) {
                averageDegree = degreeTotal/totalNodes;
            }
        }


        return averageDegree;
    }

    /**
     * To calcualte average degree for given hashtag graph and persist in file
     *
     * @param hashTagdegreeWriter
     * @param tweetList
     * @param latestTweet
     * @throws Exception
     */
    private void persistHashTagDegree(BufferedWriter hashTagdegreeWriter, List<Tweet> tweetList, Tweet latestTweet)
            throws Exception {

        // To get last60seconds tweet list
        tweetList = getLast60SecondsTweets(tweetList, latestTweet);

        if (tweetList.size() > 0) {
            // To get hashTag graph from last60seconds tweets
            HashMap<String, Set<String>> hashTagGraph = getHashTagGraph(tweetList);

            // To get average degree of hashtag graph
            double averageDegree = calculateAverageDegree(hashTagGraph);

            // To persist average degree into file
            hashTagdegreeWriter.write(String.format("%.2f", averageDegree));

            // To add new line
            hashTagdegreeWriter.newLine();
        }

    }


    public void performAndPersistTweetAnalysis() {

        // Holders for tweet input file and analysis output files
        BufferedReader tweetFeed = null;
        BufferedWriter cleanTweetWriter = null;
        BufferedWriter hashTagDegreeWriter = null;

        // To hold last 60 seconds tweets
        List<Tweet> last60SecondsTweets = new ArrayList<Tweet>(20);

        // To hold the count of tweets which has unicodes
        long tweetCount = 0;

        try {
            // Open input tweet file
            tweetFeed = new BufferedReader(new InputStreamReader(
                    this.getClass().getResourceAsStream("../../../tweet_input/tweets.txt")));

            // Performed substring to remove "file:/" from path
            String cleanTweetFilePath = this.getClass().getResource("../../../tweet_output/ft1.txt").toString().substring(6);
            String degreeCalcFilePath = this.getClass().getResource("../../../tweet_output/ft2.txt").toString().substring(6);

            // Open output files to persist cleaned tweet and hashtag degree.
            cleanTweetWriter    =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(cleanTweetFilePath))));
            hashTagDegreeWriter =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(degreeCalcFilePath))));

            // To hold tweet from input file and model
            String tweetText = null;
            Tweet  tweet     = null;

            // Iterate through the tweets and process it
            while ((tweetText = tweetFeed.readLine()) != null) {
                // To get Tweet model from raw tweet
                tweet = TweetProcessor.parseTweet(tweetText);
                if(tweet.getTweetText()!=null)  {
                    // Persist cleaned tweet text with created date
                    persistCleantedTweet(cleanTweetWriter, tweet);

                    // Persist average degree of hashtag graph from tweet
                    persistHashTagDegree(hashTagDegreeWriter, last60SecondsTweets, tweet);

                    if (tweet.getTweetText() != null && tweet.getTweetText().isHasUniCodes()) {
                        tweetCount++;
                    }
                }
            }

            // To add unicode tweet text statistic to the file
            if (tweetCount > 0) {
                String statText = Long.toString(tweetCount)+" tweets contained unicode.";
                cleanTweetWriter.newLine();
                cleanTweetWriter.write(statText);
            }

        }catch (Exception ex) {

            System.out.println("Exception :"+ex.getMessage());
            ex.printStackTrace();
        }
        finally {

            // To close input/output file connections
            try {
                if (tweetFeed != null) {
                    tweetFeed.close();
                }
                if (cleanTweetWriter != null) {
                    cleanTweetWriter.close();
                }
                if (hashTagDegreeWriter != null) {
                    hashTagDegreeWriter.close();
                }
            }
            catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        }

    }



    public static void main(String args[]) {

        new TweetAnalysis().performAndPersistTweetAnalysis();
        System.out.println("Process Complted, Please check tweet_output directory for output");

    }
}
