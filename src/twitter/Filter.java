package twitter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Filter {

    /**
     * Find tweets written by a particular user.
     */
    public static List<Tweet> writtenBy(List<Tweet> tweets, String username) {
        List<Tweet> result = new ArrayList<>();
        for (Tweet tweet : tweets) {
            if (tweet.getAuthor().equalsIgnoreCase(username)) {
                result.add(tweet);
            }
        }
        return result;
    }

    /**
     * Find tweets that were sent during a particular timespan.
     */
    public static List<Tweet> inTimespan(List<Tweet> tweets, Timespan timespan) {
        List<Tweet> result = new ArrayList<>();
        Instant start = timespan.getStart();
        Instant end = timespan.getEnd();

        for (Tweet tweet : tweets) {
            Instant time = tweet.getTimestamp();
            if ((time.equals(start) || time.isAfter(start)) &&
                    (time.equals(end) || time.isBefore(end))) {
                result.add(tweet);
            }
        }
        return result;
    }

    /**
     * Find tweets that contain certain words.
     */
    public static List<Tweet> containing(List<Tweet> tweets, List<String> words) {
        List<Tweet> result = new ArrayList<>();
        if (words.isEmpty()) return result;

        // Normalize search words to lowercase
        List<String> lowerWords = new ArrayList<>();
        for (String w : words) {
            lowerWords.add(w.toLowerCase());
        }

        for (Tweet tweet : tweets) {
            String[] tokens = tweet.getText().split("\\s+");
            boolean added = false;
            for (String token : tokens) {
                String lowerToken = token.toLowerCase();
                for (String word : lowerWords) {
                    if (lowerToken.equals(word)) {
                        if (!added) {
                            result.add(tweet);
                            added = true;  // ensure tweet added only once
                        }
                        break;  // break inner word loop
                    }
                }
                if (added) break;  // break outer token loop
            }
        }
        return result;
    }
}
