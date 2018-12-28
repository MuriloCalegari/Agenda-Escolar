package calegari.murilo.agendaescolar.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;
import calegari.murilo.agendaescolar.subjecthelper.Subject;

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
        // This naming is very bad, sorry english speakers
        public static final String COLUMN_SUBJECT_OBTAINED_GRADE = "obtainedgrade";
        public static final String COLUMN_SUBJECT_MAXIMUM_GRADE = "maximumgrade";
        public static final Integer DATABASE_VERSION = 1;
        private static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " +
                SubjectEntry.TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                SubjectEntry.COLUMN_SUBJECT_NAME + " TEXT," +
                SubjectEntry.COLUMN_SUBJECT_ABBREVIATION + " TEXT," +
                SubjectEntry.COLUMN_SUBJECT_PROFESSOR + " TEXT," +
                SubjectEntry.COLUMN_SUBJECT_OBTAINED_GRADE + " TEXT," +
                SubjectEntry.COLUMN_SUBJECT_MAXIMUM_GRADE + " TEXT)";

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

    public void insertData(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_NAME, subject.getName());
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_ABBREVIATION, subject.getAbbreviation());
        contentValues.put(SubjectEntry.COLUMN_SUBJECT_PROFESSOR, subject.getProfessor());

        db.insert(SubjectEntry.TABLE_NAME, null, contentValues);
        db.close();
    }

    public void removeData(String Abbreviation) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + SubjectEntry.TABLE_NAME + " WHERE " + SubjectEntry.COLUMN_SUBJECT_ABBREVIATION + " = " + '"' + Abbreviation + '"';
        Log.d("SubjectDatabaseHelper", "Removing data from database with query: " + deleteQuery);

        db.delete(SubjectEntry.TABLE_NAME, SubjectEntry.COLUMN_SUBJECT_ABBREVIATION + "=?", new String[] {Abbreviation});
        db.close();
    }

    public void updateData(Subject subject) {
    	SQLiteDatabase db = this.getWritableDatabase();

    	ContentValues contentValues = new ContentValues();
	    contentValues.put(SubjectEntry.COLUMN_SUBJECT_NAME, subject.getName());
	    contentValues.put(SubjectEntry.COLUMN_SUBJECT_ABBREVIATION, subject.getAbbreviation());
	    contentValues.put(SubjectEntry.COLUMN_SUBJECT_PROFESSOR, subject.getProfessor());

	    db.update(SubjectEntry.TABLE_NAME, contentValues, SubjectEntry.COLUMN_SUBJECT_ABBREVIATION + "=?", new String[] {subject.getAbbreviation()});
	    db.close();
    }

    public Cursor getAllDataInAlphabeticalOrder() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + SubjectEntry.TABLE_NAME + " ORDER BY " + SubjectEntry.COLUMN_SUBJECT_NAME + " ASC", null);
        return cursor;
    }

    public Cursor getAllData() {
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery("SELECT * FROM " + SubjectEntry.TABLE_NAME + "", null);
	    return cursor;
    }

    public boolean hasObject(String columnName, String entry) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(SubjectEntry.TABLE_NAME, new String[] {columnName}, columnName  + "=?", new String[] {entry}, null, null, null);

        if(cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return true;
        } else {
            cursor.close();
            db.close();
            return false;
        }
    }

}
