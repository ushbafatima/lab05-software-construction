/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * Testing strategy
     *
     * getTimespan(tweets):
     *  - tweets size: 0 (invalid input, should throw), 1, >1
     *  - order: tweets in chronological order, reverse order, mixed order
     *  - timespan result: same start=end, or start<end
     *
     * getMentionedUsers(tweets):
     *  - no mentions
     *  - one valid mention
     *  - multiple valid mentions
     *  - mentions with different cases (case-insensitive)
     *  - duplicate mentions across tweets (should appear once)
     *  - invalid mentions (inside email, preceded/followed by username chars)
     *  - mentions at start, middle, and end of tweet
     *  - empty tweet list
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "charlie", "hey @Alice let's meet", d3);
    private static final Tweet tweet4 = new Tweet(4, "dave", "@Bob what do you think?", d1);
    private static final Tweet tweet5 = new Tweet(5, "eve", "email me at bob@mit.edu", d2);
    private static final Tweet tweet6 = new Tweet(6, "frank", "shoutout to @alice and @BOB!", d3);
    private static final Tweet tweet7 = new Tweet(7, "grace", "multiple @bob @charlie mentions", d1);

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // ======================= getTimespan() tests =======================

    @Test
    public void testGetTimespanOneTweet() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1));
        assertEquals("start should equal tweet time", d1, timespan.getStart());
        assertEquals("end should equal tweet time", d1, timespan.getEnd());
    }

    @Test
    public void testGetTimespanMultipleInOrder() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet3));
        assertEquals(d1, timespan.getStart());
        assertEquals(d3, timespan.getEnd());
    }

    @Test
    public void testGetTimespanMultipleReverseOrder() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet3, tweet2, tweet1));
        assertEquals(d1, timespan.getStart());
        assertEquals(d3, timespan.getEnd());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetTimespanEmptyList() {
        Extract.getTimespan(Collections.emptyList());
    }

    // ======================= getMentionedUsers() tests =======================

    @Test
    public void testGetMentionedUsersSingleMention() {
        Set<String> mentioned = Extract.getMentionedUsers(Arrays.asList(tweet3));
        assertEquals(new HashSet<>(Arrays.asList("alice")), mentioned);
    }

    @Test
    public void testGetMentionedUsersMultipleMentionsCaseInsensitive() {
        Set<String> mentioned = Extract.getMentionedUsers(Arrays.asList(tweet3, tweet4, tweet6));
        // alice appears twice, Bob appears in different cases
        assertEquals(new HashSet<>(Arrays.asList("alice", "bob")), mentioned);
    }

    @Test
    public void testGetMentionedUsersNoMentions() {
        Set<String> mentioned = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet2));
        assertTrue(mentioned.isEmpty());
    }

    @Test
    public void testGetMentionedUsersInvalidEmailAddress() {
        Set<String> mentioned = Extract.getMentionedUsers(Arrays.asList(tweet5));
        assertTrue("email mention should not be counted", mentioned.isEmpty());
    }

    @Test
    public void testGetMentionedUsersMultipleMentionsInOneTweet() {
        Set<String> mentioned = Extract.getMentionedUsers(Arrays.asList(tweet7));
        assertEquals(new HashSet<>(Arrays.asList("bob", "charlie")), mentioned);
    }

    @Test
    public void testGetMentionedUsersEmptyList() {
        Set<String> mentioned = Extract.getMentionedUsers(Collections.emptyList());
        assertTrue(mentioned.isEmpty());
    }
}
