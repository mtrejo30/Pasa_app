package pasa.inventarios.com;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.widget.Adapter;
import android.widget.AdapterView;
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

import java.util.ArrayList;
import java.util.List;

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
    int id_Branch;

    final int[] cont = {0};
    EditText txt_Cantidad;
    LinearLayout container;
    View addView;

    SimpleCursorAdapter genreSpinnerAdapter1;
    SimpleCursorAdapter genreSpinnerAdapter2;

    DbDataSource dataSource;

    public static final String URI_CONTACTO = "extra.uriContacto";

    private Uri uriContacto;

    public Activity_AddData() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__add__data);


        dataSource = new DbDataSource(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String uri = getIntent().getStringExtra(URI_CONTACTO);
        if (uri != null) {
            uriContacto = Uri.parse(uri);
            getSupportLoaderManager().restartLoader(1, null, this);
        }
        // /////////////////////////////////////////////////////////////////
        spn_TipoEquipo = (Spinner) findViewById(R.id.spn_TipoEquipo);
        genreSpinnerAdapter1 = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,//Layout simple
                dataSource.getCatTipEquipo(),//Todos los registros
                new String[]{Contrato.cls_Columnas_Catalogo_Tipo_Equipo.STR_TIPO_EQUIPO_DESCRIPCION},//Mostrar solo el nombre
                new int[]{android.R.id.text1},//View para el nombre
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco
        spn_TipoEquipo.setAdapter(genreSpinnerAdapter1);
        spn_TipoEquipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                id_Tipo_Equipo = (int) id;
                Toast.makeText(getApplicationContext(),
                        "Id-TipoEquipo: ===>>>" + id_Tipo_Equipo, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // //////////////////////////////////////////////////////////////////
        spn_Almacen = (Spinner) findViewById(R.id.spn_Almacen);
        genreSpinnerAdapter2 = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,//Layout simple
                dataSource.getCatAlmacenes(),//Todos los registros
                new String[]{Contrato.cls_Columnas_Catalogo_Almacenes.STR_EQUIPO_ALMACEN_DESCRIPCION},//Mostrar solo el nombre
                new int[]{android.R.id.text1},//View para el nombre
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco
        spn_Almacen.setAdapter(genreSpinnerAdapter2);
        spn_Almacen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                id_Equipo_Almacen = (int) id;
                Toast.makeText(getApplicationContext(),
                        "Id-Almacen: ===>>>" + id_Equipo_Almacen + " --- ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // //////////////////////////////////////////////////////////////////

        txt_Cantidad = (EditText) findViewById(R.id.edit_Cantidad);

        container = (LinearLayout) findViewById(R.id.lin_AddEditTextEscaner);
        final ViewGroup finalContainer = container;

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
                        TextView textOut = (TextView) addView.findViewById(R.id.editext_BarCode);
                        finalContainer.addView(addView);
                    }
                    btn_Escanear.setEnabled(true);
                    btn_Escanear.setBackgroundColor(Color.parseColor("#017A42"));

                } else {
                    Toast.makeText(v.getContext(),
                            "Introduce un Número", Toast.LENGTH_SHORT).show();
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
                insertar(finalContainer);
                btn_Save.setEnabled(false);
                btn_Save.setBackgroundColor(Color.parseColor("#60000000"));
                finalContainer.removeAllViews();
                cont[0] = 0;
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

    private void eliminar() {
        if (uriContacto != null) {
            new TareaEliminarContacto(getContentResolver()).execute(uriContacto);
            finish();
        }
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

        if (id == R.id.nav_datos) {
        }
        else if (id == R.id.nav_perfil) {
        }
        else if (id == R.id.nav_salir) {
            System.exit(0);
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
         ArrayList array = getAllChildren(finalContainer);

        for(int i = 0; i < finalContainer.getChildCount(); i++){
            Log.d("===========", array.get(i).toString());
            String str_EquipoFolio = finalContainer.getChildAt(i).toString();
            Integer int_EquipoRFID = 0;
            Integer int_TipoEquipoId = id_Tipo_Equipo;
            Integer intEquipoAlmacenId = id_Equipo_Almacen;
            Integer int_EquipoEstatusId = 1;
            Integer int_EquipoPropio = 0;
            Integer int_BranchId = 52;

            ContentValues valores = new ContentValues();
            valores.clear();
            valores.put(Inventarios.EQUIPO_FOLIO, str_EquipoFolio);
            valores.put(Inventarios.EQUIPO_RFID, int_EquipoRFID);
            valores.put(Inventarios.FK_TIPO_EQUIPO_ID, int_TipoEquipoId);
            valores.put(Inventarios.FK_EQUIPO_ALMACEN_ID, intEquipoAlmacenId);
            valores.put(Inventarios.EQUIPO_ESTATUS_ID, int_EquipoEstatusId);
            valores.put(Inventarios.EQUIPO_PROPIO, int_EquipoPropio);
            valores.put(Inventarios.FK_BRANCH_ID, int_BranchId);
            new TareaAnadirContacto(getContentResolver(), valores).execute(uriContacto);
            finish();
        }
    }
    private ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }
    private boolean esNombreValido(String nombre) {
        return !TextUtils.isEmpty(nombre);
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
                Cursor c = resolver.query(uri, new String[]{Inventarios.INSERTADO}, null, null, null);
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
    static class TareaEliminarContacto extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;
        public TareaEliminarContacto(ContentResolver resolver) {
            this.resolver = resolver;
        }
        @Override
        protected Void doInBackground(Uri... args) {
        /*
        Verificación: Si el registro no ha sido sincronizado aún, entonces puede eliminarse
        directamente. De lo contrario se marca como 'eliminado' = 1
         */
            Cursor c = resolver.query(args[0], new String[]{Inventarios.INSERTADO}
                    , null, null, null);
            int insertado;
            if (c != null && c.moveToNext()) {
                insertado = UConsultas.obtenerInt(c, Inventarios.INSERTADO);
            } else {
                return null;
            }
            if (insertado == 1) {
                resolver.delete(args[0], null, null);
            } else if (insertado == 0) {
                ContentValues valores = new ContentValues();
                valores.put(Inventarios.ELIMINADO, 1);
                resolver.update(args[0], valores, null, null);
            }
            return null;
        }
    }
}
