package pasa.inventarios.com;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class Activity_Consulta_Inventario_Diario extends AppCompatActivity {

    String str_branch = "";
    String str_user = "";
    String str_pass = "";
    String str_app = "";
    String str_division = "";
    String str_valida = "";
    String str_bandera = "";

    HelperInventarios baseDatos;

    /** Items entered by the user is stored in this ArrayList variable */
    ArrayList<String> list = new ArrayList<String>();

    /** Declaring an ArrayAdapter to set items to ListView */
    ArrayAdapter<String> adapter;


    private static Activity_Login instancia = new Activity_Login();
    public Activity_Login obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new HelperInventarios(contexto);
        }
        return instancia;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__consulta__inventario__diario);
        obtenerInstancia(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.titulo_actividad_actividad_listar);

        prepararLista();

    }
    private void prepararLista() {
        Log.e("=========>>>>>>>>", "  Soy el metodo prepararLista - Inventario Diario");
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO, null);

        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                str_branch= c.getString(0);
                str_user = c.getString(1);
                str_pass = c.getString(2);
                str_app = c.getString(3);
                str_division = c.getString(4);
                str_valida = c.getString(5);
                Log.e("", "==>>     " + str_branch + "--" + str_user + "--" + str_pass + "--" + str_app + "--" + str_division + "--" + str_valida);


            } while(c.moveToNext());
        }
    }
}
