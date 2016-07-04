package pasa.inventarios.com;

import android.database.Cursor;

/**
 * Created by Abraham on 27/06/2016.
 */
public class UConsultas {
    public static String obtenerString(Cursor cursor, String columna) {
        return cursor.getString(cursor.getColumnIndex(columna));
    }

    public static int obtenerInt(Cursor cursor, String columna) {
        return cursor.getInt(cursor.getColumnIndex(columna));
    }

    public static float obtenerFloat(Cursor cursor, String columna) {
        return cursor.getFloat(cursor.getColumnIndex(columna));
    }
}
