package pasa.inventarios.com;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Adapter;
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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import pasa.inventarios.com.Contrato.Inventarios;
import pasa.inventarios.com.HelperInventarios.*;
import pasa.inventarios.com.client.android.CaptureActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity_AddData extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
                                            LoaderManager.LoaderCallbacks<Cursor>   {
    Spinner spn_TipoEquipo;
    Spinner spn_Almacen;
    Button btn_Escanear;
    Button btn_Save;
    Button btn_Add;
    int id_Tipo_Equipo;
    int id_Equipo_Almacen;
    String str_Tipo_Equipo = "";
    String str_Equipo_Almacen = "";
    int id_Branch;
    int contador = 0;
    TextView textOut;
    HelperInventarios baseDatos;
    final int[] cont = {0};
    EditText txt_Cantidad;
    LinearLayout container;
    View addView;
    ViewGroup finalContainer;
    SimpleCursorAdapter genreSpinnerAdapter1;
    SimpleCursorAdapter genreSpinnerAdapter2;
    DbDataSource dataSource;
    private static Activity_AddData instancia = new Activity_AddData();
    public static final String URI_CONTACTO = "extra.uriContacto";
    private Uri uriContacto;

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
            //Log.e("Codigo==========>", " -RESULtttttttttt===============>>> " + _Codigo);

            View view = finalContainer.getChildAt(cont[0] - 1);
            String str = "";
            if (view instanceof RelativeLayout) {
                View editText = (View) ((RelativeLayout) view).getChildAt(0);
                EditText editText1 = (EditText) editText;
                editText1.setText(_Codigo);
                if(_Codigo == "")
                {
                    contador = contador + 1;
                }
                //Log.e("", " =======>>>>>>> " + editText1 + " =======>>>>>>" + contador);
            }

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

        String uri = getIntent().getStringExtra(URI_CONTACTO);
        if (uri != null) {
            uriContacto = Uri.parse(uri);
            getSupportLoaderManager().restartLoader(1, null, this);
        }

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
        txt_Cantidad = (EditText) findViewById(R.id.edit_Cantidad);
        container = (LinearLayout) findViewById(R.id.lin_AddEditTextEscaner);
        finalContainer = container;
        btn_Add = (Button) findViewById(R.id.btn_AgregarCajas);
        btn_Add.setEnabled(true);
        /*btn_Add.setOnClickListener(this);*/
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
        });
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
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_perfil) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
}
