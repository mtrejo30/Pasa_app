package pasa.inventarios.com;

import android.content.ContentValues;
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
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import pasa.inventarios.com.client.android.CaptureActivity;
import pasa.inventarios.com.Contrato.*;

public class Activity_Inventario_Diario extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    TextView txtView_Fecha;
    TextView txtView_Division;
    Button btn_AddEditText;
    Button btn_delete_item;
    Button btn_buscar;
    Spinner spn_Almacen;
    LinearLayout container;
    View addView;
    ViewGroup finalContainer = null;
    TextView txtViewRow;
    EditText editText_Barcode;
    Button btn_Escanear;
    final int[] cont = {0};
    String _Codigo;
    final int Resultado=1;
    Button btn_Save;
    HelperInventarios baseDatos;
    TextView txtViewTit;
    TextView txtViewSub;
    TextView txtViewFolio_Inv;

    String str_id = "";
    String str_division = "";
    String str_fecha = "";
    String str_user = "";
    String str_barcode = "";
    String str_folio = "";
    String str_folio_cerrado = "";
    String str_id_almacen = "";
    String str_cerrar = "";
    String str_branch = "";

    String str_pass = "";
    String str_app = "";
    String str_valida = "";
    String str_bandera = "";
    boolean bool_query_inv = false;
    boolean bool_query_inv_diario = false;
    boolean result1 = false;
    boolean val11 = true;
    boolean val12 = true;
    boolean val13 = true;
    boolean val14 = true;
    boolean result = false;
    boolean val1 = false;
    boolean val2 = false;
    boolean val3 = false;
    boolean val4 = false;
    String str_folio_inevntario_diario = "";
    SimpleCursorAdapter genreSpinnerAdapter2;
    DbDataSource dataSource;
    String str_Equipo_Almacen = "";
    String str_Folio_Inventario_Diario = "";
    int id_Branch;
    int id_Equipo_Almacen;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            _Codigo = data.getStringExtra("id_codigo");
            Log.e("", "==========>>>>>>>>> " + _Codigo);
            editText_Barcode.setText(_Codigo);
        }
    }

    private static Activity_Inventario_Diario instancia = new Activity_Inventario_Diario();
    public Activity_Inventario_Diario obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new HelperInventarios(contexto);
        }
        return instancia;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__inventario__diario);

        obtenerInstancia(getApplicationContext());

        dataSource = new DbDataSource(this);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MMMM/yyyy");
        String formattedDate = df.format(c.getTime());
        txtView_Fecha = (TextView) findViewById(R.id.txtView_Fecha);
        //assert txtView_Fecha != null;
        txtView_Fecha.setText(formattedDate);

        txtView_Division = (TextView) findViewById(R.id.txtView_Division);

        txtViewFolio_Inv = (TextView) findViewById(R.id.txtView_Folio);

        container = (LinearLayout) findViewById(R.id.lin_AddEditTextEscaner);
        finalContainer = container;

        editText_Barcode = (EditText) findViewById(R.id.editText_BarcodeEscaner);
        prepararLista();
        mtd_Query_Tbl_Login_User();

        btn_Escanear = (Button) findViewById(R.id.btn_Escanear);
        btn_Escanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Activity_Inventario_Diario.this, CaptureActivity.class);
                startActivityForResult(i,Resultado);
            }
        });

        btn_AddEditText = (Button) findViewById(R.id.btn_AddEditText);
        btn_AddEditText.setEnabled(true);
        btn_AddEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText_Barcode.getText().toString().equals("")) {
                    /*LayoutInflater layoutInflater = (LayoutInflater)
                        getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    addView = layoutInflater.inflate(R.layout.fragment__row2, null);
                    txtViewRow = (TextView) addView.findViewById(R.id.editext_BarCode);
                    txtViewRow.setText(editText_Barcode.getText().toString());
                    finalContainer.addView(addView);
                    editText_Barcode.setText("");*/
                    mtd_insert(editText_Barcode.getText().toString().trim());
                    prepararLista();
                    editText_Barcode.setText("");
                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "La caja del código de barras esta vacía", Toast.LENGTH_SHORT).show();
                }
            }
        });


        spn_Almacen = (Spinner) findViewById(R.id.spn_Almacen);

        genreSpinnerAdapter2 = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,//Layout simple
                dataSource.getCatAlmacenes(),//Todos los registros
                new String[]{Contrato.cls_Columnas_Catalogo_Almacenes.
                        STR_EQUIPO_ALMACEN_DESCRIPCION},//Mostrar solo el nombre
                new int[]{android.R.id.text1},//View para el nombre
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco
        spn_Almacen.setAdapter(genreSpinnerAdapter2);
        spn_Almacen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor colCur=(Cursor)spn_Almacen.getSelectedItem();

                str_Folio_Inventario_Diario = colCur.getString(colCur.getColumnIndex(
                        cls_Columnas_Catalogo_Almacenes.STR_FOLIO_INVENTARIO_DIARIO));

                String str_Bran = colCur.getString(colCur.getColumnIndex(Contrato.
                        cls_Columnas_Catalogo_Almacenes.FK_INT_BRANCHID));

                str_Equipo_Almacen = str_Folio_Inventario_Diario;
                txtViewFolio_Inv.setText(str_Folio_Inventario_Diario);
                id_Branch = Integer.parseInt(str_Bran);
                id_Equipo_Almacen = Integer.parseInt(colCur.getString(colCur.getColumnIndex(cls_Columnas_Catalogo_Almacenes.ID_INT_EQUIPO_ALMACEN_ID)));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.
                navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setTitle(R.string.inventario_diario);
    }
    private void prepararLista() {
        final SQLiteDatabase db = baseDatos.getWritableDatabase();
        final Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO, null);
        finalContainer.removeAllViews();
        if (c.moveToFirst()) {
            do {
                Log.e("", "==>>     " + c.getString(0) + "--" + c.getString(1) + "--"
                        + c.getString(2) + "--" + c.getString(3) + "--" + c.getString(4)
                        + "--" + c.getString(5));
                LayoutInflater layoutInflater = (LayoutInflater)
                        getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                addView = layoutInflater.inflate(R.layout.listitem_titular, null);
                txtViewTit = (TextView) addView.findViewById(R.id.LblTitulo);
                txtViewTit.setText(c.getString(4));
                txtViewSub = (TextView) addView.findViewById(R.id.LblSubTitulo);
                txtViewSub.setText(c.getString(3) + " -- " + c.getString(2));
                btn_delete_item = (Button) addView.findViewById(R.id.btn_delete_item);
                final String bar = txtViewTit.getText().toString();
                btn_delete_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "El código " + bar + " se eliminó correctamente", Toast.LENGTH_SHORT).show();
                        db.execSQL("DELETE FROM " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO + " WHERE " + cls_Columnas_Inventario_Diario.STR_BARCODE + "= '" + bar + "'");
                        prepararLista();
                    }
                });
                finalContainer.addView(addView);
            } while (c.moveToNext());
        } else {
            Toast.makeText(getApplicationContext(), "No hay registros", Toast.LENGTH_SHORT).show();
        }

    }

    private void mtd_insert(String str_bar_code) {
        if(txtViewFolio_Inv.getText().toString().length() != 0) {
            Log.i("", "==>>     " + "--" + str_division + "--" + txtView_Fecha.getText().toString()
                    + "--" + str_user + "--" + str_bar_code + "--" + str_branch);

            SQLiteDatabase db = baseDatos.getWritableDatabase();
            Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO + " where "
                            + cls_Columnas_Inventario_Diario.STR_BARCODE + " = '" + str_bar_code + "'"
                    , null);
            if (c.moveToFirst()) {
                Toast.makeText(getApplicationContext(), "El folio: " + str_bar_code.toUpperCase()
                        + " ya existe en la base con el mismo Branch", Toast.LENGTH_LONG).show();
            } else {
                ContentValues valores = new ContentValues();
                valores.clear();
                //valores.put(cls_Columnas_Inventario_Diario.ID_INVENTARIO_DIARIO, );
                valores.put(cls_Columnas_Inventario_Diario.STR_DIVISION, str_division);
                valores.put(cls_Columnas_Inventario_Diario.STR_FECHA, txtView_Fecha.getText().toString());
                valores.put(cls_Columnas_Inventario_Diario.STR_USER, str_user);
                valores.put(cls_Columnas_Inventario_Diario.STR_BARCODE, str_bar_code);
                valores.put(cls_Columnas_Inventario_Diario.STR_FOLIO_INVENTARIO_DIARIO, txtViewFolio_Inv.getText().toString());
                valores.put(cls_Columnas_Inventario_Diario.INT_EQUIPO_ALMACEN_ID, id_Equipo_Almacen);
                valores.put(cls_Columnas_Inventario_Diario.INT_FK_ID, str_branch);
                db.insertOrThrow(HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO, null, valores);
            }
        } else{
            Toast.makeText(getApplicationContext(), "Elije un almacén que cuente con un folio", Toast.LENGTH_LONG).show();
        }
    }
    private void insertar(ViewGroup finalContainer) {
        for(int i = 0; i < finalContainer.getChildCount(); i++){
            View view = finalContainer.getChildAt(i);
            String str =finalContainer.getChildAt(i).toString();

            if (view instanceof RelativeLayout) {
                View editText = (View) ((RelativeLayout) view).getChildAt(1);
                EditText editText1 = (EditText) editText;
                str = editText1.getText().toString();
                Log.d("Entg000000000e", " === " + str);
            }
            String str_EquipoFolio = str;
            Log.e("", "==>>     " + i + "--" + str_division + "--"
                    + txtView_Fecha.getText().toString() + "--" + str_user + "--"
                    + str_EquipoFolio + "--" + str_branch);

            SQLiteDatabase db = baseDatos.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.clear();
            //valores.put(cls_Columnas_Inventario_Diario.ID_INVENTARIO_DIARIO, );
            valores.put(cls_Columnas_Inventario_Diario.STR_DIVISION, str_division);
            valores.put(cls_Columnas_Inventario_Diario.STR_FECHA, txtView_Fecha.getText().toString());
            valores.put(cls_Columnas_Inventario_Diario.STR_USER, str_user);
            valores.put(cls_Columnas_Inventario_Diario.STR_BARCODE, str_EquipoFolio);
            valores.put(cls_Columnas_Inventario_Diario.INT_FK_ID, str_branch);
            db.insertOrThrow(HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO, null, valores);

        }
    }
    public void mtd_Query_Tbl_Login_User(){
        Log.e("=========>>>>>>>>", "  Soy el metodo mtd_Query_Tbl_Login_User");
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_LOGIN_USER
                + " where int_select = 1", null);

        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                str_branch= c.getString(0);
                str_user = c.getString(1);
                str_pass = c.getString(2);
                str_app = c.getString(3);
                str_division = c.getString(4);
                str_valida = c.getString(5);
                str_bandera = c.getString(6);
                Log.e("", "==>>     " + str_branch + "--" + str_user + "--" + str_pass + "--"
                        + str_app + "--" + str_division + "--" + str_valida + "--" + str_bandera);
                txtView_Division.setText(str_division);
            } while(c.moveToNext());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_insercion_contacto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sincronisar:
                //Toast.makeText(getApplicationContext(), "Prueba Inventario diario", Toast.LENGTH_SHORT).show();
                //insertar();
                if (!estaConectado()) {
                } else {
                    if(finalContainer.getChildCount() != 0){
                        //Toast.makeText(getApplicationContext(), "Hay " + finalContainer.getChildCount(), Toast.LENGTH_SHORT).show();
                        TareaWSInsertarInventarioDiario tare = new TareaWSInsertarInventarioDiario();
                        tare.execute();
                    }else{
                        //Toast.makeText(getApplicationContext(), "No hay", Toast.LENGTH_SHORT).show();
                        prepararLista();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_AddEditText){ }

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
            Intent i = new Intent(Activity_Inventario_Diario.this,Activity_Home.class);
            startActivity(i);
        }

        if (id == R.id.nav_Agregar) {
            Intent i = new Intent(Activity_Inventario_Diario.this,Activity_AddData.class);
            startActivity(i);
        }

        if (id == R.id.nav_InventarioDiario) {
            Intent i = new Intent(Activity_Inventario_Diario.this,Activity_Inventario_Diario.class);
            startActivity(i);
        }

        if (id == R.id.nav_ConsultaInventarioDiario) {
            Intent i = new Intent(Activity_Inventario_Diario.this,Activity_Consulta_Inventario_Diario.class);
            startActivity(i);
        }

        if (id == R.id.nav_Sincronizar) {
            Intent i = new Intent(Activity_Inventario_Diario.this,Actividad_Lista_Inventarios.class);
            startActivity(i);
        }

        if (id == R.id.nav_Salir) {
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
                    if(!val1 || val2 || val3 || val4 || !val11 || !val12 || !val13 || !val14) {
                        Toast.makeText(getApplicationContext(), "Hay registros que no se pudieron sincronizar", Toast.LENGTH_LONG).show();

                        val11 = true;
                        val12 = true;
                        val13 = true;
                        val14 = true;

                        val1 = false;
                        val2 = false;
                        val3 = false;
                        val4 = false;
                    }else {
                        mtd_salir_sesion();

                    }
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




    private class TareaWSInsertarInventario extends AsyncTask<String, Integer, Boolean> {
        public int contador = 0;

        protected Boolean doInBackground(String... params) {
            String message;
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/altaEquipos");
            post.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            //post.setHeader("content-type", "application/json");
            try {
                String str_equipo_folio = "";
                String str_equipo_RFID = "";
                String str_tipo_Equipo_Id = "";
                String str_equipo_Almacen_Id = "";
                String str_equipo_Estatus_Id = "";
                String str_equipo_Propio = "";
                String str_branch_Id = "";
                SQLiteDatabase db = baseDatos.getWritableDatabase();
                Cursor c = db.rawQuery("Select equipo_Folio, equipo_RFID, tipo_Equipo_Id, equipo_Almacen_Id, equipo_Estatus_Id, equipo_Propio, branch_Id from " + HelperInventarios.Tablas.INVENTARIO, null);
                if (c.moveToFirst()) {
                    JSONObject object = new JSONObject();
                    ContentValues valores = new ContentValues();
                    HttpResponse resp;
                    do {
                        str_equipo_folio = c.getString(0);
                        str_equipo_RFID = c.getString(1);
                        str_tipo_Equipo_Id = c.getString(2);
                        str_equipo_Almacen_Id = c.getString(3);
                        str_equipo_Estatus_Id = c.getString(4);
                        str_equipo_Propio = c.getString(5);
                        str_branch_Id = c.getString(6);
                        object.put("equipoFolio", str_equipo_folio);
                        object.put("equipoRFID", "");
                        object.put("tipoEquipoId", str_tipo_Equipo_Id);
                        object.put("equipoAlmacenId", str_equipo_Almacen_Id);
                        object.put("equipoEstatusId", str_equipo_Estatus_Id);
                        object.put("equipoPropio", str_equipo_Propio);
                        object.put("branchId", str_branch_Id);
                        message = object.toString();
                        post.setEntity(new StringEntity(message, "UTF8"));
                        post.setHeader("Content-type", "application/json");
                        resp = httpClient.execute(post);
                        if (resp != null) {
                            if (resp.getStatusLine().getStatusCode() == 204)
                                result = true;
                        }
                        String respuesta = EntityUtils.toString(resp.getEntity());
                        String[] str_validar_msj = respuesta.split(" ");
                        String str_last2 = str_validar_msj[str_validar_msj.length - 1];
                        if (str_last2.equals("correctamente\"")) {
                            Log.e("Mensaje: correctamente", str_last2);
                            //db.execSQL("DELETE FROM inventario WHERE equipo_Folio = '" + str_equipo_folio + "'", null);
                            db.execSQL("DELETE FROM " + HelperInventarios.Tablas.INVENTARIO + " WHERE " + Contrato.ColumnasPasa.EQUIPO_FOLIO + " = '" + str_equipo_folio + "'");
                            val1 = true;
                        } else {
                            val1 = false;
                            if (str_last2.equals("registrado\"")) {
                                Log.e("Mensaje: registrado", str_last2);
                                val2 = true;
                            } else {
                                if(str_last2.equals("Existe\"")){
                                    val4 = true;
                                }else {
                                    Log.e("Mensaje: error", str_last2);
                                    val3 = true;
                                }
                            }
                        }
                        result = true;
                    } while (c.moveToNext());
                } else {
                    Toast.makeText(getApplicationContext(), "No hay datos", Toast.LENGTH_SHORT);
                }
            } catch (Exception ex) {
                Log.i("ServicioRest", "Error=============>>>>!", ex);
                Log.i("TareaWSInsertar: ", "catch(Exception ex)");
                result = false;
            }
            return result;
        }

        public void showToast(String toast) {
            toast = toast.substring(1, toast.length() - 1);
            final String finalToast = toast;
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Activity_Inventario_Diario.this, finalToast, Toast.LENGTH_SHORT).show();
                }
            });
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
            }
            if (val1) {
                Toast.makeText(getApplicationContext(), "Todos los datos se sincronizaron de manera correcta", Toast.LENGTH_LONG).show();
            } else {
                if (val2) {
                    Toast.makeText(getApplicationContext(), "Folios existentes con el mismo branch y usuario", Toast.LENGTH_LONG).show();
                } else {
                    if(val4){
                        Toast.makeText(getApplicationContext(), "El almacén no existe en la base de datos", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Ha ocurrido un error, repórtelo con la empresa", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }


    private class TareaWSInsertarInventarioDiario extends AsyncTask<String, Integer, Boolean> {
        protected Boolean doInBackground(String... params) {
            String message;
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/altaEquiposInsertReporte");
            post.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            try {
                Log.e("=========>>>>>>>>", "  Soy el metodo TareaWSInsertar - Inventario Diario");
                SQLiteDatabase db = baseDatos.getWritableDatabase();
                Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO, null);
                db.execSQL("UPDATE tbl_inventario_diario SET cerrarInventario = 1 where _id = (SELECT MAX(_id) from tbl_inventario_diario)");
                if (c.moveToFirst()) {
                    LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    addView = layoutInflater.inflate(R.layout.listitem_titular, null);
                    JSONObject object = new JSONObject();
                    ContentValues valores = new ContentValues();
                    HttpResponse resp;
                    do {
                        str_id = c.getString(0);
                        str_division = c.getString(1);
                        str_fecha = c.getString(2);
                        str_user = c.getString(3);
                        str_barcode = c.getString(4);
                        str_folio = c.getString(5);
                        str_id_almacen = c.getString(6);
                        str_cerrar = c.getString(7);
                        str_branch = c.getString(8);

                        //13--GIN MONTERREY--25/julio/2016--4400170--hjjk--INV/EQUIP-000009-072016--9
                        Log.i("Muestra", "==>>     " + str_id + "--" + str_division + "--" + str_fecha + "--" + str_user + "--" + str_barcode + "--" + str_folio + "--" + str_id_almacen + "--" + str_cerrar + "--" + str_branch);
                        ////////////////////////////////////////////////
                        object.put("equipoFolio", str_barcode.trim());
                        object.put("branchId", str_branch.trim());
                        object.put("user", str_user.trim());
                        object.put("equipoAlmacenId", str_id_almacen.trim());
                        object.put("folioInventarioDiario", str_folio.trim());
                        object.put("cerrarInventario", str_cerrar);
                        message = object.toString();
                        post.setEntity(new StringEntity(message, "UTF8"));
                        post.setHeader("Content-type", "application/json");
                        resp = httpClient.execute(post);
                        if (resp != null) {
                            if (resp.getStatusLine().getStatusCode() == 204) {
                                result1 = true;
                                Log.e("=====>>>>>", " Soy IF ");
                            } else {
                                Log.e("=====>>>>>", " Soy ELSE " + resp.getStatusLine().getStatusCode());
                            }
                        }
                        String respuesta = "Respuesta default";
                        //assert resp != null;
                        respuesta = EntityUtils.toString(resp.getEntity());
                        //showToast(respuesta);
                        String[] str_validar_msj = respuesta.split(" ");
                        String str_last1 = str_validar_msj[0] + " " + str_validar_msj[1] + " " + str_validar_msj[2];
                        String str_last2 = str_validar_msj[str_validar_msj.length - 1];
                        Log.d("", " =====>>>>> :\"" + str_last1 + "\"");
                        Log.d("", " =====>>>>> :\"" + str_last2 + "\"");
                        if (str_last1.equals("\"Se registro Correctamente")) {
                            String[] args = new String[]{str_id};
                            db.execSQL("DELETE FROM tbl_inventario_diario WHERE _id=?", args);
                        } else {
                            val11 = false;
                            if (str_last2.equals("Existente\"")) {
                                val12 = false;
                            } else {
                                if (str_last2.equals("Cerrado\"")) {
                                    val14 = false;
                                    str_folio_cerrado = str_folio;
                                }else{
                                    val13 = false;
                                }
                            }
                        }

                        Log.e("getEntity.getContent", "=====>>>>> " + respuesta);
                        Log.e("Status", "=====>>>>> " + resp.getStatusLine().getStatusCode());
                        Log.e("Status message", "=====>>>>>" + message);
                        Log.e("resp.getStatusLine", "=====>>>>>" + resp.getStatusLine().toString());
                        result1 = true;
                    } while (c.moveToNext());
                } else {
                    Toast.makeText(getApplicationContext(), "No hay datos", Toast.LENGTH_SHORT);
                }
            } catch (Exception ex) {
                Log.e("ServicioRest", "Error=============>>>>!", ex);
                Log.d("TareaWSInsertar: ", "catch(Exception ex)");
                result1 = false;
            }
            return result1;
        }

        public void showToast(String toast) {
            toast = toast.substring(1, toast.length() - 1);
            final String finalToast = toast;
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Activity_Inventario_Diario.this, finalToast, Toast.LENGTH_SHORT).show();
                }
            });
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.e("onPostExecute", "=========" + finalContainer.getChildCount());
            }
            //Toast.makeText(getApplicationContext(), "Entré al onPostExecute", Toast.LENGTH_SHORT).show();
            if (val11) {
                Toast.makeText(getApplicationContext(), "Todos los datos se sincronizaron de manera correcta", Toast.LENGTH_SHORT).show();
            } else {
                if (!val12) {
                    Toast.makeText(getApplicationContext(), "Folios existentes con el mismo branch y usuario", Toast.LENGTH_SHORT).show();
                } else {
                    if (!val13) {
                        Toast.makeText(getApplicationContext(), "Ha ocurrido un error, repórtelo con la empresa", Toast.LENGTH_LONG).show();
                    } else{
                        if(!val14) {
                            //Toast.makeText(getApplicationContext(), "Éste folio ya está cerrado", Toast.LENGTH_LONG).show();
                            //mtd_dialog_folio(str_folio_cerrado);
                            mtd_dialog_alert(str_folio_cerrado);

                        }
                    }
                }
            }
            prepararLista();
        }
    }

    public void mtd_dialog_alert(final String str_folio_cerrado){
        final android.support.v7.app.AlertDialog.Builder dialogo1 = new android.support.v7.app.AlertDialog.Builder(this);
        dialogo1.setTitle("Advertencia... ");
        dialogo1.setMessage("El folio de inventario: " + str_folio_cerrado + " ya está cerrado, los registros que contienen este folio necesitan ser cambiados por otro folio para realizar la sincronización correcta");
        dialogo1.setIcon(R.drawable.ic_information_black_18dp);
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                mtd_dialog_elije(str_folio_cerrado);
            }
        });
        dialogo1.show();
    }


    public void mtd_dialog_elije(final String str_folio_cerrado){

        int i = 0;
        final SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("select vch_folio_inventario_diario from " + HelperInventarios.Tablas.TBL_CATALOGO_ALMACENES + " where vch_folio_inventario_diario <> '' and vch_folio_inventario_diario <> '" + str_folio_cerrado + "'", null);
        final String[] items = new String[c.getCount()];
        if(c.moveToFirst()){
            do {
                items[i] = c.getString(0);
                Log.i("", items[i]);
                i++;
            }while (c.moveToNext());
        }
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Activity_Inventario_Diario.this);
        builder.setTitle("Elije el nuevo folio:");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(getApplicationContext(), "El nuevo folio es: \n" + items[item], Toast.LENGTH_LONG).show();
                mtd_update_folio_cerrado(str_folio_cerrado, items[item]);
            }
        });
        builder.show();
    }
    public void mtd_update_folio_cerrado(String str_folio_cerrado, String item){
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.clear();
        db.execSQL("UPDATE "+ HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO +" SET cerrarInventario = " + 0);
        db.execSQL("UPDATE "+ HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO
                +" SET vch_folio_inventario_diario = '" + item
                + "', equipoAlmacenId = (select " + cls_Columnas_Catalogo_Almacenes.ID_INT_EQUIPO_ALMACEN_ID + " from "
                + HelperInventarios.Tablas.TBL_CATALOGO_ALMACENES
                + " where " + cls_Columnas_Catalogo_Almacenes.STR_FOLIO_INVENTARIO_DIARIO + " = '" + item
                + "') where vch_folio_inventario_diario = '"
                + str_folio_cerrado + "'");

        //equipoAlmacenId
        prepararLista();
    }
}
