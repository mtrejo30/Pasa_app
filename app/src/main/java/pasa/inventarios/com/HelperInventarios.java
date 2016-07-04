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
                        + cls_Columnas_Catalogo_Almacenes.ID_INT_EQUIPO_ALMACEN_ID + " INTEGER PRIMARY KEY,"
                        + cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_CLAVE + " VARCHAR(30) NOT NULL,"
                        + cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_DESCRIPCION + " VARCHAR(30) NOT NULL,"
                        + cls_Columnas_Catalogo_Almacenes.FK_INT_BRANCHID + " INTEGER NOT NULL,"
                        + " FOREIGN KEY (" + cls_Columnas_Catalogo_Almacenes.FK_INT_BRANCHID + ") REFERENCES " + Tablas.TBL_LOGIN_USER + "(" + cls_Columnas_Login_User.ID_INT_BRANCHID + "))");

        db.execSQL(
                "CREATE TABLE " + Tablas.TBL_CATALOGO_TIPO_EQUIPO + "("
                        + cls_Columnas_Catalogo_Tipo_Equipo.ID_INT_TIPO_EQUIPO_ID + " INTEGER PRIMARY KEY,"
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
                        + Inventarios.INSERTADO + " INTEGER DEFAULT 1,"
                        + Inventarios.MODIFICADO + " INTEGER DEFAULT 0,"
                        + Inventarios.ELIMINADO + " INTEGER DEFAULT 0,"
                        + " FOREIGN KEY (" + Inventarios.FK_TIPO_EQUIPO_ID + ") REFERENCES " + Tablas.TBL_CATALOGO_TIPO_EQUIPO + "(" + cls_Columnas_Catalogo_Tipo_Equipo.ID_INT_TIPO_EQUIPO_ID + "),"
                        + " FOREIGN KEY (" + Inventarios.FK_EQUIPO_ALMACEN_ID + ") REFERENCES " + Tablas.TBL_LOGIN_USER + "(" + cls_Columnas_Catalogo_Almacenes.ID_INT_EQUIPO_ALMACEN_ID + "),"
                        + " FOREIGN KEY (" + Inventarios.FK_BRANCH_ID + ") REFERENCES " + Tablas.TBL_LOGIN_USER + "(" + cls_Columnas_Login_User.ID_INT_BRANCHID + "))");

        ContentValues valores = new ContentValues();

        valores.clear();
        valores.put(cls_Columnas_Login_User.ID_INT_BRANCHID, 1);
        valores.put(cls_Columnas_Login_User.INT_USER, "INT_USER1");
        valores.put(cls_Columnas_Login_User.STR_PASS, "STR_PASS");
        valores.put(cls_Columnas_Login_User.STR_APP, 1);
        valores.put(cls_Columnas_Login_User.STR_NAME, "");
        valores.put(cls_Columnas_Login_User.INT_VALIDA, 1);
        db.insertOrThrow(Tablas.TBL_LOGIN_USER, null, valores);

        valores.clear();
        valores.put(cls_Columnas_Login_User.ID_INT_BRANCHID, 2);
        valores.put(cls_Columnas_Login_User.INT_USER, "INT_USER1");
        valores.put(cls_Columnas_Login_User.STR_PASS, "STR_PASS");
        valores.put(cls_Columnas_Login_User.STR_APP, 1);
        valores.put(cls_Columnas_Login_User.STR_NAME, "STR_APP");
        valores.put(cls_Columnas_Login_User.INT_VALIDA, 1);
        db.insertOrThrow(Tablas.TBL_LOGIN_USER, null, valores);

        valores.clear();
        valores.put(cls_Columnas_Login_User.ID_INT_BRANCHID, 3);
        valores.put(cls_Columnas_Login_User.INT_USER, "INT_USER2");
        valores.put(cls_Columnas_Login_User.STR_PASS, "STR_PASS2");
        valores.put(cls_Columnas_Login_User.STR_APP, 1);
        valores.put(cls_Columnas_Login_User.STR_NAME, "STR_APP2");
        valores.put(cls_Columnas_Login_User.INT_VALIDA, 1);
        db.insertOrThrow(Tablas.TBL_LOGIN_USER, null, valores);

        valores.clear();
        valores.put(cls_Columnas_Login_User.ID_INT_BRANCHID, 4);
        valores.put(cls_Columnas_Login_User.INT_USER, "INT_USER3");
        valores.put(cls_Columnas_Login_User.STR_PASS, "STR_PASS3");
        valores.put(cls_Columnas_Login_User.STR_APP, 1);
        valores.put(cls_Columnas_Login_User.STR_NAME, "STR_APP3");
        valores.put(cls_Columnas_Login_User.INT_VALIDA, 1);
        db.insertOrThrow(Tablas.TBL_LOGIN_USER, null, valores);

        valores.clear();
        valores.put(cls_Columnas_Catalogo_Almacenes.ID_INT_EQUIPO_ALMACEN_ID, 1);
        valores.put(cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_CLAVE, "ID_INT_EQUIPO_ALMACEN_ID1");
        valores.put(cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_DESCRIPCION, "");
        valores.put(cls_Columnas_Catalogo_Almacenes.FK_INT_BRANCHID, 1);
        db.insertOrThrow(Tablas.TBL_CATALOGO_ALMACENES, null, valores);

        valores.clear();
        valores.put(cls_Columnas_Catalogo_Almacenes.ID_INT_EQUIPO_ALMACEN_ID, 2);
        valores.put(cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_CLAVE, "ID_INT_EQUIPO_ALMACEN_ID2");
        valores.put(cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_DESCRIPCION, "STR_EQUIPO_ALMACEN_DESCRIPCION2");
        valores.put(cls_Columnas_Catalogo_Almacenes.FK_INT_BRANCHID, 2);
        db.insertOrThrow(Tablas.TBL_CATALOGO_ALMACENES, null, valores);


        valores.clear();
        valores.put(cls_Columnas_Catalogo_Almacenes.ID_INT_EQUIPO_ALMACEN_ID, 3);
        valores.put(cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_CLAVE, "ID_INT_EQUIPO_ALMACEN_ID3");
        valores.put(cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_DESCRIPCION, "STR_EQUIPO_ALMACEN_DESCRIPCION3");
        valores.put(cls_Columnas_Catalogo_Almacenes.FK_INT_BRANCHID, 3);
        db.insertOrThrow(Tablas.TBL_CATALOGO_ALMACENES, null, valores);

        valores.clear();
        valores.put(cls_Columnas_Catalogo_Almacenes.ID_INT_EQUIPO_ALMACEN_ID, 4);
        valores.put(cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_CLAVE, "ID_INT_EQUIPO_ALMACEN_ID1");
        valores.put(cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_DESCRIPCION, "");
        valores.put(cls_Columnas_Catalogo_Almacenes.FK_INT_BRANCHID, 1);
        db.insertOrThrow(Tablas.TBL_CATALOGO_ALMACENES, null, valores);



        valores.clear();
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.ID_INT_TIPO_EQUIPO_ID, 1);
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_CLAVE, "STR_TIPO_EQUIPO_CLAVE1");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_DESCRIPCION, "");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_CAPACIDAD, "STR_TIPO_EQUIPO_CAPACIDAD1");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_UNIDAD_MEDIDA, "STR_TIPO_EQUIPO_UNIDAD_MEDIDA1");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_MOVIMIENTO, "STR_TIPO_EQUIPO_MOVIMIENTO1");
        db.insertOrThrow(Tablas.TBL_CATALOGO_TIPO_EQUIPO, null, valores);

        valores.clear();
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.ID_INT_TIPO_EQUIPO_ID, 2);
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_CLAVE, "STR_TIPO_EQUIPO_CLAVE2");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_DESCRIPCION, "STR_TIPO_EQUIPO_DESCRIPCION2");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_CAPACIDAD, "STR_TIPO_EQUIPO_CAPACIDAD2");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_UNIDAD_MEDIDA, "STR_TIPO_EQUIPO_UNIDAD_MEDIDA2");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_MOVIMIENTO, "STR_TIPO_EQUIPO_MOVIMIENTO2");
        db.insertOrThrow(Tablas.TBL_CATALOGO_TIPO_EQUIPO, null, valores);

        valores.clear();
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.ID_INT_TIPO_EQUIPO_ID, 3);
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_CLAVE, "STR_TIPO_EQUIPO_CLAVE3");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_DESCRIPCION, "STR_TIPO_EQUIPO_DESCRIPCION3");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_CAPACIDAD, "STR_TIPO_EQUIPO_CAPACIDAD3");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_UNIDAD_MEDIDA, "STR_TIPO_EQUIPO_UNIDAD_MEDIDA3");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_MOVIMIENTO, "STR_TIPO_EQUIPO_MOVIMIENTO3");
        db.insertOrThrow(Tablas.TBL_CATALOGO_TIPO_EQUIPO, null, valores);

        valores.clear();
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.ID_INT_TIPO_EQUIPO_ID, 4);
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_CLAVE, "STR_TIPO_EQUIPO_CLAVE1");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_DESCRIPCION, "STR_TIPO_EQUIPO_DESCRIPCION1");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_CAPACIDAD, "STR_TIPO_EQUIPO_CAPACIDAD1");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_UNIDAD_MEDIDA, "STR_TIPO_EQUIPO_UNIDAD_MEDIDA1");
        valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_MOVIMIENTO, "STR_TIPO_EQUIPO_MOVIMIENTO1");
        db.insertOrThrow(Tablas.TBL_CATALOGO_TIPO_EQUIPO, null, valores);


        valores.clear();
        valores.put(Inventarios.EQUIPO_FOLIO, "Pasa001");
        valores.put(Inventarios.EQUIPO_RFID, 0);
        valores.put(Inventarios.FK_TIPO_EQUIPO_ID, 1);
        valores.put(Inventarios.FK_EQUIPO_ALMACEN_ID, 1);
        valores.put(Inventarios.EQUIPO_ESTATUS_ID, 1);
        valores.put(Inventarios.EQUIPO_PROPIO, 0);
        valores.put(Inventarios.FK_BRANCH_ID, 1);
        db.insertOrThrow(Tablas.INVENTARIO, null, valores);

        valores.clear();
        valores.put(Inventarios.EQUIPO_FOLIO, "Pasa001");
        valores.put(Inventarios.EQUIPO_RFID, 0);
        valores.put(Inventarios.FK_TIPO_EQUIPO_ID, 1);
        valores.put(Inventarios.FK_EQUIPO_ALMACEN_ID, 1);
        valores.put(Inventarios.EQUIPO_ESTATUS_ID, 1);
        valores.put(Inventarios.EQUIPO_PROPIO, 0);
        valores.put(Inventarios.FK_BRANCH_ID, 1);
        db.insertOrThrow(Tablas.INVENTARIO, null, valores);

        valores.clear();
        valores.put(Inventarios.EQUIPO_FOLIO, "Pasa002");
        valores.put(Inventarios.EQUIPO_RFID, 0);
        valores.put(Inventarios.FK_TIPO_EQUIPO_ID, 2);
        valores.put(Inventarios.FK_EQUIPO_ALMACEN_ID, 2);
        valores.put(Inventarios.EQUIPO_ESTATUS_ID, 1);
        valores.put(Inventarios.EQUIPO_PROPIO, 0);
        valores.put(Inventarios.FK_BRANCH_ID, 2);
        db.insertOrThrow(Tablas.INVENTARIO, null, valores);

        valores.clear();
        valores.put(Inventarios.EQUIPO_FOLIO, "Pasa003");
        valores.put(Inventarios.EQUIPO_RFID, 0);
        valores.put(Inventarios.FK_TIPO_EQUIPO_ID, 3);
        valores.put(Inventarios.FK_EQUIPO_ALMACEN_ID, 3);
        valores.put(Inventarios.EQUIPO_ESTATUS_ID, 1);
        valores.put(Inventarios.EQUIPO_PROPIO, 0);
        valores.put(Inventarios.FK_BRANCH_ID, 3);
        db.insertOrThrow(Tablas.INVENTARIO, null, valores);
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