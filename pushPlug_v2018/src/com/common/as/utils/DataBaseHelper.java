package com.common.as.utils;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLite的工具类
 * 
 * @author Administrator
 * 
 */
public class DataBaseHelper extends SQLiteOpenHelper {
	Context context;
	public static final String DB_NAME = "DD.db";
	public static final int DB_VERSION = 1;
	// /////////////////////////
	public static final String SIGNED_TBL = "favourites";
	public static final String ID_SIGNED = "_ID";
	public static final String SIGNED_DATE = "_DATE";
	ContentValues values = new ContentValues();

	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql_saveString = "create table if not exists " + SIGNED_TBL
				+ " (" + ID_SIGNED + " integer auto_increment primary key,"
				+ SIGNED_DATE + " nvarchar(50))";
		db.execSQL(sql_saveString);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void insert_signed(String s) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(SIGNED_TBL, null, SIGNED_DATE + "=?",
				new String[] { s }, null, null, null);
		if (!cursor.moveToNext()) {
			values.clear();
			values.put(SIGNED_DATE, s);
			db.insert(SIGNED_TBL, null, values);
		}
		cursor.close();
		db.close();
	}

	// 查询
	public boolean queryIsExist(String date) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(SIGNED_TBL, null, SIGNED_DATE + "=?",
				new String[] { date }, null, null, null);
		if (cursor.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
