package pasa.inventarios.com;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import pasa.inventarios.com.client.android.CaptureActivity;
import pasa.inventarios.com.Contrato.*;

public class Activity_Inventario_Diario extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    TextView txtView_Fecha;
    TextView txtView_Division;
    Button btn_AddEditText;
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

    String str_branch = "";
    String str_user = "";
    String str_pass = "";
    String str_app = "";
    String str_division = "";
    String str_valida = "";
    String str_bandera = "";

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

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MMMM/yyyy");
        String formattedDate = df.format(c.getTime());
        txtView_Fecha = (TextView) findViewById(R.id.txtView_Fecha);
        txtView_Fecha.setText(formattedDate);

        txtView_Division = (TextView) findViewById(R.id.txtView_Division);

        container = (LinearLayout) findViewById(R.id.lin_AddEditTextEscaner);
        finalContainer = container;

        editText_Barcode = (EditText) findViewById(R.id.editText_BarcodeEscaner);

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
                    LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    addView = layoutInflater.inflate(R.layout.fragment__row2, null);
                    txtViewRow = (TextView) addView.findViewById(R.id.editext_BarCode);
                    txtViewRow.setText(editText_Barcode.getText().toString());
                    finalContainer.addView(addView);
                    editText_Barcode.setText("");
                }
                else{
                    Toast.makeText(getApplicationContext(), "La caja del código de barras esta vacía", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_Save = (Button) findViewById(R.id.btn_Save);
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Log.e("==========>>>>>>>", " Boton Save OnClickListener");
                Log.e("==========>>>>>>>", " Boton Save OnClickListener " + finalContainer.getChildCount());
                for (int i = 0; i < finalContainer.getChildCount(); i++) {
                    View view = finalContainer.getChildAt(i);
                    if (view instanceof RelativeLayout) {
                        View editText = (View) ((RelativeLayout) view).getChildAt(0);
                        EditText editText1 = (EditText) editText;
                        Log.e("", "========>>>>>>>" + editText1);
                    }
                }
                */
                insertar(finalContainer);
                finalContainer.removeAllViews();

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
            Log.e("", "==>>     " + i + "--" + str_division + "--"+ txtView_Fecha.getText().toString() + "--" + str_user + "--" + str_EquipoFolio + "--" + str_branch);

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
        Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_LOGIN_USER+ " where int_select = 1", null);

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
                Log.e("", "==>>     " + str_branch + "--" + str_user + "--" + str_pass + "--" + str_app + "--" + str_division + "--" + str_valida + "--" + str_bandera);

                txtView_Division.setText(str_division);

            } while(c.moveToNext());
        }
    }

    public void mtd_Query_Tbl_Inventario_Diario(){
        Log.e("=========>>>>>>>>", "  Soy el metodo mtd_Query_Tbl_Inventario_Diario");
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
                Log.e("", "==>>     " + str_branch + "--" + str_user + "--" + str_pass + "--" + str_app + "--" + str_division + "--" + str_valida + "--" + str_bandera);
            } while(c.moveToNext());
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_AddEditText){
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }
}
