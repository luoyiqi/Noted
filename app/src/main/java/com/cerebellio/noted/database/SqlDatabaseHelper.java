package com.cerebellio.noted.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cerebellio.noted.models.CheckList;
import com.cerebellio.noted.models.CheckListItem;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.NavDrawerItem;
import com.cerebellio.noted.models.Note;
import com.cerebellio.noted.models.Sketch;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Database helper, allows CRUD operations on database
 */
public class SqlDatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = TextFunctions.makeLogTag(SqlDatabaseHelper.class);

    /**
     * Database constants
     */
    private static final String DATABASE_NAME = "noted";
    private static final int DATABASE_VERSION = 1;

    /**
     * Common column names
     */
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_COLOUR = "colour";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_IMPORTANT = "important";
    private static final String COLUMN_TAGS = "tags";
    private static final String COLUMN_CREATED_DATE = "created_date";
    private static final String COLUMN_EDITED_DATE = "edited_date";

    /**
     * Table names
     */
    private static final String TABLE_NOTES = "notes";
    private static final String TABLE_CHECKLIST = "checklist";
    private static final String TABLE_CHECKLIST_ITEM = "checklist_item";
    private static final String TABLE_SKETCH = "sketch";

    /**
     * Columns only for Note table
     */
    private static final String COLUMN_NOTES_CONTENT = "content";

    /**
     * Columns only for ChecklistItem table
     */
    private static final String COLUMN_CHECKLIST_ITEM_CHECKLIST_ID = "checklist_id";
    private static final String COLUMN_CHECKLIST_ITEM_CONTENT = "content";
    private static final String COLUMN_CHECKLIST_ITEM_COMPLETED = "completed";
    private static final String COLUMN_CHECKLIST_ITEM_POSITION = "position";

    /**
     * Columns only for Sketch table
     */
    private static final String COLUMN_SKETCH_IMAGE_PATH = "image_path";


    /**
     * Notes table creator
     */
    private static final String NOTES_CREATION_STRING = "CREATE TABLE "
            + TABLE_NOTES + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_COLOUR + " INTEGER,"
            + COLUMN_NOTES_CONTENT + " TEXT,"
            + COLUMN_CREATED_DATE + " INTEGER,"
            + COLUMN_EDITED_DATE + " INTEGER,"
            + COLUMN_IMPORTANT + " INTEGER DEFAULT 0,"
            + COLUMN_TAGS + " TEXT,"
            + COLUMN_STATUS + " TEXT DEFAULT '" + Item.Status.PINBOARD.toString() + "')";

    /**
     * Checklist table creator
     */
    private static final String CHECKLIST_CREATION_STRING = "CREATE TABLE "
            + TABLE_CHECKLIST + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_COLOUR + " INTEGER,"
            + COLUMN_CREATED_DATE + " INTEGER,"
            + COLUMN_EDITED_DATE + " INTEGER,"
            + COLUMN_IMPORTANT + " INTEGER DEFAULT 0,"
            + COLUMN_TAGS + " TEXT,"
            + COLUMN_STATUS + " TEXT DEFAULT '" + Item.Status.PINBOARD.toString() + "')";

    /**
     * Checklist Item table creator
     */
    private static final String CHECKLIST_ITEM_CREATION_STRING = "CREATE TABLE "
            + TABLE_CHECKLIST_ITEM + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CHECKLIST_ITEM_CHECKLIST_ID + " INTEGER,"
            + COLUMN_CHECKLIST_ITEM_CONTENT + " TEXT,"
            + COLUMN_CHECKLIST_ITEM_COMPLETED + " INTEGER DEFAULT 0,"
            + COLUMN_CHECKLIST_ITEM_POSITION + " INTEGER,"
            + COLUMN_STATUS + " TEXT DEFAULT '" + Item.Status.PINBOARD.toString() + "')";

    /**
     * Sketch table creator
     */
    private static final String SKETCH_CREATION_STRING = "CREATE TABLE "
            + TABLE_SKETCH + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_COLOUR + " INTEGER,"
            + COLUMN_SKETCH_IMAGE_PATH + " TEXT,"
            + COLUMN_CREATED_DATE + " INTEGER,"
            + COLUMN_EDITED_DATE + " INTEGER,"
            + COLUMN_IMPORTANT + " INTEGER DEFAULT 0,"
            + COLUMN_TAGS + " TEXT,"
            + COLUMN_STATUS + " TEXT DEFAULT '" + Item.Status.PINBOARD.toString() + "')";


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
        sqLiteDatabase.execSQL(SKETCH_CREATION_STRING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //TODO write onUpgrade for db
    }


    /**
     * Find {@link Item} in the database by its ID
     *
     * @param id            ID to search for
     * @param type          i.e. {@link Note}, {@link CheckList}, {@link CheckListItem}, {@link Sketch}
     * @return              {@link Item} from the database
     */
    public Item getItemById(long id, Item.Type type) {

        String where = " WHERE "
                + COLUMN_ID
                + " = "
                + id;

        switch (type) {
            default:
            case NOTE:
                return getNotes(where).get(0);
            case CHECKLIST:
                return getCheckLists(where).get(0);
            case SKETCH:
                return getSketches(where).get(0);
            case CHECKLIST_ITEM:
                return getChecklistItems(where).get(0);
        }
    }

    /**
     * Get all {@link Item} from the database for a specific type, i.e. items
     *
     * which have been trashed
     * @param type          {@link com.cerebellio.noted.models.NavDrawerItem.NavDrawerItemType} type to search for
     * @return              List of returned {@link Item}
     */
    public List<Item> getAllItems(NavDrawerItem.NavDrawerItemType type) {

        List<Item> items = new ArrayList<>();

        items.addAll(getAllNotes(type));
        items.addAll(getAllChecklists(type));
        items.addAll(getAllSketches(type));

        return items;
    }

    /**
     * Retrieves a List of {@link CheckList} from the database
     *
     * @param whereCondition        WHERE condition to supply to query
     * @return                      List of retrieved {@link CheckList}
     */
    public List<CheckList> getCheckLists(String whereCondition) {

        SQLiteDatabase db = getReadableDatabase();
        List<CheckList> checkLists = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CHECKLIST
                + whereCondition
                + " ORDER BY "
                + COLUMN_EDITED_DATE
                + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {

                CheckList checkList = new CheckList();

                checkList.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                checkList.setColour(cursor.getInt(cursor.getColumnIndex(COLUMN_COLOUR)));
                checkList.setCreatedDate(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_DATE)));
                checkList.setEditedDate(cursor.getLong(cursor.getColumnIndex(COLUMN_EDITED_DATE)));
                checkList.setIsImportant(cursor.getInt(cursor.getColumnIndex(COLUMN_IMPORTANT)) == 1);
                checkList.setTagString(cursor.getString(cursor.getColumnIndex(COLUMN_TAGS)));
                checkList.setStatus(Item.Status.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS))));

                //Get all ChecklistItems for this Checklist
                checkList.setItems(getChecklistItems(" WHERE "
                        + COLUMN_CHECKLIST_ITEM_CHECKLIST_ID
                        + " = "
                        + checkList.getId()
                        + " AND " + COLUMN_STATUS
                        + " != '" + Item.Status.DELETED + "'"));

                checkLists.add(checkList);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return checkLists;
    }

    /**
     * Get all {@link CheckList} in the database
     *
     * @return          List of all {@link CheckList}
     */
    public List<CheckList> getAllChecklists(NavDrawerItem.NavDrawerItemType type) {
        return getCheckLists(getItemTypeWhereString(type));
    }

    /**
     * Retrieves a List of {@link CheckListItem} from the database
     *
     * @param whereCondition        WHERE condition to supply to query
     * @return                      List of retrieved {@link CheckListItem}
     */
    public List<CheckListItem> getChecklistItems(String whereCondition) {

        SQLiteDatabase db = getReadableDatabase();
        List<CheckListItem> checkListItems = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CHECKLIST_ITEM
                + whereCondition
                + " ORDER BY "
                + COLUMN_CHECKLIST_ITEM_POSITION
                + " ASC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {

                CheckListItem item = new CheckListItem();

                item.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                item.setChecklistId(cursor.getLong(cursor.getColumnIndex(COLUMN_CHECKLIST_ITEM_CHECKLIST_ID)));
                item.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CHECKLIST_ITEM_CONTENT)));
                item.setIsCompleted(cursor.getInt(cursor.getColumnIndex(COLUMN_CHECKLIST_ITEM_COMPLETED)) == 1);
                item.setPosition(cursor.getInt(cursor.getColumnIndex(COLUMN_CHECKLIST_ITEM_POSITION)));
                item.setStatus(Item.Status.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS))));

                checkListItems.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return checkListItems;
    }

    /**
     * Retrieves a List of {@link Sketch} from the database
     *
     * @param whereCondition        WHERE condition to supply to query
     * @return                      List of retrieved {@link Sketch}
     */
    public List<Sketch> getSketches(String whereCondition) {

        SQLiteDatabase db = getReadableDatabase();
        List<Sketch> sketches = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_SKETCH
                + whereCondition
                + " ORDER BY "
                + COLUMN_EDITED_DATE
                + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {

                Sketch sketch = new Sketch();

                sketch.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                sketch.setColour(cursor.getInt(cursor.getColumnIndex(COLUMN_COLOUR)));
                sketch.setImagePath(cursor.getString(cursor.getColumnIndex(COLUMN_SKETCH_IMAGE_PATH)));
                sketch.setCreatedDate(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_DATE)));
                sketch.setEditedDate(cursor.getLong(cursor.getColumnIndex(COLUMN_EDITED_DATE)));
                sketch.setIsImportant(cursor.getInt(cursor.getColumnIndex(COLUMN_IMPORTANT)) == 1);
                sketch.setTagString(cursor.getString(cursor.getColumnIndex(COLUMN_TAGS)));
                sketch.setStatus(Item.Status.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS))));

                sketches.add(sketch);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return sketches;
    }

    /**
     * Get all {@link Sketch} in the database
     *
     * @return          List of all {@link Sketch}
     */
    public List<Sketch> getAllSketches(NavDrawerItem.NavDrawerItemType type) {
        return getSketches(getItemTypeWhereString(type));
    }

    /**
     * Retrieves a List of {@link Note} from the database
     *
     * @param whereCondition        WHERE condition to supply to query
     * @return                      List of retrieved {@link Note}
     */
    public List<Note> getNotes(String whereCondition) {

        SQLiteDatabase db = getReadableDatabase();
        List<Note> notes = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NOTES
                + whereCondition
                + " ORDER BY "
                + COLUMN_EDITED_DATE
                + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            do {

                Note note = new Note();

                note.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                note.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES_CONTENT)));
                note.setColour(cursor.getInt(cursor.getColumnIndex(COLUMN_COLOUR)));
                note.setCreatedDate(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_DATE)));
                note.setEditedDate(cursor.getLong(cursor.getColumnIndex(COLUMN_EDITED_DATE)));
                note.setIsImportant(cursor.getInt(cursor.getColumnIndex(COLUMN_IMPORTANT)) == 1);
                note.setTagString(cursor.getString(cursor.getColumnIndex(COLUMN_TAGS)));
                note.setStatus(Item.Status.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS))));

                notes.add(note);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return notes;
    }

    /**
     * Get all {@link Note} in the database
     *
     * @return          List of all {@link Note}
     */
    public List<Note> getAllNotes(NavDrawerItem.NavDrawerItemType type) {
        return getNotes(getItemTypeWhereString(type));
    }

    /**
     * Create WHERE string depending upon {@link com.cerebellio.noted.models.Item.Type}
     *
     * @param type          {@link com.cerebellio.noted.models.NavDrawerItem.NavDrawerItemType} to convert
     * @return              created WHERE String
     */
    private String getItemTypeWhereString(NavDrawerItem.NavDrawerItemType type) {

        String itemStatus = convertItemType(type);
        return " WHERE " + COLUMN_STATUS + " = '" + itemStatus + "'";
    }

    /**
     * Converts {@link com.cerebellio.noted.models.NavDrawerItem.NavDrawerItemType} to equivalent
     * {@link com.cerebellio.noted.models.Item.Status}
     *
     * @param type      {@link com.cerebellio.noted.models.NavDrawerItem.NavDrawerItemType} to convert
     * @return          equivalent String
     */
    private String convertItemType(NavDrawerItem.NavDrawerItemType type) {
        switch (type) {
            default:
            case PINBOARD:
                return Item.Status.PINBOARD.toString();
            case ARCHIVE:
                return Item.Status.ARCHIVED.toString();
            case TRASH:
                return Item.Status.TRASHED.toString();
        }
    }

    /**
     * Add a blank new {@link CheckList} to the database
     *
     * @return          ID of new {@link CheckList}
     */
    public long addBlankChecklist() {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COLOUR,
                UtilityFunctions.getRandomIntegerFromArray(Constants.COLOURS));
        contentValues.put(COLUMN_IMPORTANT, 0);
        contentValues.put(COLUMN_CREATED_DATE, new Date().getTime());
        contentValues.put(COLUMN_EDITED_DATE, 0);
        contentValues.put(COLUMN_TAGS, "");
        contentValues.put(COLUMN_STATUS, Item.Status.PINBOARD.toString());

        long id = db.insert(TABLE_CHECKLIST, null, contentValues);

        addBlankChecklistItem(id);

        return id;
    }

    /**
     * Add a blank new {@link CheckListItem} to the database
     *
     * @return          ID of new {@link CheckListItem}
     */
    public long addBlankChecklistItem(long checklistId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CHECKLIST_ITEM_CHECKLIST_ID, checklistId);
        contentValues.put(COLUMN_CHECKLIST_ITEM_CONTENT, "");
        contentValues.put(COLUMN_CHECKLIST_ITEM_COMPLETED, 0);
        contentValues.put(COLUMN_CHECKLIST_ITEM_POSITION, 0);
        contentValues.put(COLUMN_STATUS, Item.Status.PINBOARD.toString());

        return db.insert(TABLE_CHECKLIST_ITEM, null, contentValues);
    }

    /**
     * Add a blank new {@link Note} to the database
     *
     * @return          ID of new {@link Note}
     */
    public long addBlankNote() {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COLOUR,
                UtilityFunctions.getRandomIntegerFromArray(Constants.COLOURS));
        contentValues.put(COLUMN_NOTES_CONTENT, "");
        contentValues.put(COLUMN_CREATED_DATE, new Date().getTime());
        contentValues.put(COLUMN_EDITED_DATE, 0);
        contentValues.put(COLUMN_IMPORTANT, 0);
        contentValues.put(COLUMN_TAGS, "");
        contentValues.put(COLUMN_STATUS, Item.Status.PINBOARD.toString());

        return db.insert(TABLE_NOTES, null, contentValues);
    }

    /**
     * Add a blank new {@link Sketch} to the database
     *
     * @return          ID of new {@link Sketch}
     */
    public long addBlankSketch() {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SKETCH_IMAGE_PATH, "");
        contentValues.put(COLUMN_COLOUR,
                UtilityFunctions.getRandomIntegerFromArray(Constants.COLOURS));
        contentValues.put(COLUMN_CREATED_DATE, new Date().getTime());
        contentValues.put(COLUMN_EDITED_DATE, 0);
        contentValues.put(COLUMN_IMPORTANT, 0);
        contentValues.put(COLUMN_TAGS, "");
        contentValues.put(COLUMN_STATUS, Item.Status.PINBOARD.toString());

        return db.insert(TABLE_SKETCH, null, contentValues);
    }

    /**
     * Check if {@link Note} exists.
     * If not, insert into database.
     * If so, update  database.
     *
     * @param note             {@link Note} to insert/update
     */
    public void addOrEditNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTES_CONTENT, note.getContent());
        contentValues.put(COLUMN_COLOUR, note.getColour());
        contentValues.put(COLUMN_CREATED_DATE, note.getCreatedDate());
        contentValues.put(COLUMN_EDITED_DATE, note.getEditedDate());
        contentValues.put(COLUMN_IMPORTANT, note.isImportant());
        contentValues.put(COLUMN_TAGS, note.getRawTagString());
        contentValues.put(COLUMN_STATUS, note.getStatus().toString());

        //check to see if this note is already in db
        //if so edit, if not insert
        if (getItemById(note.getId(), Item.Type.NOTE) == null) {
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
     *
     * @param checkList             {@link CheckList} to insert/update
     */
    public void addOrEditChecklist(CheckList checkList) {
        SQLiteDatabase db = getWritableDatabase();

        checkList.assignPositions();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COLOUR, checkList.getColour());
        contentValues.put(COLUMN_CREATED_DATE, checkList.getCreatedDate());
        contentValues.put(COLUMN_EDITED_DATE, checkList.getEditedDate());
        contentValues.put(COLUMN_IMPORTANT, checkList.isImportant());
        contentValues.put(COLUMN_TAGS, checkList.getRawTagString());
        contentValues.put(COLUMN_STATUS, checkList.getStatus().toString());

        //Have to add each individual ChecklistItem in this Checklist
        for (CheckListItem item : checkList.getItems()) {
            addOrEditChecklistItem(item);
        }

        //check to see if this Checklist is already in db
        //if so edit, if not insert
        if (getItemById(checkList.getId(), Item.Type.CHECKLIST) == null) {
            db.insert(TABLE_CHECKLIST, null, contentValues);
        } else {
            db.update(TABLE_CHECKLIST, contentValues, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(checkList.getId())});
        }
    }

    /**
     * Check if {@link CheckListItem} exists.
     * If not, insert into database.
     * If so, update  database.
     *
      * @param checkListItem             {@link CheckListItem} to insert/update
     */
    public void addOrEditChecklistItem(CheckListItem checkListItem) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CHECKLIST_ITEM_CHECKLIST_ID, checkListItem.getChecklistId());
        contentValues.put(COLUMN_CHECKLIST_ITEM_CONTENT, checkListItem.getContent());
        contentValues.put(COLUMN_CHECKLIST_ITEM_COMPLETED, checkListItem.isCompleted() ? 1 : 0);
        contentValues.put(COLUMN_CHECKLIST_ITEM_POSITION, checkListItem.getPosition());
        contentValues.put(COLUMN_STATUS, checkListItem.getStatus().toString());

        //check to see if this checklistItem is already in db
        //if so edit, if not insert
        if (getItemById(checkListItem.getId(), Item.Type.CHECKLIST_ITEM) == null) {
            db.insert(TABLE_CHECKLIST_ITEM, null, contentValues);
        } else {
            db.update(TABLE_CHECKLIST_ITEM, contentValues, COLUMN_ID + " = ?",
                    new String[] {String.valueOf(checkListItem.getId())});
        }
    }

    /**
     * Check if {@link Sketch} exists.
     * If not, insert into database.
     * If so, update  database.
     *
      * @param sketch             {@link Sketch} to insert/update
     */
    public void addOrEditSketch(Sketch sketch) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COLOUR, sketch.getColour());
        contentValues.put(COLUMN_SKETCH_IMAGE_PATH, sketch.getImagePath());
        contentValues.put(COLUMN_CREATED_DATE, sketch.getCreatedDate());
        contentValues.put(COLUMN_EDITED_DATE, sketch.getEditedDate());
        contentValues.put(COLUMN_IMPORTANT, sketch.isImportant());
        contentValues.put(COLUMN_TAGS, sketch.getRawTagString());
        contentValues.put(COLUMN_STATUS, sketch.getStatus().toString());

        //check to see if this sketch is already in db
        //if so edit, if not insert
        if (getItemById(sketch.getId(), Item.Type.SKETCH) == null) {
            db.insert(TABLE_SKETCH, null, contentValues);
        } else {
            db.update(TABLE_SKETCH, contentValues, COLUMN_ID + " = ?",
                    new String[] {String.valueOf(sketch.getId())});
        }
    }

    /**
     * Parses the type of {@link Item} given and forwards to correct method
     *
     * @param item          {@link Item} to add
     */
    public void addOrEditItem(Item item) {

        //Funnel to respective method depending on type of Item received
        if (item instanceof Note) {
            addOrEditNote((Note) item);
        } else if (item instanceof CheckList){
            addOrEditChecklist((CheckList) item);
        } else if (item instanceof CheckListItem){
            addOrEditChecklistItem((CheckListItem) item);
        } else {
            addOrEditSketch((Sketch) item);
        }
    }

    /**
     * Search the database for {@link Item} containg a given query,
     * with a {@link Item#mStatus} equivalent to type
     *
     * @param query         term to search for
     * @param type          equivalent to {@link com.cerebellio.noted.models.Item.Status}
     * @return              List of {@link Item} matching search terms
     */
    public List<Item> searchItems(String query, NavDrawerItem.NavDrawerItemType type) {

        List<Item> items = new ArrayList<>();

        //WHERE (content LIKE '%x%' OR tags LIKE '%x%') AND status = 'type'
        items.addAll(getNotes(" WHERE ("
                + COLUMN_NOTES_CONTENT
                + " LIKE '%"
                + query
                + "%' OR "
                + COLUMN_TAGS
                + " LIKE '%"
                + query
                + "%') AND "
                + COLUMN_STATUS
                + " = '"
                + convertItemType(type)
                + "'"));

        //WHERE tags LIKE '%x%' AND status = 'type'
        items.addAll(getCheckLists(" WHERE "
                + COLUMN_TAGS
                + " LIKE '%"
                + query
                + "%' AND "
                + COLUMN_STATUS
                + " = '"
                + convertItemType(type)
                + "'"));

        //WHERE tags LIKE '%x%' AND status = 'type'
        items.addAll(getSketches(" WHERE "
                + COLUMN_TAGS
                + " LIKE '%"
                + query
                + "%' AND "
                + COLUMN_STATUS
                + " = '"
                + convertItemType(type)
                + "'"));

        //ChecklistItems need to be added separately because
        //we need to check if the Checklist it belongs to has already been added
        List<CheckListItem> checkListItems = new ArrayList<>();

        //WHERE content LIKE '%x%'
        checkListItems.addAll(getChecklistItems(" WHERE " + COLUMN_CHECKLIST_ITEM_CONTENT + " LIKE '%" + query + "%'"));

        for (CheckListItem checkListItem : checkListItems) {

            //Is the current Checklist already in master list?
            boolean isAlreadyInList = false;

            //Get checklist by current ChecklistItem's Checklist id
            CheckList checkList =
                    (CheckList) getItemById(checkListItem.getChecklistId(), Item.Type.CHECKLIST);

            //Not the status we are checking for (i.e. not trashed when we want it to be)
            if (!checkList.getStatus().equals(Item.Status.valueOf(convertItemType(type)))) {
                continue;
            }

            for (Item item : items) {

                if (!(item instanceof CheckList)) {
                    continue;
                }

                //This Item is a Checklist and we already have it
                if (item.getId() == checkListItem.getChecklistId()) {
                    isAlreadyInList = true;
                }
            }

            //New Checklist discovered to be needed!
            if (!isAlreadyInList) {
                items.add(checkList);
            }
        }

        return items;
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
