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
    public static final String COLUMN_USER_ID = "user_id"; // 사용자 ID (기본 키)
    public static final String COLUMN_USER_PW = "user_pw"; // 사용자 비밀번호

    public UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 사용자 테이블 생성 SQL
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " + // 기본 키로 설정
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

    public UserDTO getUserById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USER_ID + "=?", new String[]{userId}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            UserDTO user = new UserDTO();
            user.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setUserPw(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PW)));
            cursor.close();
            db.close();
            return user;
        }
        cursor.close();
        db.close();
        return null; // 사용자가 존재하지 않으면 null 반환
    }

    // 비밀번호 업데이트 메소드
    public void updateUserPassword(UserDTO user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_PW, user.getUserPw()); // 새로운 비밀번호 설정

        // ID에 해당하는 사용자 비밀번호 업데이트
        db.update(TABLE_USERS, values, COLUMN_USER_ID + "=?", new String[]{user.getUserId()});
        db.close(); // 데이터베이스 닫기
    }

    public void deleteUser(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COLUMN_USER_ID + "=?", new String[]{userId});
        db.close(); // 데이터베이스 닫기
    }
}
