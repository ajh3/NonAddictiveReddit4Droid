package com.aaronhalbert.nosurfforreddit.viewstate;

import android.text.Spanned;

/* data structure to hold cleaned/transformed comment data from the Reddit API */

public class CommentsViewState {
    public int numComments;
    public Spanned[] commentBodies;
    public String[] commentDetails;
    public String id;

    public CommentsViewState(int numComments, String id) {
        this.numComments = numComments;
        commentBodies = new Spanned[numComments];
        commentDetails = new String[numComments];
        this.id = id;
    }
}
