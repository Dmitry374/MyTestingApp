package com.example.dima.mytestingapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dima on 28.09.2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inf.db";
    public static final String TABLE_USER = "user";
    public static final String TABLE_GET = "table_get";
    public static final String TABLE_SPEND = "table_spend";
    public static final String TABLE_GET_FOR_TOTAL = "total_get";

    public static final String TABLE_STATISTIC = "table_statistic";

    public static final String TABLE_REMINDER = "reminders";

    //    Table user
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_SURNAME = "surname";
    public static final String KEY_PATRONYMIC = "patronymic"; // Отчество
    public static final String KEY_GENDER = "gender";

    public static final String KEY_DATE_OF_BIRTH = "date_of_birth";

    public static final String KEY_MOBILE = "mobile_phone";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_LOGIN = "login";
    public static final String KEY_PASSWORD = "password";

    public static final String KEY_SIGN_IN = "key_sign";
    public static final String KEY_SYNCHRONISE = "key_synchronise";

    //    Table Get
    public static final String KEY_GET_ID = "_id";
    public static final String KEY_GET_NAME = "get_name";
    public static final String KEY_GET_IMAGE = "get_image";
    public static final String KEY_KOL_GET = "kol_get";
    public static final String KEY_REF_LOGIN = "ref_login";
    public static final String KEY_GET_SYNCHRONISE = "key_get_synchronise";

    //    Table Spend
    public static final String KEY_SPEND_ID = "spend_id";
    public static final String KEY_SPEND_NAME = "spend_name";
    public static final String KEY_SPEND_IMAGE = "spend_image";
    public static final String KEY_KOL_SPEND = "kol_spend";
    public static final String KEY_REF_LOGIN_SPEND = "ref_login_spend";
    public static final String KEY_SPEND_SYNCHRONISE = "key_spend_synchronise";

    //    Table GetTotal
    public static final String KEY_GET_TOTAL_ID = "_id";
    public static final String KEY_KOL_TOTAL_GET = "kol_total_get";
    public static final String KEY_REF_LOGIN_TOTAL_GET = "ref_login_total_get";
    public static final String KEY_TOTAL_SYNCHRONISE = "key_total_synchronise";

    //    Table Statistic
    public static final String KEY_STATISTIC_ID = "_id";
    public static final String KEY_STATISTIC_TYPE = "type_statistic";
    public static final String KEY_STATISTIC_NAME_GET = "name_statistic_get";
    public static final String KEY_STATISTIC_KOL = "kol_statistic";
    public static final String KEY_STATISTIC_NAME_SPEND = "name_statistic_spend";
    public static final String KEY_STATISTIC_DATE = "date_statistic";
    public static final String KEY_STATISTIC_TIME = "time_statistic";
    public static final String KEY_STATISTIC_REF_LOGIN = "reflogin_statistic";
    public static final String KEY_STATISTIC_SYNCHRONISE = "key_statistic_synchronise";

    //    Table Reminders
    public static final String KEY_REMINDER_ID = "_id";
    public static final String KEY_REMINDER_TYPE = "reminder_type";
    public static final String KEY_REMINDER_NAME = "reminder_name";
    public static final String KEY_REMINDER_DATE = "reminder_date";
    public static final String KEY_REMINDER_TIME = "reminder_time";
    public static final String KEY_REMINDER_REPEAT = "reminder_repeat";
    public static final String KEY_REMINDER_IMG_MARKER = "reminder_img_marker";
    public static final String KEY_REMINDER_MARKER_NAME = "reminder_marker_name";
    public static final String KEY_REMINDER_SOUND = "reminder_sound";
    public static final String KEY_REMINDER_REF_LOGIN = "reminder_reflogin";
    public static final String KEY_REMINDER_SYNCHRONISE = "reminder_synchronise";



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_USER + "("
                + KEY_ID + " integer primary key autoincrement,"
                + KEY_NAME + " text not null, "
                + KEY_SURNAME + " text not null, "
                + KEY_PATRONYMIC + " text not null, "
                + KEY_GENDER + " text not null, "
                + KEY_DATE_OF_BIRTH + " text not null, "
                + KEY_MOBILE + " text not null, "
                + KEY_EMAIL + " text not null unique, "
                + KEY_LOGIN + " text not null unique, "
                + KEY_PASSWORD + " text not null, "
                + KEY_SIGN_IN + " integer, "
                + KEY_SYNCHRONISE + " text not null" + ")");

        db.execSQL("create table " + TABLE_GET + "("
                + KEY_GET_ID + " integer primary key autoincrement, "
                + KEY_GET_NAME + " text not null, "
                + KEY_GET_IMAGE + " integer not null, "
                + KEY_KOL_GET + " text, "
                + KEY_REF_LOGIN + " text,"
                + KEY_GET_SYNCHRONISE + " text not null,"
//                + KEY_GET_LOCAL_ID + " integer not null,"
                + " foreign key (" + KEY_REF_LOGIN + ") references " + TABLE_USER + "(" + KEY_LOGIN + "))");

        db.execSQL("create table " + TABLE_SPEND + "("
                + KEY_SPEND_ID + " integer primary key autoincrement, "
                + KEY_SPEND_NAME + " text not null, "
                + KEY_SPEND_IMAGE + " integer not null, "
                + KEY_KOL_SPEND + " text, "
                + KEY_REF_LOGIN_SPEND + " text, "
                + KEY_SPEND_SYNCHRONISE + " text not null,"
//                + KEY_SPEND_LOCAL_ID + " integer not null,"
                + " foreign key (" + KEY_REF_LOGIN_SPEND + ") references " + TABLE_USER + "(" + KEY_LOGIN + "))");

        db.execSQL("create table " + TABLE_GET_FOR_TOTAL + "("
                + KEY_GET_TOTAL_ID + " integer primary key autoincrement, "
                + KEY_KOL_TOTAL_GET + " text, "
                + KEY_REF_LOGIN_TOTAL_GET + " text, "
                + KEY_TOTAL_SYNCHRONISE + " text not null,"
                + " foreign key (" + KEY_REF_LOGIN_TOTAL_GET + ") references " + TABLE_USER + "(" + KEY_LOGIN + "))");

        db.execSQL("create table " + TABLE_STATISTIC + "("
                + KEY_STATISTIC_ID + " integer primary key autoincrement, "
                + KEY_STATISTIC_TYPE + " text not null, "
//                + KEY_STATISTIC_IMAGE + " integer not null,"
                + KEY_STATISTIC_NAME_GET + " text not null, "
                + KEY_STATISTIC_KOL + " text not null, "
                + KEY_STATISTIC_NAME_SPEND + " text not null, "
                + KEY_STATISTIC_DATE + " text not null, "
                + KEY_STATISTIC_TIME + " text not null, "
                + KEY_STATISTIC_REF_LOGIN + " text, "
                + KEY_STATISTIC_SYNCHRONISE + " text not null, "
                + " foreign key (" + KEY_STATISTIC_REF_LOGIN + ") references " + TABLE_USER + "(" + KEY_LOGIN + "))");

        db.execSQL("create table " + TABLE_REMINDER + "("
                + KEY_REMINDER_ID + " integer primary key autoincrement, "
                + KEY_REMINDER_TYPE + " text not null, "
                + KEY_REMINDER_NAME + " text not null, "
                + KEY_REMINDER_DATE + " text not null, "
                + KEY_REMINDER_TIME + " text not null, "
                + KEY_REMINDER_IMG_MARKER + " integer not null, "
                + KEY_REMINDER_MARKER_NAME + " text not null, "
                + KEY_REMINDER_REPEAT + " text, "
                + KEY_REMINDER_SOUND + " text, "
                + KEY_REMINDER_REF_LOGIN + " text, "
                + KEY_REMINDER_SYNCHRONISE + " text not null, "
//                + KEY_REMINDER_LOCAL_ID + " integer not null, "
                + " foreign key (" + KEY_REMINDER_REF_LOGIN + ") references " + TABLE_USER + "(" + KEY_LOGIN + "))");
    }

    public void dropTables(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPEND);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GET_FOR_TOTAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATISTIC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDER);
        onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
