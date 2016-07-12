package pasa.inventarios.com;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import pasa.inventarios.com.Contrato.*;
import pasa.inventarios.com.HelperInventarios.*;

public class Activity_Login extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
                                            LoaderManager.LoaderCallbacks<Cursor> {
    private ListView lstClientes;
    EditText txt_User;
    CheckBox chrecuerdame;
    EditText txt_Pass;
    private String[] clientes;
    SQLiteDatabase db;
    private String[][] clientes_res;
    private String[][] almacenes_res;
    private String[][] tipos_res;
    boolean resul = false;
    String str_Division = "División Default";
    public static final String URI_CONTACTO = "extra.uriContacto";
    DbDataSource dataSource;
    private Uri uriContacto;
    HelperInventarios baseDatos;
    public String _msje = "";
    ProgressDialog pDialog;
    _VariablesPublicas _variables = new _VariablesPublicas();
    private String MensajeError = "Datos Incorrectos";

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

        SharedPreferences prefs = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        _variables.usuario  = prefs.getString("user", "");
        _variables.pass = prefs.getString("pass", "");
        if (prefs.getString("user","").length()>0)
        {
            Intent i = new Intent(Activity_Login.this,Activity_Home.class);
            startActivity(i);
            //finish();
        }

        setContentView(R.layout.activity__login);
        obtenerInstancia(getApplicationContext());

        Button btn_Access = (Button) findViewById(R.id.btnAceptar);
        btn_Access.setOnClickListener(this);
        chrecuerdame=(CheckBox) findViewById(R.id.checkBox);
        txt_User = (EditText) findViewById(R.id.idtUsuario);
        txt_Pass = (EditText) findViewById(R.id.editText);

        chrecuerdame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                SharedPreferences prefs= getSharedPreferences("sesion",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user", txt_User.getText().toString());
                editor.putString("pass", txt_Pass.getText().toString());
                editor.commit();
            }
        });

        dataSource = new DbDataSource(this);

        String uri = getIntent().getStringExtra(URI_CONTACTO);
        if (uri != null) {
            uriContacto = Uri.parse(uri);
            getSupportLoaderManager().restartLoader(1, null, this);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnAceptar){

            if(!txt_User.getText().toString().equals("") & !txt_Pass.getText().toString().equals("")) {

                /*  Inicio validacion del internet    */
                if(!estaConectado()) {
                }
                else {
                    TareaWSListarUser tarea = new TareaWSListarUser();
                    tarea.execute();
                }
                /*  Fin validacion del internet    */

            }else {
                Toast.makeText(getApplicationContext(),
                        "No deje los campos vacios", Toast.LENGTH_SHORT).show();
                txt_User.requestFocus();
            }

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, uriContacto, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    //Tarea Asíncrona para llamar al WS de listado en segundo plano
    private class TareaWSListarUser extends AsyncTask<String,Integer,Boolean> {
        String a = txt_User.getText().toString();
        String b = txt_Pass.getText().toString();


        protected void onPreExecute() {

            /*  Inicio validacion del internet    */
            pDialog = new ProgressDialog(Activity_Login.this);
            pDialog.setMessage("Autenticando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            /*  Fin validacion del internet    */
        }


        protected Boolean doInBackground(String... params) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet del =
                    new HttpGet("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/loginUser?user="+a+"&pass="+b+"&app=1");
            del.addHeader(BasicScheme.authenticate( new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            del.setHeader("content-type", "application/json");
            try
            {
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());
                JSONArray respJSON = new JSONArray(respStr);
                clientes = new String[respJSON.length()];
                clientes_res = new String[respJSON.length()][3];
                for(int i=0; i<respJSON.length(); i++) {
                    JSONObject obj = respJSON.getJSONObject(i);
                    String idBranch = obj.getString("branchId");
                    String strName = obj.getString("name");
                    String strValida = obj.getString("valida");
                    clientes_res[i][0] = idBranch;
                    clientes_res[i][1] = strName;
                    clientes_res[i][2] = strValida;
                    Log.d("TareaWSListar", "Metodo - for: " + i);
                    Log.e("apiLogin", "=========>>>>>>>>>: " + strValida);
                    /*if (Integer.parseInt(clientes_res[i][2]) == 1) {
                        */
                    insertar(i);
                    resul = true;
                    /*} else {
                        Toast.makeText(getApplicationContext(), "Datos Incorrectos", Toast.LENGTH_SHORT).show();
                    }*/
                }
            }
            catch(Exception ex)
            {
                Log.e("Error =====>>>>> ","Api: loginUser", ex);
                Toast.makeText(getApplicationContext(),
                        "Ocurrió un error al tratar de conectarse al servidor", Toast.LENGTH_SHORT).show();
                resul = false;
            }
            return resul;
        }
        private void insertar(int i) {
            SQLiteDatabase db = baseDatos.getWritableDatabase();
            ContentValues valores = new ContentValues();
            if (Integer.parseInt(clientes_res[0][2]) == 1) {
                if (i == 0) {
                    db.execSQL("delete from " + Tablas.INVENTARIO);
                    db.execSQL("delete from " + Tablas.TBL_CATALOGO_TIPO_EQUIPO);
                    db.execSQL("delete from " + Tablas.TBL_CATALOGO_ALMACENES);
                    db.execSQL("delete from " + Tablas.TBL_LOGIN_USER);
                    db.execSQL("delete from " + Tablas.TBL_INVENTARIO_DIARIO);
                    valores.clear();
                    valores.put(cls_Columnas_Login_User.ID_INT_BRANCHID, 0);
                    valores.put(cls_Columnas_Login_User.INT_USER, a);
                    valores.put(cls_Columnas_Login_User.STR_PASS, b);
                    valores.put(cls_Columnas_Login_User.STR_APP, 1);
                    valores.put(cls_Columnas_Login_User.STR_NAME, "");
                    valores.put(cls_Columnas_Login_User.INT_VALIDA, 0);
                    valores.put(cls_Columnas_Login_User.INT_SELECT, 0);
                    db.insertOrThrow(HelperInventarios.Tablas.TBL_LOGIN_USER, null, valores);
                }
                    Log.d("", " ===>> " + (clientes_res[i][0]));
                    Log.d("", " ===>> " + (clientes_res[i][1]));
                    Log.d("", " ===>> " + (clientes_res[i][2]));
                    Log.d("", " ===>> " + a);
                    Log.d("", " ===>> " + b);
                    valores.clear();
                    valores.put(cls_Columnas_Login_User.ID_INT_BRANCHID, (Integer.parseInt(clientes_res[i][0])));
                    valores.put(cls_Columnas_Login_User.INT_USER, a);
                    valores.put(cls_Columnas_Login_User.STR_PASS, b);
                    valores.put(cls_Columnas_Login_User.STR_APP, 1);
                    valores.put(cls_Columnas_Login_User.STR_NAME, (clientes_res[i][1]));
                    valores.put(cls_Columnas_Login_User.INT_VALIDA, (Integer.parseInt(clientes_res[i][2])));
                    valores.put(cls_Columnas_Login_User.INT_SELECT, 0);
                    db.insertOrThrow(HelperInventarios.Tablas.TBL_LOGIN_USER, null, valores);

            }
        }
        protected void onPostExecute(Boolean result) {
            if (result) {
                if (Integer.parseInt(clientes_res[0][2]) == 1){
                    TareaWSListarCatalogosAlmacenes tarea2 = new TareaWSListarCatalogosAlmacenes();
                    tarea2.execute();
                }
                else {
                    pDialog.cancel();
                    Toast.makeText(getApplicationContext(),
                            "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    txt_User.requestFocus();
                }
            }
            else {

                pDialog.cancel();
                Toast.makeText(getApplicationContext(),
                        "Datos incorrectos", Toast.LENGTH_SHORT).show();
                txt_User.requestFocus();
            }
        }
    }

    //Tarea Asíncrona para llamar al WS de listado en segundo plano
    private class TareaWSListarCatalogosAlmacenes extends AsyncTask<String,Integer,Boolean> {

        String a = txt_User.getText().toString();
        String b = txt_Pass.getText().toString();
        protected Boolean doInBackground(String... params) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet del =
                    new HttpGet("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/getCatalogoAlmacenes?branchId="+(Integer.parseInt(clientes_res[0][0])));
            del.addHeader(BasicScheme.authenticate( new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            del.setHeader("content-type", "application/json");
            Log.d("Aquiiiiiii", " ======================================>> " + (clientes_res[0][0]));

            try
            {
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());
                JSONArray respJSON = new JSONArray(respStr);
                clientes = new String[respJSON.length()];
                almacenes_res = new String[respJSON.length()][4];
                for(int i=0; i<respJSON.length(); i++)	        	{
                    JSONObject obj = respJSON.getJSONObject(i);
                    String _id = obj.getString("equipoAlmacenId");
                    String vch_equipo_almacen_clave = obj.getString("equipoAlmacenClave");
                    String vch_equipo_almacen_descripcion = obj.getString("equipoAlmacenDescripcion");
                    String fk_int_branch = obj.getString("BranchId");
                    //clientes[i] = "" + idCli + "-" + nombCli + "-" + telefCli;
                    almacenes_res[i][0] = _id;
                    almacenes_res[i][1] = vch_equipo_almacen_clave;
                    almacenes_res[i][2] = vch_equipo_almacen_descripcion;
                    almacenes_res[i][3] = fk_int_branch;
                    Log.d("TareaWSListar", "Metodo - for: " + i);
                    insertar(i);
                }

                resul = true;
            }
            catch(Exception ex)
            {
                Log.e("ServicioRest","Error!", ex);
                resul = false;
            }
            return resul;
        }
        private void insertar(int i) {
            SQLiteDatabase db = baseDatos.getWritableDatabase();
            ContentValues valores = new ContentValues();
            if(i == 0){
                Log.e("===>>>", "Pasé al if" );
                valores.clear();
                valores.put(cls_Columnas_Catalogo_Almacenes.ID_INT_EQUIPO_ALMACEN_ID, 0);
                valores.put(cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_CLAVE, 0);
                valores.put(cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_DESCRIPCION, "");
                valores.put(cls_Columnas_Catalogo_Almacenes.FK_INT_BRANCHID, 0);
                db.insertOrThrow(Tablas.TBL_CATALOGO_ALMACENES, null, valores);
            }
                Log.d("", " ===>> " + (almacenes_res[i][0]));
                Log.d("", " ===>> " + (almacenes_res[i][1]));
                Log.d("", " ===>> " + (almacenes_res[i][2]));
                Log.d("", " ===>> " + (almacenes_res[i][3]));
                valores.clear();
                valores.put(cls_Columnas_Catalogo_Almacenes.ID_INT_EQUIPO_ALMACEN_ID, (Integer.parseInt(almacenes_res[i][0])));
                valores.put(cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_CLAVE, almacenes_res[i][1]);
                valores.put(cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_DESCRIPCION, almacenes_res[i][2]);
                valores.put(cls_Columnas_Catalogo_Almacenes.FK_INT_BRANCHID, almacenes_res[i][3]);
                db.insertOrThrow(Tablas.TBL_CATALOGO_ALMACENES, null, valores);


        }

        protected void onPostExecute(Boolean result) {
            if (result)            {
                if (Integer.parseInt(clientes_res[0][2]) == 1){
                    TareaWSListarTipoEquipo tarea3 = new TareaWSListarTipoEquipo();
                    tarea3.execute();

                }
                else {
                    pDialog.cancel();
                    Toast.makeText(getApplicationContext(),
                            "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    txt_User.requestFocus();
                }
            }
            else {
                pDialog.cancel();
                Toast.makeText(getApplicationContext(),
                        "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                txt_User.requestFocus();
            }
        }
    }

    //Tarea Asíncrona para llamar al WS de listado en segundo plano
    private class TareaWSListarTipoEquipo extends AsyncTask<String,Integer,Boolean> {
        String a = txt_User.getText().toString();
        String b = txt_Pass.getText().toString();
        protected Boolean doInBackground(String... params) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet del =
                    new HttpGet("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/getCatalogoTipoEquipo");
            del.addHeader(BasicScheme.authenticate( new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            del.setHeader("content-type", "application/json");
            Log.d("Aquiiiiiii", " ======================================>> " + (almacenes_res[0][0]));
            try
            {
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());
                JSONArray respJSON = new JSONArray(respStr);
                clientes = new String[respJSON.length()];
                tipos_res = new String[respJSON.length()][6];
                for(int i=0; i<respJSON.length(); i++)	        	{
                    JSONObject obj = respJSON.getJSONObject(i);
                    String _id = obj.getString("tipoEquipoId");
                    String tipoEquipoClave = obj.getString("tipoEquipoClave");
                    String tipoEquipoDescripcion = obj.getString("tipoEquipoDescripcion");
                    String tipoEquipoCapacidad = obj.getString("tipoEquipoCapacidad");
                    String tipoEquipoUnidadMedida = obj.getString("tipoEquipoUnidadMedida");
                    String tipoEquipoMovimiento = obj.getString("tipoEquipoMovimiento");
                    //clientes[i] = "" + idCli + "-" + nombCli + "-" + telefCli;
                    tipos_res[i][0] = _id;
                    tipos_res[i][1] = tipoEquipoClave;
                    tipos_res[i][2] = tipoEquipoDescripcion;
                    tipos_res[i][3] = tipoEquipoCapacidad;
                    tipos_res[i][4] = tipoEquipoUnidadMedida;
                    tipos_res[i][5] = tipoEquipoMovimiento;
                    Log.d("TareaWSListar", "Metodo - for: " + i);
                    insertar(i);
                }

                resul = true;
            }
            catch(Exception ex)
            {
                Log.e("ServicioRest","Error!", ex);
                resul = false;
            }
            return resul;
        }
        private void insertar(int i) {
            SQLiteDatabase db = baseDatos.getWritableDatabase();
            ContentValues valores = new ContentValues();
            if(i==0){
                valores.clear();
                valores.put(cls_Columnas_Catalogo_Tipo_Equipo.ID_INT_TIPO_EQUIPO_ID, 0);
                valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_CLAVE, "");
                valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_DESCRIPCION, "");
                valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_CAPACIDAD, "");
                valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_UNIDAD_MEDIDA, "");
                valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_MOVIMIENTO, "");
                db.insertOrThrow(Tablas.TBL_CATALOGO_TIPO_EQUIPO, null, valores);
            }
                Log.d("", " ===>> " + (tipos_res[i][0]));
                Log.d("", " ===>> " + (tipos_res[i][1]));
                Log.d("", " ===>> " + (tipos_res[i][2]));
                Log.d("", " ===>> " + (tipos_res[i][3]));
                Log.d("", " ===>> " + (tipos_res[i][4]));
                Log.d("", " ===>> " + (tipos_res[i][5]));
                valores.clear();
                valores.put(cls_Columnas_Catalogo_Tipo_Equipo.ID_INT_TIPO_EQUIPO_ID, (Integer.parseInt(tipos_res[i][0])));
                valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_CLAVE, tipos_res[i][1]);
                valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_DESCRIPCION, tipos_res[i][2]);
                valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_CAPACIDAD, tipos_res[i][3]);
                valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_UNIDAD_MEDIDA, tipos_res[i][4]);
                valores.put(cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_MOVIMIENTO, tipos_res[i][5]);
                db.insertOrThrow(Tablas.TBL_CATALOGO_TIPO_EQUIPO, null, valores);

        }

        protected void onPostExecute(Boolean result) {
            if (result)            {
                if (Integer.parseInt(clientes_res[0][2]) == 1){
                    if(clientes_res.length == 1){

                        /*  Inicio validacion del internet    */
                        txt_Pass.setText("");
                        txt_User.setText("");
                        pDialog.cancel();
                        /*  Fin validacion del internet    */

                        SQLiteDatabase db = baseDatos.getWritableDatabase();
                        ContentValues valores = new ContentValues();
                        valores.clear();
                        db.execSQL("UPDATE "+ Tablas.TBL_LOGIN_USER +" SET int_select= " + 0);
                        valores.clear();
                        db.execSQL("UPDATE "+ Tablas.TBL_LOGIN_USER +" SET int_select= " + 1 + " WHERE _id= " + clientes_res[0][0]);

                        Log.d("Adios", " ===>> " + (clientes_res[0][1]));
                        Intent i = new Intent(Activity_Login.this, Activity_Home.class);
                        startActivity(i);
                    }else {
                        /*  Inicio validacion del internet    */
                        txt_Pass.setText("");
                        txt_User.setText("");
                        pDialog.cancel();
                        /*  Fin validacion del internet    */

                        //pDialog.hide();
                        //pDialog.dismiss();
                        Log.d("Adios", " ===>> " + (Integer.parseInt(clientes_res[0][2])));
                        Intent i = new Intent(Activity_Login.this, Activity_Division.class);
                        startActivity(i);
                    }
                }
                else {

                    pDialog.cancel();
                    Toast.makeText(getApplicationContext(),
                            "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    txt_User.requestFocus();
                }
            }
            else {
                Toast.makeText(getApplicationContext(),
                        "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                txt_User.requestFocus();
            }
        }
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
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(title);

        alertDialog.setMessage(message);

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();
    }
    /*  Fin validacion del internet    */
}
