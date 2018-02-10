package com.jtulayan.util;

import java.util.Comparator;

/**
 * @author Jared Tulayan
 */
public class ScoreComparator implements Comparator<ScoreEntry> {
    public ScoreComparator() {

    }

    @Override
    public int compare(ScoreEntry o1, ScoreEntry o2) {
        return (int) Math.signum(o2.SCORE - o1.SCORE);
    }
}