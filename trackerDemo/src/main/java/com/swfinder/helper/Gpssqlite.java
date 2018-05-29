package com.swfinder.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Gpssqlite extends SQLiteOpenHelper {

	public Gpssqlite(Context context) {
		super(context, "mygpsa.db", null, 1);
		}


	public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE gpsa ( "+
					"sid  INT PRIMARY KEY,"+
					"name  VARCHAR NOT NULL,"+
					"site  VARCHAR NOT NULL,"+
					"time  VARCHAR NOT NULL,"+
					"address  VARCHAR NOT NULL,"+
					"jd VARCHAR)");
			db.execSQL("CREATE TABLE connect ( "+
					
					"address  VARCHAR NOT NULL,"+
					"site  VARCHAR NOT NULL,"+
					"time  VARCHAR NOT NULL,"+
					"jd VARCHAR)");
			
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int newversion, int oldversion) {
		
		if(newversion!=oldversion){
			db.execSQL("drop table styden");
			onCreate(db);
		}
	}

}
