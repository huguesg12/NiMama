package com.dico.dicochinois;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	public static final String DICO_KEY = "_id";
	public static final String DICO_IDCC = "idCc";
	public static final String DICO_IDCAR = "idCar";
	public static final String DICO_IDDICO = "idDico";
	
	public static final String DICO_CARACTERE = "caractere";
	public static final String DICO_PRON = "prononciation";
	public static final String DICO_TRAD = "trad";
	public static final String DICO_FREQUENCE = "frequence";
	public static final String DICO_TEMP= "temp";
	public static final String DICO_SCORE= "score";
	public static final String DICO_NBI= "nbInterrogations";
	public static final String DICO_POIDS= "poids";

	public static final String DICO_TABLE_NAME = "dico";
	
	public static final String DICO_TABLE_CREATE =
			"CREATE TABLE " + DICO_TABLE_NAME + " (" +	DICO_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					DICO_IDCC + " INTEGER, " + DICO_IDCAR + " INTEGER, " + DICO_IDDICO + " INTEGER, " +
					DICO_CARACTERE + " TEXT, " + DICO_PRON + " TEXT, " + DICO_TRAD + " TEXT, " + 
					DICO_FREQUENCE + " INTEGER, " + DICO_TEMP+ " INTEGER, " + DICO_SCORE + " REAL, " +
					DICO_NBI + " INTEGER, " + DICO_POIDS + " REAL);";

	public static final String DICO_TABLE_DROP = "DROP TABLE IF EXISTS " + DICO_TABLE_NAME + ";";
	
	public DatabaseHandler(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DICO_TABLE_CREATE);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("UPGRADE");
		db.execSQL(DICO_TABLE_DROP);
		onCreate(db);
	}
	
}
