package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * Testing strategy:
     * - writtenBy:
     *      - single tweet matches
     *      - multiple tweets, multiple matches
     *      - username case-insensitive
     *      - no matches
     * - inTimespan:
     *      - tweets inside, outside, at boundaries
     *      - empty list
     * - containing:
     *      - single word, multiple words
     *      - duplicate words in tweet
     *      - case-insensitive match
     *      - empty words list
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "Alyssa", "Talk talk talk", d3);

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // -------------------- writtenBy tests --------------------
    @Test
    public void testWrittenBySingleMatch() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        assertEquals(1, writtenBy.size());
        assertTrue(writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenByCaseInsensitive() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "alyssa");
        assertEquals(2, writtenBy.size());
        assertTrue(writtenBy.containsAll(Arrays.asList(tweet1, tweet3)));
    }

    @Test
    public void testWrittenByNoMatch() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "nonexistent");
        assertTrue(writtenBy.isEmpty());
    }

    // -------------------- inTimespan tests --------------------
    @Test
    public void testInTimespanAllInside() {
        Timespan ts = new Timespan(d1, d3);
        List<Tweet> inSpan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), ts);
        assertEquals(3, inSpan.size());
        assertTrue(inSpan.containsAll(Arrays.asList(tweet1, tweet2, tweet3)));
    }

    @Test
    public void testInTimespanSomeOutside() {
        Timespan ts = new Timespan(d1, d2);
        List<Tweet> inSpan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), ts);
        assertEquals(2, inSpan.size());
        assertTrue(inSpan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertFalse(inSpan.contains(tweet3));
    }

    @Test
    public void testInTimespanAtBoundaries() {
        Timespan ts = new Timespan(d2, d3);
        List<Tweet> inSpan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), ts);
        assertEquals(2, inSpan.size());
        assertTrue(inSpan.containsAll(Arrays.asList(tweet2, tweet3)));
    }

    @Test
    public void testInTimespanEmptyList() {
        Timespan ts = new Timespan(d1, d2);
        List<Tweet> inSpan = Filter.inTimespan(Collections.emptyList(), ts);
        assertTrue(inSpan.isEmpty());
    }

    // -------------------- containing tests --------------------
    @Test
    public void testContainingSingleWord() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3),
                Arrays.asList("rivest"));
        assertEquals(2, containing.size());
        assertTrue(containing.containsAll(Arrays.asList(tweet1, tweet2)));
    }

    @Test
    public void testContainingMultipleWords() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3),
                Arrays.asList("rivest", "hype"));
        assertEquals(2, containing.size());
        assertTrue(containing.containsAll(Arrays.asList(tweet1, tweet2)));
    }

    @Test
    public void testContainingCaseInsensitive() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3),
                Arrays.asList("TALK"));
        assertEquals(3, containing.size());
        assertTrue(containing.containsAll(Arrays.asList(tweet1, tweet2, tweet3)));
    }

    @Test
    public void testContainingDuplicateWordInTweet() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet3), Arrays.asList("talk"));
        assertEquals(1, containing.size());
        assertTrue(containing.contains(tweet3));
    }

    @Test
    public void testContainingEmptyWords() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3),
                Collections.emptyList());
        assertTrue(containing.isEmpty());
    }

}
