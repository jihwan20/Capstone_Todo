package com.bjh.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_db";  // 데이터베이스 이름
    private static final int DATABASE_VERSION = 1;           // 데이터베이스 버전
    public static final String TABLE_USERS = "users";       // 사용자 테이블 이름

    // 사용자 테이블 컬럼
    public static final String COLUMN_USER_NO = "user_no";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_PW = "user_pw";

    public UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 사용자 테이블 생성 SQL
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " TEXT UNIQUE, " +
                COLUMN_USER_PW + " TEXT)";
        db.execSQL(createUsersTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // 비밀번호 해시화 메서드 (SHA-256)
    private String hashPassword(String userPw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(userPw.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0'); // 0-padding
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // 해시 생성 실패 시 null 반환
        }
    }

    // 사용자 등록 메서드
    public void insertUser(UserDTO user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, user.getUserId());
        values.put(COLUMN_USER_PW, hashPassword(user.getUserPw())); // 비밀번호 해시화
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    // 사용자 로그인 체크 메서드
    public boolean checkUser(String userId, String userPw) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPw = hashPassword(userPw);
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_USER_PW + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userId, hashedPw});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    // 사용자 ID 중복 확인 메서드
    public boolean isUserExists(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userId});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    // 현재 로그인한 사용자의 외래키를 가져오는 메서드
    public int getLoggedInUserNo(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userNo = -1;
        String query = "SELECT " + COLUMN_USER_NO + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userId});

        if (cursor.moveToFirst()) {
            userNo = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return userNo;
    }
}
