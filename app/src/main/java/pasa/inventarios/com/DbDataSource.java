package pasa.inventarios.com;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Abraham on 29/06/2016.
 */
public class DbDataSource {

    private HelperInventarios openHelper;
    private SQLiteDatabase database;

    public DbDataSource(Context context) {
        openHelper = new HelperInventarios(context);
        database = openHelper.getWritableDatabase();
    }
    public Cursor getUser(){
        return database.rawQuery(
                "select * from " + HelperInventarios.Tablas.TBL_LOGIN_USER, null);
    }
    public Cursor getCatAlmacenes(){
        return database.rawQuery(
                "select * from " + HelperInventarios.Tablas.TBL_CATALOGO_ALMACENES, null);
    }
    public Cursor getCatAlmacenesFolio(String str_folio_cerrado){
        return database.rawQuery(
                "select vch_folio_inventario_diario from " + HelperInventarios.Tablas.TBL_CATALOGO_ALMACENES + " where vch_folio_inventario_diario != 'str_folio_cerrado'", null);
    }
    public Cursor getCatAlmacenes3(){
        return database.rawQuery(
                "select cerrarInventario from " + HelperInventarios.Tablas.TBL_CATALOGO_ALMACENES, null);
    }
    public Cursor getCatTipEquipo(){
        return database.rawQuery(
                "select * from " + HelperInventarios.Tablas.TBL_CATALOGO_TIPO_EQUIPO, null);
    }
    public Cursor getAltaEquipos(){
        return database.rawQuery(
                "select * from " + HelperInventarios.Tablas.INVENTARIO, null);
    }
}
