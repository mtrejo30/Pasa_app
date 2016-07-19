package pasa.inventarios.com;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Activity_Home extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private Button btn_Acceder;
    private Button btn_ConsultaInventarioDiario;
    private Button btn_Sincronizacion;
    private Button btn_Inventario_Diario;
    HelperInventarios baseDatos;
    DbDataSource dataSource;
    boolean bool_query_inv = false;
    boolean bool_query_inv_diario = false;

    private static Activity_Home instancia = new Activity_Home();
    public Activity_Home obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new HelperInventarios(contexto);
        }
        return instancia;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__home);

        obtenerInstancia(getApplicationContext());
        dataSource = new DbDataSource(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        btn_Acceder = (Button) findViewById(R.id.btn_AgregarDatos);
        btn_Acceder.setOnClickListener(this);
        btn_ConsultaInventarioDiario = (Button) findViewById(R.id.btn_ConsultaInventarioDiario);
        btn_ConsultaInventarioDiario.setOnClickListener(this);
        btn_Sincronizacion = (Button) findViewById(R.id.btn_Sincronizacion);
        btn_Sincronizacion.setOnClickListener(this);
        btn_Inventario_Diario = (Button) findViewById(R.id.btn_InventarioDiario);
        btn_Inventario_Diario.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void onClick(View v) {

        /*que cargue por defecto home en otro caso seria verificar*/
        if (v.getId() == R.id.btn_AgregarDatos){
            Intent i = new Intent(v.getContext(), Activity_AddData.class);
            startActivity(i);
        }
        if (v.getId() == R.id.btn_InventarioDiario){
            Intent i = new Intent(v.getContext(), Activity_Inventario_Diario.class);
            startActivity(i);
        }
        if (v.getId() == R.id.btn_Sincronizacion){
            Intent i = new Intent(v.getContext(), Actividad_Lista_Inventarios.class);
            startActivity(i);
        }
        if (v.getId() == R.id.btn_ConsultaInventarioDiario){
            Intent i = new Intent(v.getContext(), Activity_Consulta_Inventario_Diario.class);
            startActivity(i);
        }

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            Intent i = new Intent(Activity_Home.this,Activity_Home.class);
            startActivity(i);
        }

        if (id == R.id.nav_Agregar) {
            Intent i = new Intent(Activity_Home.this,Activity_AddData.class);
            startActivity(i);
        }

        if (id == R.id.nav_InventarioDiario) {
            Intent i = new Intent(Activity_Home.this,Activity_Inventario_Diario.class);
            startActivity(i);
        }

        if (id == R.id.nav_ConsultaInventarioDiario) {
            Intent i = new Intent(Activity_Home.this,Activity_Consulta_Inventario_Diario.class);
            startActivity(i);
        }

        if (id == R.id.nav_Sincronizar) {
            Intent i = new Intent(Activity_Home.this,Actividad_Lista_Inventarios.class);
            startActivity(i);
        }

        if (id == R.id.nav_Salir) {
            /*
            Toast.makeText(getApplicationContext(), "Sesión cerrada con éxito", Toast.LENGTH_LONG).show();
            SharedPreferences preferences = getSharedPreferences("sesion", 0);
            preferences.edit().remove("user").commit();
            preferences.edit().remove("pass").commit();
            */
            /*
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            */

            boolean bool_query_inv1 = mtd_consulta_inventario();
            boolean bool_query_inv_diario1 = mtd_consulta_tbl_inventario_diario();

            //Toast.makeText(getApplicationContext(), "Datos " + bool_query_inv1 + bool_query_inv_diario1, Toast.LENGTH_SHORT).show();
            Log.e("", "Datos " + bool_query_inv1 + bool_query_inv_diario1);
            if(bool_query_inv1 || bool_query_inv_diario1){
                mtd_alert_dialog(bool_query_inv1, bool_query_inv_diario1);
            }else{
                mtd_salir_sesion();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void mtd_salir_sesion() {

        SharedPreferences preferences = getSharedPreferences("sesion", 0);
        preferences.edit().remove("user").commit();
        preferences.edit().remove("pass").commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }else{
            Intent salida=new Intent( Intent.ACTION_MAIN); //Llamando a la activity principal
            finish(); // La cerramos.
        }
    }

    private void mtd_alert_dialog(final boolean bool_query_inv1, final boolean bool_query_inv_diario1) {
        final AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Advertencia... ");
        dialogo1.setMessage("Tienes registros que no se han sincronizado, ¿Deseas sincronizar antes de salir de tu sesión?");
        dialogo1.setIcon(R.drawable.ic_information_black_18dp);
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                if(!estaConectado()) {
                }
                else {
                    mtd_sincronizar_y_salir(bool_query_inv1, bool_query_inv_diario1);
                    mtd_salir_sesion();
                    //mtd_cerrar_sesion();
                }
                //Toast.makeText(getApplicationContext(), "Sincronizar y salir", Toast.LENGTH_LONG).show();
            }
        });
        dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

                mtd_borrar_y_salir();
                mtd_salir_sesion();
                //mtd_cerrar_sesion();
                //Toast.makeText(getApplicationContext(), "Salir sin sincronizar", Toast.LENGTH_LONG).show();
            }
        });
        dialogo1.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(), "Se canceló", Toast.LENGTH_LONG).show();
            }
        });
        dialogo1.show();
    }

    private void mtd_borrar_y_salir() {
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        db.execSQL("delete from " + HelperInventarios.Tablas.INVENTARIO);
        db.execSQL("delete from " + HelperInventarios.Tablas.TBL_CATALOGO_TIPO_EQUIPO);
        db.execSQL("delete from " + HelperInventarios.Tablas.TBL_CATALOGO_ALMACENES);
        db.execSQL("delete from " + HelperInventarios.Tablas.TBL_LOGIN_USER);
        db.execSQL("delete from " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO);
    }

    private void mtd_sincronizar_y_salir(boolean bool_query_inv1, boolean bool_query_inv_diario1) {
        if(bool_query_inv1){
            if (!estaConectado()) {
            } else {
                if (!estaConectado()) {
                } else {
                    TareaWSInsertarInventario tare = new TareaWSInsertarInventario();
                    tare.execute();
                }
            }
        }
        if(bool_query_inv_diario1){
            if (!estaConectado()) {
            } else {
                TareaWSInsertarInventarioDiario tare = new TareaWSInsertarInventarioDiario();
                tare.execute();
            }
        }
    }

    public boolean mtd_consulta_inventario() {
        Log.e("", " ==>> -------------------------     mtd_consulta_inventario");
        final SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.INVENTARIO, null);
        if (c.moveToFirst()) {
            Log.e("", "==>>     " + c.getString(0) + "--" + c.getString(1) + "--" + c.getString(2) + "--" + c.getString(3) + "--" + c.getString(4) + "--" + c.getString(6) + "--" + c.getString(7) + "--" + c.getString(8));
            bool_query_inv = true;
            //Toast.makeText(getApplicationContext(), "Si hay datos " + bool_query_inv, Toast.LENGTH_SHORT).show();
            do {
            } while (c.moveToNext());
        } else {
            bool_query_inv = false;
            //Toast.makeText(getApplicationContext(), "No hay datos " + bool_query_inv, Toast.LENGTH_SHORT).show();
        }
        return bool_query_inv;
    }

    public boolean mtd_consulta_tbl_inventario_diario() {
        Log.e("", "==>>-------------------------     mtd_consulta_tbl_inventario_diario");
        final SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO, null);
        if (c.moveToFirst()) {
            Log.e("", "==>>     " + c.getString(0) + "--" + c.getString(1) + "--"
                    + c.getString(2) + "--" + c.getString(3) + "--" + c.getString(4)
                    + "--" + c.getString(5));
            bool_query_inv_diario = true;
            //Toast.makeText(getApplicationContext(), "Si hay datos " + bool_query_inv_diario, Toast.LENGTH_SHORT).show();
            do {
            } while (c.moveToNext());
        } else {
            bool_query_inv_diario = false;
            //Toast.makeText(getApplicationContext(), "No hay datos " + bool_query_inv_diario, Toast.LENGTH_SHORT).show();
        }
        return bool_query_inv_diario;
    }

    public void mtd_cerrar_sesion(){
        Toast.makeText(getApplicationContext(), "Sesión cerrada con éxito", Toast.LENGTH_LONG).show();
        SharedPreferences preferences = getSharedPreferences("sesion", 0);
        preferences.edit().remove("user").commit();
        preferences.edit().remove("pass").commit();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    /*  Inicio validacion del internet    */
    protected Boolean estaConectado(){
        if(conectadoWifi()){
            return true;
        }else{
            if(conectadoRedMovil()){
                return true;
            }else{
                showAlertDialog(this, "Error de Inicio de Sesión",
                        "Tu Dispositivo no tiene Conexión a Internet.", false);
                return false;
            }
        }
    }
    /*  Fin validacion del internet    */

    /*  Inicio validacion del internet    */
    protected Boolean conectadoWifi(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
    /*  Fin validacion del internet    */

    /*  Inicio validacion del internet    */
    protected Boolean conectadoRedMovil(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
    /*  Fin validacion del internet    */

    /*  Inicio validacion del internet    */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();

        alertDialog.setTitle(title);

        alertDialog.setMessage(message);

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
    /*  Fin validacion del internet    */

    private class TareaWSInsertarInventarioDiario extends AsyncTask<String, Integer, Boolean> {
        boolean result = false;
        protected Boolean doInBackground(String... params) {
            String message;
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/altaEquiposInsertReporte");
            post.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            try {
                SQLiteDatabase db = baseDatos.getWritableDatabase();
                Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO, null);
                if (c.moveToFirst()) {
                    JSONObject object = new JSONObject();
                    do {
                        object.put("equipoFolio", c.getString(4).trim());
                        object.put("branchId", c.getString(5).trim());
                        object.put("user", c.getString(3).trim());
                        message = object.toString();
                        post.setEntity(new StringEntity(message, "UTF8"));
                        post.setHeader("Content-type", "application/json");
                        httpClient.execute(post);
                        result = true;
                    } while (c.moveToNext());
                } else { }
            } catch (Exception ex) { result = false; }
            return result;
        }
        protected void onPostExecute(Boolean result) { if (result){} }
    }

    private class TareaWSInsertarInventario extends AsyncTask<String,Integer,Boolean> {
        boolean result = false;
        protected Boolean doInBackground(String... params) {
            String message;
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/altaEquipos");
            post.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            try {
                SQLiteDatabase db = baseDatos.getWritableDatabase();
                Cursor c = db.rawQuery("Select equipo_Folio, equipo_RFID, tipo_Equipo_Id, equipo_Almacen_Id, equipo_Estatus_Id, equipo_Propio, branch_Id from " + HelperInventarios.Tablas.INVENTARIO, null);
                if (c.moveToFirst()) {
                    JSONObject object = new JSONObject();
                    do {
                        object.put("equipoFolio", c.getString(0));
                        object.put("equipoRFID", "");
                        object.put("tipoEquipoId", c.getString(2));
                        object.put("equipoAlmacenId", c.getString(3));
                        object.put("equipoEstatusId", c.getString(4));
                        object.put("equipoPropio", c.getString(5));
                        object.put("branchId", c.getString(6));
                        message = object.toString();
                        post.setEntity(new StringEntity(message, "UTF8"));
                        post.setHeader("Content-type", "application/json");
                        httpClient.execute(post);
                        result = true;
                    } while (c.moveToNext());
                } else { }
            } catch (Exception ex) { result = false; }
            return result;
        }
        protected void onPostExecute(Boolean result) { if (result) { } }
    }

}
