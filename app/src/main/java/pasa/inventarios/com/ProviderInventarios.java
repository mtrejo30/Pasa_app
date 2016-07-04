package pasa.inventarios.com;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import pasa.inventarios.com.Contrato.Inventarios;
import pasa.inventarios.com.HelperInventarios.Tablas;

/**
 * Created by Abraham on 27/06/2016.
 */
public class ProviderInventarios extends ContentProvider {

    // Comparador de URIs de contenido
    public static final UriMatcher uriMatcher;

    // Identificadores de tipos
    public static final int INVENTARIOS = 100;
    public static final int INVENTARIOS_ID = 101;

    public static final int USERS = 100;
    public static final int USER_ID = 101;

    public static final int ALMACENES = 100;
    public static final int ALMACENES_ID = 101;

    public static final int TIPOEQUIPOS = 100;
    public static final int TIPOEQUIPOS_ID = 101;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Contrato.AUTORIDAD, "inventarios", INVENTARIOS);
        uriMatcher.addURI(Contrato.AUTORIDAD, "inventarios/*", INVENTARIOS_ID);

        uriMatcher.addURI(Contrato.AUTORIDAD, "login_user", USERS);
        uriMatcher.addURI(Contrato.AUTORIDAD, "login_user/*", USER_ID);

        uriMatcher.addURI(Contrato.AUTORIDAD, "catalogo_almacenes", ALMACENES);
        uriMatcher.addURI(Contrato.AUTORIDAD, "catalogo_almacenes/*", ALMACENES_ID);

        uriMatcher.addURI(Contrato.AUTORIDAD, "tipo_equipo", TIPOEQUIPOS);
        uriMatcher.addURI(Contrato.AUTORIDAD, "tipo_equipo/*", TIPOEQUIPOS_ID);
    }

    private HelperInventarios manejadorBD;
    private ContentResolver resolver;

    @Override
    public boolean onCreate() {
        manejadorBD = new HelperInventarios(getContext());
        resolver = getContext().getContentResolver();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Obtener base de datos
        SQLiteDatabase db = manejadorBD.getWritableDatabase();
        // Comparar Uri
        int match = uriMatcher.match(uri);
        Cursor c;
        switch (match) {
            case INVENTARIOS:
                // Consultando todos los registros
                c = db.query(Tablas.INVENTARIO, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                c.setNotificationUri(resolver, Inventarios.URI_CONTENIDO);
                break;
            case INVENTARIOS_ID:
                // Consultando un solo registro basado en el Id del Uri
                String idContacto = Inventarios.obtenerIdContacto(uri);
                c = db.query(Tablas.INVENTARIO, projection,
                        Inventarios.ID_PASA + "=" + "\'" + idContacto + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs, null, null, sortOrder);
                c.setNotificationUri(resolver, uri);
                break;
            default:
                throw new IllegalArgumentException("URI no soportada: " + uri);
        }
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case INVENTARIOS:
                return Inventarios.MIME_COLECCION;
            case INVENTARIOS_ID:
                return Inventarios.MIME_RECURSO;
            default:
                throw new IllegalArgumentException("Tipo desconocido: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d("Insert:", "=========>>>" + uri);

            if (uriMatcher.match(uri) != INVENTARIOS) {
                throw new IllegalArgumentException("URI desconocida : " + uri);
            }
            ContentValues contentValues;
            if (values != null) {
                contentValues = new ContentValues(values);
            } else {
                contentValues = new ContentValues();
            }
            SQLiteDatabase db = manejadorBD.getWritableDatabase();
            long _id = db.insert(Tablas.INVENTARIO, null, contentValues);
            if (_id > 0) {
                resolver.notifyChange(uri, null, false);
                String idContacto = contentValues.getAsString(Inventarios.ID_PASA);
                return Inventarios.construirUriContacto(idContacto);
            }
            throw new SQLException("Falla al insertar fila en : " + uri);

    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = manejadorBD.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int filasAfectadas;
        switch (match) {
            case INVENTARIOS:
                filasAfectadas = db.delete(Tablas.INVENTARIO,
                        selection,
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case INVENTARIOS_ID:
                String idContacto = Inventarios.obtenerIdContacto(uri);
                filasAfectadas = db.delete(Tablas.INVENTARIO,
                        Inventarios.ID_PASA + "=" + "\'" + idContacto + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            default:
                throw new IllegalArgumentException("Contacto desconocido: " +
                        uri);
        }
        return filasAfectadas;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                                                        String[] selectionArgs) {
        SQLiteDatabase db = manejadorBD.getWritableDatabase();
        int filasAfectadas;
        switch (uriMatcher.match(uri)) {
            case INVENTARIOS:
                filasAfectadas = db.update(Tablas.INVENTARIO, values,
                        selection, selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            case INVENTARIOS_ID:
                String idContacto = Inventarios.obtenerIdContacto(uri);
                filasAfectadas = db.update(Tablas.INVENTARIO, values,
                        Inventarios.ID_PASA + "=" + "\'" + idContacto + "\'"
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                resolver.notifyChange(uri, null, false);
                break;
            default:
                throw new IllegalArgumentException("URI desconocida: " + uri);
        }
        return filasAfectadas;
    }
}
