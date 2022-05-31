package com.mitpoly.quizcomp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists questions (id int, question text, optionOne text, optionTwo text, optionThree text, optionFour text, correctOption text)");
	}

	public Cursor getQuestion(int n) {
		SQLiteDatabase db = getReadableDatabase();
		String[] id = { String.valueOf(n) };
		Cursor c = db.rawQuery("select * from questions where id = ?", id );
		return c;
	}
	
	public void clearQuestions() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete("questions", null, null);
	}
	
	public void addQuestions(int cnt, String question, String optionOne, String optionTwo, String optionThree, String optionFour, String correctOption) {
		
		SQLiteDatabase db = getWritableDatabase();
		
		ContentValues record = new ContentValues();
		record.put("id", cnt);
		record.put("question", question);
		record.put("OptionOne", optionOne);
		record.put("OptionTwo", optionTwo);
		record.put("OptionThree", optionThree);
		record.put("OptionFour", optionFour);
		record.put("correctOption", correctOption);
		db.insert("questions", null, record);
	}
	
	public int getnumberOfQuestions() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("select * from questions", null);
		return c.getCount();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

}