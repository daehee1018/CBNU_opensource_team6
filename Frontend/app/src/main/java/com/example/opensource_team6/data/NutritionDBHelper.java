package com.example.opensource_team6.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class NutritionDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "nutrition_data.db";
    private static final int DB_VERSION = 1;
    private final Context context;

    public NutritionDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    // 앱 첫 실행 시 assets에서 DB 복사
    @Override
    public void onCreate(SQLiteDatabase db) { }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    // DB가 없으면 assets에서 복사
    /*public void checkAndCopyDatabase() {
        File dbPath = context.getDatabasePath(DB_NAME);
        if (!dbPath.exists()) {
            dbPath.getParentFile().mkdirs();
            try (InputStream is = context.getAssets().open(DB_NAME);
                 OutputStream os = new FileOutputStream(dbPath)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
            } catch (IOException e) {
                throw new RuntimeException("DB 복사 실패", e);
            }
        }
    }*/
    // NutritionDBHelper.java
    public List<String> getAllCategories() {
        SQLiteDatabase db = openDatabase();
        List<String> categories = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT category FROM nutrition", null);
        while (cursor.moveToNext()) {
            categories.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return categories;
    }

    public List<String> getSubcategoriesByCategory(String category) {
        SQLiteDatabase db = openDatabase();
        List<String> subcategories = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT subcategory FROM nutrition WHERE category = ?",
                new String[]{category}
        );
        while (cursor.moveToNext()) {
            subcategories.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return subcategories;
    }

    private void copyDatabase() {
        File dbPath = context.getDatabasePath(DB_NAME);

        // ✅ 항상 삭제 후 복사
        if (dbPath.exists()) {
            dbPath.delete();
        }

        try (InputStream inputStream = context.getAssets().open(DB_NAME);
             OutputStream outputStream = new FileOutputStream(dbPath)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            Log.d("DBHelper", "DB 복사 완료: " + dbPath.getAbsolutePath());

        } catch (IOException e) {
            Log.e("DBHelper", "DB 복사 실패: " + e.getMessage());
        }
    }


    public SQLiteDatabase openDatabase() {
        copyDatabase();
        return SQLiteDatabase.openDatabase(
                context.getDatabasePath(DB_NAME).getPath(),
                null,
                SQLiteDatabase.OPEN_READONLY
        );
    }
}
