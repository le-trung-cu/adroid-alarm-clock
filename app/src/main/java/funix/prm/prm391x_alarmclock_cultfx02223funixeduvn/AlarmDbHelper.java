package funix.prm.prm391x_alarmclock_cultfx02223funixeduvn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class AlarmDbHelper extends SQLiteOpenHelper {

    private static AlarmDbHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "alarmDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_ALARM = "alarms";

    // Alarm Table Columns
    private static final String KEY_ALARM_ID = "id";
    private static final String KEY_ALARM_TIME_HOUR= "hour";
    private static final String KEY_ALARM_TIME_MINUTE= "minute";
    private static final String KEY_ALARM_ON_OFF = "onOff";


    private AlarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized AlarmDbHelper getInstance(Context context){
        if(sInstance == null){
            sInstance = new AlarmDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ALARM_TABLE =String.format(
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER )",
                TABLE_ALARM, KEY_ALARM_ID, KEY_ALARM_TIME_HOUR, KEY_ALARM_TIME_MINUTE, KEY_ALARM_ON_OFF);
        db.execSQL(CREATE_ALARM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion){
            String DROP_ALARM_TABLE = String.format("DROP TABLE IF EXISTS %s", TABLE_ALARM);
            db.execSQL(DROP_ALARM_TABLE);
            onCreate(db);
        }
    }

    public AlarmModel getAlarm(int alarmId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALARM, null,
                KEY_ALARM_ID + "=?", new String[]{String.valueOf(alarmId)},
                null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            boolean alarmIsOn = cursor.getInt(3) == 1;
            AlarmModel alarm = new AlarmModel(cursor.getInt(0),
                    cursor.getInt(1), cursor.getInt(2), alarmIsOn);
            return alarm;
        }
        return null;
    }

    public ArrayList<AlarmModel> getAlarms(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALARM, null, null, null,
                null, null, KEY_ALARM_TIME_HOUR +", " + KEY_ALARM_TIME_MINUTE);

        ArrayList<AlarmModel> alarms = new ArrayList<AlarmModel>();
        if(cursor != null){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                boolean alarmIsOn = cursor.getInt(3) == 1;
                AlarmModel alarm = new AlarmModel(cursor.getInt(0),
                        cursor.getInt(1), cursor.getInt(2), alarmIsOn);

                alarms.add(alarm);
                cursor.moveToNext();
            }
        }

        db.close();

        return alarms;
    }

    public int addAlarm(AlarmModel alarm){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ALARM_TIME_HOUR, alarm.getHour());
        values.put(KEY_ALARM_TIME_MINUTE, alarm.getMinute());
        values.put(KEY_ALARM_ON_OFF, alarm.isOn());

        int n =(int) db.insertOrThrow(TABLE_ALARM, null, values);

        db.close();
        return n;
    }

    public int deleteAlarm(int alarmId){
        SQLiteDatabase db = getWritableDatabase();
        int n = db.delete(TABLE_ALARM, KEY_ALARM_ID+"=?", new String[]{String.valueOf(alarmId)});
        db.close();
        return n;
    }

    public int updateAlarm(AlarmModel alarm){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ALARM_TIME_HOUR, alarm.getHour());
        values.put(KEY_ALARM_TIME_MINUTE, alarm.getMinute());
        values.put(KEY_ALARM_ON_OFF, alarm.isOn());

        int n = db.update(TABLE_ALARM, values,
                KEY_ALARM_ID+"=?",
                new String[]{String.valueOf(alarm.getId())});
        db.close();
        return n;
    }
}























