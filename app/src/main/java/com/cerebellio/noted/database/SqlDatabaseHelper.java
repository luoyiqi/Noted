package com.cerebellio.noted.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cerebellio.noted.models.CheckList;
import com.cerebellio.noted.models.CheckListItem;
import com.cerebellio.noted.models.Item;
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
    private static final String COLUMN_NOTES_COLOUR = "colour";
    private static final String COLUMN_NOTES_CONTENT = "content";
    private static final String COLUMN_NOTES_TRASHED = "trashed";
    private static final String COLUMN_NOTES_URGENT = "urgent";

    private static final String TABLE_CHECKLIST = "checklist";
    private static final String COLUMN_CHECKLIST_TITLE = "name";
    private static final String COLUMN_CHECKLIST_LAST_MODIFIED_DATE = "last_modified_date";
    private static final String COLUMN_CHECKLIST_COLOUR = "colour";
    private static final String COLUMN_CHECKLIST_TRASHED = "trashed";

    private static final String TABLE_CHECKLIST_ITEM = "checklist_item";
    private static final String COLUMN_CHECKLIST_ITEM_CHECKLIST_ID = "checklist_id";
    private static final String COLUMN_CHECKLIST_ITEM_CONTENT = "content";
    private static final String COLUMN_CHECKLIST_ITEM_COMPLETED = "completed";
    private static final String COLUMN_CHECKLIST_ITEM_TRASHED = "trashed";



    private static final String NOTES_CREATION_STRING = "CREATE TABLE "
            + TABLE_NOTES + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NOTES_LAST_MODIFIED_DATE + " INTEGER,"
            + COLUMN_NOTES_TITLE + " TEXT,"
            + COLUMN_NOTES_COLOUR + " INTEGER,"
            + COLUMN_NOTES_CONTENT + " TEXT,"
            + COLUMN_NOTES_TRASHED + " INTEGER DEFAULT 0,"
            + COLUMN_NOTES_URGENT + " INTEGER DEFAULT 0)";

    private static final String CHECKLIST_CREATION_STRING = "CREATE TABLE "
            + TABLE_CHECKLIST + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CHECKLIST_TITLE + " TEXT,"
            + COLUMN_CHECKLIST_LAST_MODIFIED_DATE + " INTEGER,"
            + COLUMN_CHECKLIST_COLOUR + " INTEGER,"
            + COLUMN_CHECKLIST_TRASHED + " INTEGER DEFAULT 0)";

    private static final String CHECKLIST_ITEM_CREATION_STRING = "CREATE TABLE "
            + TABLE_CHECKLIST_ITEM + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CHECKLIST_ITEM_CHECKLIST_ID + " INTEGER,"
            + COLUMN_CHECKLIST_ITEM_CONTENT + " TEXT,"
            + COLUMN_CHECKLIST_ITEM_COMPLETED + " INTEGER DEFAULT 0,"
            + COLUMN_CHECKLIST_ITEM_TRASHED + " INTEGER DEFAULT 0)";


    private Context mContext;

    public SqlDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(NOTES_CREATION_STRING);
        sqLiteDatabase.execSQL(CHECKLIST_CREATION_STRING);
        sqLiteDatabase.execSQL(CHECKLIST_ITEM_CREATION_STRING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //TODO write onUpgrade for db
    }

    /**
     * Get the {@link CheckList} referred to by the given ID
     * @param checkListId       ID of the {@link CheckList} to retrieve
     * @return                  {@link Note} retrieved
     */
    public CheckList getChecklist(long checkListId) {
        String where = " WHERE "
                + COLUMN_ID
                + " = "
                + checkListId;
        List<CheckList> checkLists = getCheckLists(where);
        if (checkLists.size() > 0) {
            return checkLists.get(0);
        } else {
            return null;
        }
    }

    /**
     * Retrieves a List of {@link CheckList} from the database
     * @param whereCondition        WHERE condition to supply to query
     * @return                      List of retrieved {@link CheckList}
     */
    public List<CheckList> getCheckLists(String whereCondition) {
        SQLiteDatabase db = getReadableDatabase();
        List<CheckList> checkLists = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CHECKLIST
                + whereCondition;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {

                CheckList checkList = new CheckList();

                checkList.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                checkList.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_CHECKLIST_TITLE)));
                checkList.setLastModifiedDate(cursor.getLong(cursor.getColumnIndex(COLUMN_CHECKLIST_LAST_MODIFIED_DATE)));
                checkList.setColour(cursor.getInt(cursor.getColumnIndex(COLUMN_CHECKLIST_COLOUR)));
                checkList.setItems(getChecklistItems(" WHERE "
                        + COLUMN_CHECKLIST_ITEM_CHECKLIST_ID
                        + " = "
                        + checkList.getId()
                        + " AND " + COLUMN_CHECKLIST_ITEM_TRASHED
                        + " = 0"));

                checkLists.add(checkList);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return checkLists;
    }

    /**
     * Get all {@link Note} in the database
     * @return          List of all {@link Note}
     */
    public List<CheckList> getAllChecklists() {
        return getCheckLists(" WHERE " + COLUMN_CHECKLIST_TRASHED + " = 0");
    }

    /**
     * Retrieves a List of {@link CheckListItem} from the database
     * @param whereCondition        WHERE condition to supply to query
     * @return                      List of retrieved {@link CheckListItem}
     */
    public List<CheckListItem> getChecklistItems(String whereCondition) {
        SQLiteDatabase db = getReadableDatabase();
        List<CheckListItem> checkListItems = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CHECKLIST_ITEM
                + whereCondition;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {
                CheckListItem item = new CheckListItem();

                item.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                item.setChecklistId(cursor.getLong(cursor.getColumnIndex(COLUMN_CHECKLIST_ITEM_CHECKLIST_ID)));
                item.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CHECKLIST_ITEM_CONTENT)));
                item.setIsCompleted(cursor.getInt(cursor.getColumnIndex(COLUMN_CHECKLIST_ITEM_COMPLETED)) == 1);
                item.setIsTrashed(cursor.getInt(cursor.getColumnIndex(COLUMN_CHECKLIST_ITEM_TRASHED)) == 1);

                checkListItems.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return checkListItems;
    }

    public long getLatestChecklistId() {
        SQLiteDatabase db = getReadableDatabase();
        long id = 0;
        String query = "SELECT * FROM " + TABLE_CHECKLIST
                + " ORDER BY " + COLUMN_ID
                + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
        }

        cursor.close();

        return id;
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
                + whereCondition
                + " ORDER BY "
                + COLUMN_NOTES_LAST_MODIFIED_DATE
                + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {

                Note note = new Note();

                note.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_CONTENT)));
                note.setColour(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTES_COLOUR)));
                note.setIsTrashed(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTES_TRASHED)) == 1);
                note.setIsUrgent(cursor.getInt(cursor.getColumnIndex(COLUMN_NOTES_URGENT)) == 1);
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
        return getNotes(" WHERE " + COLUMN_NOTES_TRASHED + " = 0");
    }

    /**
     * Check if {@link Note} exists.
     * If not, insert into database.
     * If so, update  database.
      * @param note             {@link Note} to insert/update
     */
    public void addOrEditNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTES_TITLE, note.getTitle());
        contentValues.put(COLUMN_NOTES_CONTENT, note.getContent());
        contentValues.put(COLUMN_NOTES_COLOUR, note.getColour());
        contentValues.put(COLUMN_NOTES_LAST_MODIFIED_DATE, note.getLastModifiedDate());
        contentValues.put(COLUMN_NOTES_TRASHED, note.isTrashed() ? 1 : 0);
        contentValues.put(COLUMN_NOTES_URGENT, note.isUrgent() ? 1 : 0);

        //check to see if this note is already in db
        //if so edit, if not insert
        if (getNote(note.getId()) == null) {
            db.insert(TABLE_NOTES, null, contentValues);
        } else {
            db.update(TABLE_NOTES, contentValues, COLUMN_ID + " = ?",
                    new String[] {String.valueOf(note.getId())});
        }
    }

    /**
     * Check if {@link CheckList} exists.
     * If not, insert into database.
     * If so, update  database.
      * @param checkList             {@link CheckList} to insert/update
     */
    public void updateChecklist(CheckList checkList) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CHECKLIST_TITLE, checkList.getTitle());
        contentValues.put(COLUMN_CHECKLIST_COLOUR, checkList.getColour());
        contentValues.put(COLUMN_CHECKLIST_LAST_MODIFIED_DATE, checkList.getLastModifiedDate());
        contentValues.put(COLUMN_NOTES_TRASHED, checkList.isTrashed() ? 1 : 0);

        for (CheckListItem item : checkList.getItems()) {
            addOrEditChecklistItem(item);
        }

        db.update(TABLE_CHECKLIST, contentValues, COLUMN_ID + " = ?",
                new String[]{String.valueOf(checkList.getId())});
    }

    public void addBlankChecklist() {
        SQLiteDatabase db = getWritableDatabase();
        CheckList checkList = new CheckList();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CHECKLIST_TITLE, checkList.getTitle());
        contentValues.put(COLUMN_CHECKLIST_LAST_MODIFIED_DATE, checkList.getLastModifiedDate());
        contentValues.put(COLUMN_CHECKLIST_COLOUR, checkList.getColour());
        contentValues.put(COLUMN_NOTES_TRASHED, checkList.isTrashed() ? 1 : 0);

        db.insert(TABLE_CHECKLIST, null, contentValues);
    }

    /**
     * Check if {@link CheckListItem} exists.
     * If not, insert into database.
     * If so, update  database.
      * @param checkListItem             {@link CheckListItem} to insert/update
     */
    public void addOrEditChecklistItem(CheckListItem checkListItem) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CHECKLIST_ITEM_CHECKLIST_ID, checkListItem.getChecklistId());
        contentValues.put(COLUMN_CHECKLIST_ITEM_CONTENT, checkListItem.getContent());
        contentValues.put(COLUMN_CHECKLIST_ITEM_COMPLETED, checkListItem.isCompleted() ? 1 : 0);
        contentValues.put(COLUMN_CHECKLIST_ITEM_TRASHED, checkListItem.isTrashed() ? 1 : 0);

        if (checkListItem.getId() == 0) {
            db.insert(TABLE_CHECKLIST_ITEM, null, contentValues);
            return;
        }

        //check to see if this checklistItem is already in db
        //if so edit, if not insert
        if (getChecklistItems(" WHERE " + COLUMN_ID + " = " + checkListItem.getId()) == null) {
            db.insert(TABLE_CHECKLIST_ITEM, null, contentValues);
        } else {
            db.update(TABLE_CHECKLIST_ITEM, contentValues, COLUMN_ID + " = ?",
                    new String[] {String.valueOf(checkListItem.getId())});
        }
    }

    public void deleteItem(Item item) {
        item.setIsTrashed(true);
        addOrEditItem(item);
    }

    public void addOrEditItem(Item item) {
        if (item instanceof Note) {
            addOrEditNote((Note) item);
        } else if (item instanceof CheckList){
            updateChecklist((CheckList) item);
        } else {
            addOrEditChecklistItem((CheckListItem) item);
        }
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
