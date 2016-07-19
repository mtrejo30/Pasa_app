package pasa.inventarios.com;

import android.content.ContentResolver;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import pasa.inventarios.com.Contrato.Inventarios;
import pasa.inventarios.com.HelperInventarios.*;
import pasa.inventarios.com.client.android.CaptureActivity;

public class Activity_AddData extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
                                            LoaderManager.LoaderCallbacks<Cursor>   {

    Button btn_AddEditText;
    TextView txtViewTit;
    TextView txtViewSub;
    Button btn_delete_item;
    EditText editText_Barcode;

    Spinner spn_TipoEquipo;
    Spinner spn_Almacen;
    Button btn_Escanear;
    int id_Tipo_Equipo;
    int id_Equipo_Almacen;
    String str_Tipo_Equipo = "";
    String str_Equipo_Almacen = "";
    String str_branch;
    int id_Branch;
    HelperInventarios baseDatos;
    LinearLayout container;
    View addView;
    ViewGroup finalContainer = null;
    SimpleCursorAdapter genreSpinnerAdapter1;
    SimpleCursorAdapter genreSpinnerAdapter2;
    DbDataSource dataSource;
    private static Activity_AddData instancia = new Activity_AddData();
    public static final String URI_CONTACTO = "extra.uriContacto";
    private Uri uriContacto;
    boolean bool_query_inv = false;
    boolean bool_query_inv_diario = false;
    boolean result = false;
    boolean val1 = true;
    boolean val2 = true;
    boolean val3 = true;

    public Activity_AddData obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new HelperInventarios(contexto);
        }
        return instancia;
    }

    String _Codigo;
    final int Resultado=1;

    public Activity_AddData() {
        finalContainer = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            _Codigo = data.getStringExtra("id_codigo");
            Log.e("", "==========>>>>>>>>> " + _Codigo);
            editText_Barcode.setText(_Codigo);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__add__data);

        obtenerInstancia(getApplicationContext());
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        dataSource = new DbDataSource(this);

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

        setTitle(R.string.alta_de_equipos);
        String uri = getIntent().getStringExtra(URI_CONTACTO);
        if (uri != null) {
            uriContacto = Uri.parse(uri);
            getSupportLoaderManager().restartLoader(1, null, this);
        }
        editText_Barcode = (EditText) findViewById(R.id.editText_BarcodeEscaner);

        spn_TipoEquipo = (Spinner) findViewById(R.id.spn_TipoEquipo);
        genreSpinnerAdapter1 = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,//Layout simple
                dataSource.getCatTipEquipo(),//Todos los registros
                new String[]{Contrato.cls_Columnas_Catalogo_Tipo_Equipo.
                        STR_TIPO_EQUIPO_DESCRIPCION},//Mostrar solo el nombre
                new int[]{android.R.id.text1},//View para el nombre
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco
        spn_TipoEquipo.setAdapter(genreSpinnerAdapter1);
        spn_TipoEquipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor colCur=(Cursor)spn_TipoEquipo.getSelectedItem();
                String str_Tip_Equi = colCur.getString(colCur.getColumnIndex(Contrato
                        .cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_DESCRIPCION));
                str_Tipo_Equipo = str_Tip_Equi;
                //Log.e("División", "Item==================: "+ str_Tip_Equi);
                id_Tipo_Equipo = (int) id;
                /*Toast.makeText(getApplicationContext(),
                        "Id-TipoEquipo: ===>>>" + id_Tipo_Equipo, Toast.LENGTH_SHORT).show();*/
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
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
                String str_Alm = colCur.getString(colCur.getColumnIndex(Contrato.
                        cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_DESCRIPCION));
                String str_Bran = colCur.getString(colCur.getColumnIndex(Contrato.
                        cls_Columnas_Catalogo_Almacenes.FK_INT_BRANCHID));
                str_Equipo_Almacen = str_Alm;
                id_Branch = Integer.parseInt(str_Bran);
                //Log.e("División", "Item==================: "+ str_Alm + " --------- " + id_Branch);
                id_Equipo_Almacen = (int) id;
                /*Toast.makeText(getApplicationContext(),
                        "Id-Almacen: ===>>>" + id_Equipo_Almacen + " --- ",
                        Toast.LENGTH_SHORT).show();*/
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // //////////////////////////////////////////////////////////////////
        //txt_Cantidad = (EditText) findViewById(R.id.edit_Cantidad);
        container = (LinearLayout) findViewById(R.id.lin_AddEditTextEscaner);
        finalContainer = container;

        prepararLista();
        /*
        btn_Add = (Button) findViewById(R.id.btn_AgregarCajas);
        btn_Add.setEnabled(true);
        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalContainer.removeAllViews();
                cont[0] = 0;
                if (!txt_Cantidad.getText().toString().equals("")) {
                    for (int i = 0; i < Integer.parseInt(txt_Cantidad.getText().toString()); i++) {
                        LayoutInflater layoutInflater =
                                (LayoutInflater) getApplicationContext().getSystemService(
                                        Context.LAYOUT_INFLATER_SERVICE);
                        addView = layoutInflater.inflate(R.layout.fragment__row, null);
                        textOut = (TextView) addView.findViewById(R.id.editext_BarCode);
                        finalContainer.addView(addView);
                    }
                    btn_Escanear.setEnabled(true);
                    btn_Escanear.setBackgroundColor(Color.parseColor("#017A42"));
                } else {
                    Toast.makeText(v.getContext(),
                            "No deje el campo vacío", Toast.LENGTH_SHORT).show();
                    txt_Cantidad.requestFocus();
                }
            }
        });
        */
        /*
        btn_Save = (Button) findViewById(R.id.btn_Save);
        btn_Save.setEnabled(false);
        btn_Save.setBackgroundColor(Color.parseColor("#60000000"));
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spn_TipoEquipo.getSelectedItemPosition() != 0) {
                    if (spn_Almacen.getSelectedItemPosition() != 0) {
                        String str = finalContainer.getChildAt(0).toString();
                        boolean ver = true;
                        for(int i = 0; i < finalContainer.getChildCount(); i++) {
                            View view = finalContainer.getChildAt(i);
                            if (view instanceof RelativeLayout) {
                                View editText = (View) ((RelativeLayout) view).getChildAt(0);
                                EditText editText1 = (EditText) editText;
                                str = editText1.getText().toString();
                                //Log.d("Entg000000000e", " === " + str.length());
                            }
                            if (str.length() < 1){
                                ver = false;
                            }
                        }
                        //Log.e("========>>>>>>>>", "" + ver);
                        if (ver == true) {
                            insertar(finalContainer);
                            btn_Save.setEnabled(false);
                            btn_Save.setBackgroundColor(Color.parseColor("#60000000"));
                            finalContainer.removeAllViews();
                            cont[0] = 0;
                        } else {
                            Toast.makeText(getApplicationContext(), "Hay campos vacios", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Elije un almacén", Toast.LENGTH_SHORT).show();
                        spn_Almacen.requestFocus();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Elije un equipo", Toast.LENGTH_SHORT).show();
                    spn_TipoEquipo.requestFocus();
                }
            }
        });
        */

        btn_AddEditText = (Button) findViewById(R.id.btn_AddEditText);
        assert btn_AddEditText != null;
        btn_AddEditText.setEnabled(true);
        btn_AddEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spn_TipoEquipo.getSelectedItemPosition() != 0) {
                    if (spn_Almacen.getSelectedItemPosition() != 0) {
                        if (!editText_Barcode.getText().toString().equals("")) {
                            mtd_insert(editText_Barcode.getText().toString().trim());
                            prepararLista();
                            editText_Barcode.setText("");
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "La caja del código de barras esta vacía", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Elije un almacén", Toast.LENGTH_SHORT).show();
                        spn_Almacen.requestFocus();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Elije un equipo", Toast.LENGTH_SHORT).show();
                    spn_TipoEquipo.requestFocus();
                }

            }
        });


        btn_Escanear = (Button) findViewById(R.id.btn_Escanear);
        btn_Escanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Activity_AddData.this, CaptureActivity.class);
                startActivityForResult(i,Resultado);
            }
        });

        /*
        btn_Escanear = (Button) findViewById(R.id.btn_Escanear);
        btn_Escanear.setEnabled(false);
        btn_Escanear.setBackgroundColor(Color.parseColor("#60000000"));
        btn_Escanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cont[0] == finalContainer.getChildCount()) {
                    btn_Escanear.setBackgroundColor(Color.parseColor("#60000000"));
                    btn_Escanear.setEnabled(false);
                    btn_Save.setEnabled(true);
                    btn_Save.setBackgroundColor(Color.parseColor("#017A42"));
                    txt_Cantidad.setText("");
                }else{
                    Intent i = new Intent(Activity_AddData.this, CaptureActivity.class);
                    startActivityForResult(i,Resultado);

                    finalContainer.getChildAt(cont[0]).requestFocus();
                    cont[0] = cont[0] + 1;
                    if (cont[0] == finalContainer.getChildCount()) {
                        btn_Escanear.setBackgroundColor(Color.parseColor("#60000000"));
                        btn_Escanear.setEnabled(false);
                        btn_Save.setEnabled(true);
                        btn_Save.setBackgroundColor(Color.parseColor("#017A42"));
                        txt_Cantidad.setText("");
                    }
                }
            }
        });*/
    }

    private void mtd_insert(String str_bar_code) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();

        Cursor c = db.rawQuery("Select * from " + Tablas.INVENTARIO + " where "
                + Contrato.ColumnasPasa.EQUIPO_FOLIO + " = '" + editText_Barcode.getText().toString() + "'"
                , null);
        if(c.moveToFirst()){
            Toast.makeText(getApplicationContext(), "El folio: " + editText_Barcode.getText().toString().toUpperCase()
                    + " ya existe en la base con el mismo Branch", Toast.LENGTH_LONG).show();
        }else {
            ContentValues valores = new ContentValues();
            valores.clear();
            valores.put(Inventarios.EQUIPO_FOLIO, str_bar_code);
            valores.put(Inventarios.EQUIPO_RFID, 0);
            valores.put(Inventarios.FK_TIPO_EQUIPO_ID, id_Tipo_Equipo);
            valores.put(Inventarios.FK_EQUIPO_ALMACEN_ID, id_Equipo_Almacen);
            valores.put(Inventarios.EQUIPO_ESTATUS_ID, 1);
            valores.put(Inventarios.EQUIPO_PROPIO, 0);
            valores.put(Inventarios.FK_BRANCH_ID, id_Branch);
            valores.put(Inventarios.EQUIPO_ALMACEN_STR, str_Equipo_Almacen);
            valores.put(Inventarios.TIPO_EQUIPO_STR, str_Tipo_Equipo);
            db.insertOrThrow(Tablas.INVENTARIO, null, valores);
        }
    }


    private void prepararLista() {
        Log.e("=========>>>>>>>>", "  Soy el metodo prepararLista - Inventario Diario");
        final SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from " + Tablas.INVENTARIO, null);
        finalContainer.removeAllViews();
        if (c.moveToFirst()) {
            do {
                /*str_id = c.getString(0);
                str_division = c.getString(1);
                str_fecha = c.getString(2);
                str_user = c.getString(3);
                str_barcode = c.getString(4);
                str_branch = c.getString(5);*/
                Log.e("", "==>>     " + c.getString(0) + "--" + c.getString(1) + "--" + c.getString(2) + "--" + c.getString(3) + "--" + c.getString(4) + "--" + c.getString(6) + "--" + c.getString(7) + "--" + c.getString(8));
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                addView = layoutInflater.inflate(R.layout.listitem_titular, null);
                txtViewTit = (TextView) addView.findViewById(R.id.LblTitulo);
                txtViewTit.setText(c.getString(1));
                txtViewSub = (TextView) addView.findViewById(R.id.LblSubTitulo);
                txtViewSub.setText(c.getString(7) + " -- " + c.getString(8) + " -- " + c.getString(9));
                btn_delete_item = (Button) addView.findViewById(R.id.btn_delete_item);
                final String bar = txtViewTit.getText().toString();
                btn_delete_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "El código " + bar + " se eliminó correctamente", Toast.LENGTH_SHORT).show();
                        db.execSQL("DELETE FROM " + Tablas.INVENTARIO + " WHERE " + Contrato.ColumnasPasa.EQUIPO_FOLIO + " = '" + bar + "'");
                        prepararLista();
                    }
                });
                finalContainer.addView(addView);
            } while (c.moveToNext());
        } else {
            Toast.makeText(getApplicationContext(), "No hay registros", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                //Toast.makeText(getApplicationContext(), "Prueba AddData", Toast.LENGTH_SHORT).show();
                //insertar();
                if (!estaConectado()) {
                } else {
                    if (finalContainer.getChildCount() != 0) {
                        //Toast.makeText(getApplicationContext(), "Hay " + finalContainer.getChildCount(), Toast.LENGTH_SHORT).show();
                        TareaWSInsertar tare = new TareaWSInsertar();
                        tare.execute();
                    } else {
                        //Toast.makeText(getApplicationContext(), "No hay", Toast.LENGTH_SHORT).show();
                        prepararLista();
                    }
                }
                break;
        }
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
            Intent i = new Intent(Activity_AddData.this,Activity_Home.class);
            startActivity(i);
        }

        if (id == R.id.nav_Agregar) {
            Intent i = new Intent(Activity_AddData.this,Activity_AddData.class);
            startActivity(i);
        }

        if (id == R.id.nav_InventarioDiario) {
            Intent i = new Intent(Activity_AddData.this,Activity_Inventario_Diario.class);
            startActivity(i);
        }

        if (id == R.id.nav_ConsultaInventarioDiario) {
            Intent i = new Intent(Activity_AddData.this,Activity_Consulta_Inventario_Diario.class);
            startActivity(i);
        }

        if (id == R.id.nav_Sincronizar) {
            Intent i = new Intent(Activity_AddData.this,Actividad_Lista_Inventarios.class);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, uriContacto, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        poblarViews(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void insertar(ViewGroup finalContainer) {
        for(int i = 0; i < finalContainer.getChildCount(); i++){
            View view = finalContainer.getChildAt(i);
            String str =finalContainer.getChildAt(i).toString();

            if (view instanceof RelativeLayout) {
                View editText = (View) ((RelativeLayout) view).getChildAt(0);
                EditText editText1 = (EditText) editText;
                str = editText1.getText().toString();
                //Log.d("Entg000000000e", " === " + str);
            }

            String str_EquipoFolio = str;
            Integer int_EquipoRFID = 0;
            Integer int_TipoEquipoId = id_Tipo_Equipo;
            Integer intEquipoAlmacenId = id_Equipo_Almacen;
            Integer int_EquipoEstatusId = 1;
            Integer int_EquipoPropio = 0;
            Integer int_BranchId = id_Branch;
            String str_Almacen = str_Equipo_Almacen;
            String str_Tipo = str_Tipo_Equipo;

            ContentValues valores = new ContentValues();
            valores.clear();
            valores.put(Inventarios.EQUIPO_FOLIO, str_EquipoFolio);
            valores.put(Inventarios.EQUIPO_RFID, int_EquipoRFID);
            valores.put(Inventarios.FK_TIPO_EQUIPO_ID, int_TipoEquipoId);
            valores.put(Inventarios.FK_EQUIPO_ALMACEN_ID, intEquipoAlmacenId);
            valores.put(Inventarios.EQUIPO_ESTATUS_ID, int_EquipoEstatusId);
            valores.put(Inventarios.EQUIPO_PROPIO, int_EquipoPropio);
            valores.put(Inventarios.FK_BRANCH_ID, int_BranchId);
            valores.put(Inventarios.EQUIPO_ALMACEN_STR, str_Almacen);
            valores.put(Inventarios.TIPO_EQUIPO_STR, str_Tipo);
            new TareaAnadirContacto(getContentResolver(), valores).execute(uriContacto);
            finish();
        }
    }

    private void poblarViews(Cursor data) {
        if (!data.moveToNext()) {
            return;
        }
        spn_TipoEquipo.setPrompt(UConsultas.obtenerString(data, Inventarios.FK_TIPO_EQUIPO_ID));
        spn_Almacen.setPrompt(UConsultas.obtenerString(data, Inventarios.FK_EQUIPO_ALMACEN_ID));
    }

    static class TareaAnadirContacto extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;
        private final ContentValues valores;
        public TareaAnadirContacto(ContentResolver resolver, ContentValues valores) {
            this.resolver = resolver;
            this.valores = valores;
        }

        @Override
        protected Void doInBackground(Uri... args) {
            Uri uri = args[0];
            if (null != uri) {
                Cursor c = resolver.query(uri, new String[]{Inventarios.INSERTADO},
                        null, null, null);
                if (c != null && c.moveToNext()) {
                    if (UConsultas.obtenerInt(c, Inventarios.INSERTADO) == 0) {
                        valores.put(Inventarios.MODIFICADO, 1);
                    }
                }
            } else {
                resolver.insert(Inventarios.URI_CONTENIDO, valores);
            }
            return null;
        }
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

    private class TareaWSInsertar extends AsyncTask<String, Integer, Boolean> {
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
                    LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    addView = layoutInflater.inflate(R.layout.listitem_titular, null);
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
                            String[] args = new String[]{str_equipo_folio};
                            db.execSQL("DELETE FROM inventario WHERE equipo_Folio=?", args);
                        } else {
                            val1 = false;
                            if (str_last2.equals("registrado\"")) {
                                val2 = false;
                            } else {
                                val3 = false;
                            }
                        }
                        result = true;
                    } while (c.moveToNext());
                } else {
                    Toast.makeText(getApplicationContext(), "No hay datos", Toast.LENGTH_SHORT);
                }
            } catch (Exception ex) {
                Log.e("ServicioRest", "Error=============>>>>!", ex);
                Log.d("TareaWSInsertar: ", "catch(Exception ex)");
                result = false;
            }
            return result;
        }

        public void showToast(String toast) {
            toast = toast.substring(1, toast.length() - 1);
            final String finalToast = toast;
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Activity_AddData.this, finalToast, Toast.LENGTH_SHORT).show();
                }
            });
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
            }
            if (val1) {
                Toast.makeText(getApplicationContext(), "Todos los datos se sincronizaron de manera correcta", Toast.LENGTH_LONG).show();
            } else {
                if (!val2) {
                    Toast.makeText(getApplicationContext(), "Folios existentes con el mismo branch y usuario", Toast.LENGTH_LONG).show();
                } else {
                    if (!val3) {
                        Toast.makeText(getApplicationContext(), "Ha ocurrido un error, repórtelo con la empresa", Toast.LENGTH_LONG).show();
                    }
                }
            }
            prepararLista();
        }
    }
}
