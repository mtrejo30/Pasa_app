package pasa.inventarios.com;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Abraham on 27/06/2016.
 */
public class UPreferencias {
    private static final String PREFERENCIA_CLAVE_API = "preferencia.claveApi";


    private static SharedPreferences getDefaultSharedPreferences(Context contexto) {
        return PreferenceManager.getDefaultSharedPreferences(contexto);
    }

    public static void guardarClaveApi(Context contexto, String claveApi) {
        SharedPreferences sp = getDefaultSharedPreferences(contexto);
        sp.edit().putString(PREFERENCIA_CLAVE_API, claveApi).apply();
    }

    public static String obtenerClaveApi(Context contexto) {
        return getDefaultSharedPreferences(contexto).getString(PREFERENCIA_CLAVE_API, null);
    }
}
