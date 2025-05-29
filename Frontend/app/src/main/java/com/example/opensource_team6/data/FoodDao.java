package com.example.opensource_team6.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class FoodDao {
    private final SQLiteDatabase db;

    public FoodDao(Context context) {
        NutritionDBHelper dbHelper = new NutritionDBHelper(context);
        this.db = dbHelper.openDatabase();
    }

    public List<String> getAllFoodNames() {
        List<String> names = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT name FROM nutrition", null);
        while (cursor.moveToNext()) {
            names.add(cursor.getString(0));
        }
        cursor.close();
        return names;
    }

    @SuppressLint("Range")
    public List<Food> searchFoodByName(String keyword) {
        List<Food> results = new ArrayList<>();

        String sql = "SELECT * FROM nutrition WHERE name LIKE ?";
        Cursor cursor = db.rawQuery(sql, new String[]{"%" + keyword + "%"});

        while (cursor.moveToNext()) {
            Food food = new Food();
            food.setName(cursor.getString(cursor.getColumnIndex("name")));
            food.setCategory(cursor.getString(cursor.getColumnIndex("category")));
            food.setRep_name(cursor.getString(cursor.getColumnIndex("rep_name")));
            food.setSubcategory(cursor.getString(cursor.getColumnIndex("subcategory")));
            food.setStandard_amount(cursor.getDouble(cursor.getColumnIndex("standard_amount")));
            food.setEnergy(cursor.getDouble(cursor.getColumnIndex("energy")));
            food.setProtein(cursor.getDouble(cursor.getColumnIndex("protein")));
            food.setFat(cursor.getDouble(cursor.getColumnIndex("fat")));
            food.setCarbohydrate(cursor.getDouble(cursor.getColumnIndex("carbohydrate")));
            food.setSugar(cursor.getDouble(cursor.getColumnIndex("sugar")));
            food.setSodium(cursor.getDouble(cursor.getColumnIndex("sodium")));
            food.setCholesterol(cursor.getDouble(cursor.getColumnIndex("cholesterol")));
            food.setSaturated_fat(cursor.getDouble(cursor.getColumnIndex("saturated_fat")));

            // 기준량을 weight으로 설정
            food.setWeight(food.getStandard_amount());

            results.add(food);
        }

        cursor.close();
        return results;
    }
}
