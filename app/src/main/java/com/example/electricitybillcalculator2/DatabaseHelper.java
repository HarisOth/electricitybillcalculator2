package com.example.electricitybillcalculator2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ElectricityBill.db";
    private static final int DATABASE_VERSION = 2; // Changed to version 2
    private static final String TABLE_NAME = "bills";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_UNIT = "unit";
    private static final String COLUMN_TOTAL_CHARGES = "total_charges";
    private static final String COLUMN_REBATE = "rebate";
    private static final String COLUMN_FINAL_COST = "final_cost";
    private static final String COLUMN_CREATED_AT = "created_at";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MONTH + " TEXT,"
            + COLUMN_UNIT + " REAL,"
            + COLUMN_TOTAL_CHARGES + " REAL,"
            + COLUMN_REBATE + " REAL," // Changed from INTEGER to REAL
            + COLUMN_FINAL_COST + " REAL,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // If upgrading from version 1 to 2, we need to recreate the table
            // with REAL type for rebate column
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public long addBill(BillModel bill) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_MONTH, bill.getMonth());
        values.put(COLUMN_UNIT, bill.getUnit());
        values.put(COLUMN_TOTAL_CHARGES, bill.getTotalCharges());
        values.put(COLUMN_REBATE, bill.getRebate());
        values.put(COLUMN_FINAL_COST, bill.getFinalCost());

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public List<BillModel> getAllBills() {
        List<BillModel> billList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_CREATED_AT + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BillModel bill = new BillModel();
                bill.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                bill.setMonth(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONTH)));
                bill.setUnit(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_UNIT)));
                bill.setTotalCharges(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_CHARGES)));
                bill.setRebate(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_REBATE))); // Changed to getDouble
                bill.setFinalCost(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FINAL_COST)));

                billList.add(bill);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return billList;
    }

    public BillModel getBill(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        BillModel bill = null; // Changed to null initially

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_MONTH, COLUMN_UNIT, COLUMN_TOTAL_CHARGES, COLUMN_REBATE, COLUMN_FINAL_COST},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            bill = new BillModel(); // Initialize here
            bill.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            bill.setMonth(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONTH)));
            bill.setUnit(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_UNIT)));
            bill.setTotalCharges(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_CHARGES)));
            bill.setRebate(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_REBATE))); // Changed to getDouble
            bill.setFinalCost(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FINAL_COST)));
            cursor.close();
        }

        db.close();
        return bill;
    }

    public void deleteAllBills() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    // ADD THESE NEW METHODS

    public boolean updateBill(BillModel bill) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_MONTH, bill.getMonth());
        values.put(COLUMN_UNIT, bill.getUnit());
        values.put(COLUMN_TOTAL_CHARGES, bill.getTotalCharges());
        values.put(COLUMN_REBATE, bill.getRebate());
        values.put(COLUMN_FINAL_COST, bill.getFinalCost());

        int rowsAffected = db.update(TABLE_NAME,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(bill.getId())});

        db.close();
        return rowsAffected > 0;
    }

    public boolean deleteBill(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_NAME,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();
        return rowsAffected > 0;
    }
}