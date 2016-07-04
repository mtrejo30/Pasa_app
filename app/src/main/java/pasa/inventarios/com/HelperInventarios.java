package pasa.inventarios.com;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import pasa.inventarios.com.Contrato.*;

/**
 * Created by Abraham on 27/06/2016.
 */
public class HelperInventarios extends SQLiteOpenHelper {

    static final int VERSION = 1;
    static final String NOMBRE_BD = "pasa_app.db";
    interface Tablas {
        String TBL_LOGIN_USER = "tbl_LoginUser";
        String TBL_CATALOGO_ALMACENES = "tbl_CatalogoAlmacenes";
        String TBL_CATALOGO_TIPO_EQUIPO = "tbl_CatalogoTipoEquipo";
        String INVENTARIO = "inventario";
    }
    public HelperInventarios(Context context) {
        super(context, NOMBRE_BD, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " + Tablas.TBL_LOGIN_USER + "("
                        + cls_Columnas_Login_User.ID_INT_BRANCHID + " INTEGER PRIMARY KEY NOT NULL,"
                        + cls_Columnas_Login_User.INT_USER + " VARCHAR(30) NOT NULL,"
                        + cls_Columnas_Login_User.STR_PASS + " VARCHAR(30) NULL,"
                        + cls_Columnas_Login_User.STR_APP + " INTEGER NOT NULL,"
                        + cls_Columnas_Login_User.STR_NAME + " VARCHAR(30) NOT NULL,"
                        + cls_Columnas_Login_User.INT_VALIDA + " INTEGER NOT NULL)");

        db.execSQL(
                "CREATE TABLE " + Tablas.TBL_CATALOGO_ALMACENES + "("
                        + cls_Columnas_Catalogo_Almacenes.ID_INT_EQUIPO_ALMACEN_ID + " INTEGER PRIMARY KEY NOT NULL,"
                        + cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_CLAVE + " VARCHAR(30) NOT NULL,"
                        + cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_DESCRIPCION + " VARCHAR(30) NOT NULL,"
                        + cls_Columnas_Catalogo_Almacenes.FK_INT_BRANCHID + " INTEGER NOT NULL,"
                        + " FOREIGN KEY (" + cls_Columnas_Catalogo_Almacenes.FK_INT_BRANCHID + ") REFERENCES " + Tablas.TBL_LOGIN_USER + "(" + cls_Columnas_Login_User.ID_INT_BRANCHID + "))");

        db.execSQL(
                "CREATE TABLE " + Tablas.TBL_CATALOGO_TIPO_EQUIPO + "("
                        + cls_Columnas_Catalogo_Tipo_Equipo.ID_INT_TIPO_EQUIPO_ID + " INTEGER PRIMARY KEY NOT NULL,"
                        + cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_CLAVE + " VARCHAR(30) NOT NULL,"
                        + cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_DESCRIPCION + " VARCHAR(30) NOT NULL,"
                        + cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_CAPACIDAD + " VARCHAR(30) NOT NULL,"
                        + cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_UNIDAD_MEDIDA + " VARCHAR(30) NOT NULL,"
                        + cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_MOVIMIENTO + " VARCHAR(30) NOT NULL)");
        db.execSQL(
                "CREATE TABLE " + Tablas.INVENTARIO + "("
                        + Inventarios.ID_PASA + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + Inventarios.EQUIPO_FOLIO + " VARCHAR(30) NOT NULL,"
                        + Inventarios.EQUIPO_RFID + " INTEGER NULL,"
                        + Inventarios.FK_TIPO_EQUIPO_ID + " INTEGER NOT NULL,"
                        + Inventarios.FK_EQUIPO_ALMACEN_ID + " INTEGER NOT NULL,"
                        + Inventarios.EQUIPO_ESTATUS_ID + " INTEGER NOT NULL,"
                        + Inventarios.EQUIPO_PROPIO + " INTEGER NOT NULL,"
                        + Inventarios.FK_BRANCH_ID + " INTEGER NOT NULL,"
                        + Inventarios.EQUIPO_ALMACEN_STR + " INTEGER NOT NULL,"
                        + Inventarios.TIPO_EQUIPO_STR + " INTEGER NOT NULL,"
                        + Inventarios.INSERTADO + " INTEGER DEFAULT 1,"
                        + Inventarios.MODIFICADO + " INTEGER DEFAULT 0,"
                        + Inventarios.ELIMINADO + " INTEGER DEFAULT 0,"
                        + " FOREIGN KEY (" + Inventarios.FK_TIPO_EQUIPO_ID + ") REFERENCES " + Tablas.TBL_CATALOGO_TIPO_EQUIPO + "(" + cls_Columnas_Catalogo_Tipo_Equipo.ID_INT_TIPO_EQUIPO_ID + "),"
                        + " FOREIGN KEY (" + Inventarios.FK_EQUIPO_ALMACEN_ID + ") REFERENCES " + Tablas.TBL_LOGIN_USER + "(" + cls_Columnas_Catalogo_Almacenes.ID_INT_EQUIPO_ALMACEN_ID + "),"
                        + " FOREIGN KEY (" + Inventarios.FK_BRANCH_ID + ") REFERENCES " + Tablas.TBL_LOGIN_USER + "(" + cls_Columnas_Login_User.ID_INT_BRANCHID + "))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + Tablas.TBL_LOGIN_USER);
            db.execSQL("DROP TABLE IF EXISTS " + Tablas.TBL_CATALOGO_TIPO_EQUIPO);
            db.execSQL("DROP TABLE IF EXISTS " + Tablas.TBL_CATALOGO_ALMACENES);
            db.execSQL("DROP TABLE IF EXISTS " + Tablas.INVENTARIO);
        } catch (SQLiteException e) {
            // Manejo de excepciones
        }
        onCreate(db);
    }
}