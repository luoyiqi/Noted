package com.cerebellio.noted.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Commonly used File functions
 */
public class FileFunctions {

    private static final String LOG_TAG = TextFunctions.makeLogTag(FileFunctions.class);

    private FileFunctions(){}

    /**
     * Retrieve a bitmap from a given file
     *
     * @param path          path to bitmap file
     * @return              bitmap found or null if error
     */
    public static Bitmap getBitmapFromFile(String path) {

        Bitmap bitmap = null;

        try {
            File f = new File(path);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * Save a given Sketch bitmap to file storage
     *
     * @param sketch            bitmap to save
     * @param dirName           directory name in which to save file
     * @param context           calling Context
     * @return                  saved file path
     * @throws IOException      if file cannot be opened for writing
     */
    public static String saveSketchToStorage(Bitmap sketch, String dirName, Context context) throws IOException {

        //save file with current date and time for uniqueness
        String  fileName = DateFormat.format("MM-dd-yy HH-mm-ss", new Date().getTime()).toString();

        ContextWrapper contextWrapper = new ContextWrapper(context.getApplicationContext());
        File directory = contextWrapper.getDir(dirName, Context.MODE_PRIVATE);

        File path = new File(directory, fileName);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(path);
            sketch.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            Log.e(LOG_TAG, "File could not be opened");
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Sketch not initialised");
         }finally {
            if (fos != null) {
                fos.close();
            }
        }

        return path.getAbsolutePath();
    }

    /**
     * Deletes a file at the given path
     * @param path      path of the file to delete
     * @return          true if file deleted, false otherwise
     */
    public static boolean deleteSketchFromStorage(String path) {
        return new File(path).delete();
    }

    /**
     * Read in a text file from the assets directory and convert to a String
     *
     * @param context
     * @param fileName          file name of the text file to read
     * @return                  created String
     */
    public static String readTextFileFromAssets(Context context, String fileName) {
        String text = "";

        try {
            InputStream inputStream = context.getResources().getAssets().open(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;

            try {
                while ((line = bufferedReader.readLine()) != null)
                    text += line;
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return text;
    }

}
