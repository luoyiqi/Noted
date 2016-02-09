package com.cerebellio.noted.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cerebellio.noted.models.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam on 09/02/2016.
 */
public class SqlDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "noted";
    private static final int DATABASE_VERSION = 1;

    private static final String COLUMN_ID = "_id";

    private static final String TABLE_NOTES = "notes";
    private static final String COLUMN_NOTES_LAST_MODIFIED_DATE = "last_modified_date";
    private static final String COLUMN_NOTES_TITLE = "title";
    private static final String COLUMN_NOTES_CONTENT = "content";

    private static final String NOTES_CREATION_STRING = "CREATE TABLE "
            + TABLE_NOTES + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NOTES_LAST_MODIFIED_DATE + " INTEGER,"
            + COLUMN_NOTES_TITLE + " TEXT,"
            + COLUMN_NOTES_CONTENT + " TEXT)";

    private Context mContext;

    public SqlDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(NOTES_CREATION_STRING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //TODO write onUpgrade for db
    }

    /**
     * Retrieves a List of {@link Note} from the database
     * @param whereCondition        WHERE condition to supply to query
     * @return                      List of retrieved {@link Note}
     */
    public List<Note> getNotes(String whereCondition) {
        SQLiteDatabase db = getReadableDatabase();
        List<Note> notes = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NOTES
                + whereCondition;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {

                Note note = new Note();

                note.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_CONTENT)));
                note.setLastModifiedDate(cursor.getLong(cursor.getColumnIndex(COLUMN_NOTES_LAST_MODIFIED_DATE)));

                notes.add(note);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return notes;
    }

    /**
     * Get the {@link Note} referred to by the given ID
     * @param noteId            ID of the {@link Note} to retrieve
     * @return                  {@link Note} retrieved
     */
    public Note getNote(long noteId) {
        String where = " WHERE "
                + COLUMN_ID
                + " = "
                + noteId;
        List<Note> notes = getNotes(where);
        if (notes.size() > 0) {
            return notes.get(0);
        } else {
            return null;
        }
    }

    /**
     * Get all {@link Note} in the database
     * @return          List of all {@link Note}
     */
    public List<Note> getAllNotes() {
        return getNotes("");
    }

    /**
     * Insert a new {@link Note} into the database
      * @param note             {@link Note} to insert
     */
    public void addNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTES_TITLE, note.getTitle());
        contentValues.put(COLUMN_NOTES_CONTENT, note.getContent());
        contentValues.put(COLUMN_NOTES_LAST_MODIFIED_DATE, note.getLastModifiedDate());

        db.insert(TABLE_NOTES, null, contentValues);
    }


    /**
     * Close the database
     */
    public void closeDB() {
        SQLiteDatabase db = getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
