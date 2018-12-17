package com.spresto.righttobeforgotten.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.spresto.righttobeforgotten.model.SQLiteModel;

import java.util.ArrayList;

/**
 * Created by spresto on 2018-09-18.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBHelper.class.getSimpleName();
    private Context context;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        this.context = context;
    }

    /**
     * DB가 존재하지 않을 때, 한 번 실행됨
     * DB 생성 역활
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" CREATE TABLE TEST_TABLE ( ");
        stringBuffer.append(" _ID INTEGER PRIMARY KEY AUTOINCREMENT, ");
        stringBuffer.append(" PERCENT TEXT, ");
        stringBuffer.append(" TITLE TEXT, ");
        stringBuffer.append(" SITE TEXT, ");
        stringBuffer.append(" RANK TEXT ) ");


        //SQLite DB로 쿼리 실행
        db.execSQL(stringBuffer.toString());

        Log.e(TAG, "Create db table!");
    }

    /**
     * APP의 버전이 올라가서 Table 구조가 변경되었을 때 실행된다.
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "application version upgrade");
    }

    public void testDB(){
        SQLiteDatabase db = getReadableDatabase();
    }

    public void addModel(SQLiteModel model){
        // 1. 사용할 수 있는 DB 객체를 get
        SQLiteDatabase db = getWritableDatabase();

        // 2. model insert
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO TEST_TABLE ( ");
        sb.append("PERCENT, TITLE, SITE, RANK ) ");
        sb.append(" VALUES (?,?,?,?) ");

        db.execSQL(sb.toString(),
                new Object[]{
                model.getProb(),model.getTitle(),model.getSite(),model.getRanking()
                });

    }

    public ArrayList<SQLiteModel> getAllModel(){
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT _ID, PERCENT, TITLE, SITE, RANK FROM TEST_TABLE");

        // 읽기 전용 DB 객체
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(sb.toString(), null);

        ArrayList<SQLiteModel> data = new ArrayList();
        SQLiteModel model = null;

        // moveToNext 다음에 데이터가 있으며 true 없으면 false
        while(cursor.moveToNext()){
            model = new SQLiteModel(
                    cursor.getString(1),
            cursor.getString(2),
            cursor.getString(3),
            cursor.getString(4)
            );
            model.setId(cursor.getString(0));
            data.add(model);
        }
        return data;
    }

}
