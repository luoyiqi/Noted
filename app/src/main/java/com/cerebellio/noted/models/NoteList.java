package com.cerebellio.noted.models;

import java.util.ArrayList;

/**
 * Created by Sam on 08/02/2016.
 */
public class NoteList {

    private long mId;
    private ArrayList<Note> mNotes;

    public NoteList() {}

    public NoteList(long id, ArrayList<Note> notes) {
        mId = id;
        mNotes = notes;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public ArrayList<Note> getNotes() {
        return mNotes;
    }

    public void setNotes(ArrayList<Note> notes) {
        mNotes = notes;
    }
}
