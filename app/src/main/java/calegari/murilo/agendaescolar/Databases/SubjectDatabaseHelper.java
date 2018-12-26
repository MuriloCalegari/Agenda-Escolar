package calegari.murilo.agendaescolar.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;

public class SubjectDatabaseHelper extends SQLiteOpenHelper {

    public SubjectDatabaseHelper(@Nullable Context context) {
        super(context, SubjectEntry.DATABASE_NAME, null, SubjectEntry.DATABASE_VERSION);
    }


    public static class SubjectEntry implements BaseColumns {
        public static final String DATABASE_NAME = "schooltools.db";
        public static final String TABLE_NAME = "subjects";
        public static final String COLUMN_SUBJECT_NAME = "name";
        public static final String COLUMN_SUBJECT_ABBREVIATION = "abbreviation";
        public static final String COLUMN_SUBJECT_PROFESSOR = "professor";
        public static final Integer DATABASE_VERSION = 1;
        private static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " +
                SubjectEntry.TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                SubjectEntry.COLUMN_SUBJECT_NAME + " TEXT," +
                SubjectEntry.COLUMN_SUBJECT_ABBREVIATION + " TEXT," +
                SubjectEntry.COLUMN_SUBJECT_PROFESSOR + " TEXT)";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + SubjectEntry.TABLE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SubjectEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SubjectEntry.SQL_DELETE_ENTRIES);
    }

    public boolean insertData(String Name, String Abbreviation, String Professor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_NAME, Name);
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_ABBREVIATION, Abbreviation);
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_PROFESSOR, Professor);
        long result = db.insert(SubjectEntry.TABLE_NAME, null, contentValues);

        if(result == -1) {
            Log.d("SubjectDatabaseHelper", "Some error happened when inserting data in database");
            return false;
        } else {
            Log.d("SubjectDatabaseHelper", "Created entry in database successfully");
            return true;
        }
    }
}
