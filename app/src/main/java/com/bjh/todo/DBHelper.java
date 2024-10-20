    package com.bjh.todo;

    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;

    import java.security.MessageDigest;
    import java.security.NoSuchAlgorithmException;

    public class DBHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "users_db";
        private static final int DATABASE_VERSION = 1;
        private static final String TABLE_USERS = "users";

        // 사용자 테이블 컬럼
        private static final String COLUMN_USER_NO = "user_no";
        private static final String COLUMN_USER_ID = "user_id";
        private static final String COLUMN_USER_PW = "user_pw";

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                    + COLUMN_USER_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_USER_ID + " TEXT, "
                    + COLUMN_USER_PW + " TEXT)";
            db.execSQL(createUsersTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }

        // 비밀번호 해시 함수 (SHA-256)
        private String hsahPassword(String userPw) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(userPw.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if(hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
        // 사용자 등록 기능
        public void insertUser(String userId, String userPw) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_USER_PW, hsahPassword(userPw));  // 비밀번호 해시화
            db.insert(TABLE_USERS, null, values);
            db.close();
        }
        // 로그인 기능 구현
        public boolean checkUser(String userId, String userPw) {
            SQLiteDatabase db = this.getReadableDatabase();
            String hashedPw = hsahPassword(userPw);  // 입력받은 비밀번호 해시화
            String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_USER_PW + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{userId, hashedPw});
            boolean exists = (cursor.getCount() > 0);
            cursor.close();
            db.close();
            return exists;
        }

        // 아이디 중복 확인 메소드
        public boolean isUserExists(String userId) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_USERS + " WHERE user_id = ?";
            Cursor cursor = db.rawQuery(query, new String[]{userId});
            boolean exists = (cursor.getCount() > 0);
            cursor.close();
            db.close();
            return exists;
        }
    }