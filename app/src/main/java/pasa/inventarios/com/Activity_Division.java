package pasa.inventarios.com;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import pasa.inventarios.com.Contrato.*;
import pasa.inventarios.com.HelperInventarios.*;

public class Activity_Division extends AppCompatActivity {

    // uicontrols
    Spinner spn_Division;
    String arr_Division[] = { ""};
    ArrayAdapter<String> adap_Division;
    String str_Division;

    SimpleCursorAdapter user_SpinnerAdapter;
    DbDataSource dataSource;
    HelperInventarios baseDatos;

    private static Activity_Division instancia = new Activity_Division();

    public Activity_Division obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new HelperInventarios(contexto);
        }
        return instancia;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__division);

        obtenerInstancia(getApplicationContext());

        dataSource = new DbDataSource(this);
        spn_Division = (Spinner) findViewById(R.id.spn_Division);
        adap_Division = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                arr_Division);
        adap_Division.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_Division.setAdapter(adap_Division);

        user_SpinnerAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,//Layout simple
                dataSource.getUser(),//Todos los registros
                new String[]{Contrato.cls_Columnas_Login_User.STR_NAME},//Mostrar solo el nombre
                new int[]{android.R.id.text1},//View para el nombre
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco
        spn_Division.setAdapter(user_SpinnerAdapter);

        spn_Division.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Cursor colCur=(Cursor)spn_Division.getSelectedItem();
                String str_Div = colCur.getString(colCur.getColumnIndex(Contrato.cls_Columnas_Login_User.STR_NAME));
                Log.e("División", "Item==================: "+ str_Div);
                String a = colCur.getString(0);
                String s = colCur.getString(1);
                String d = colCur.getString(2);
                String f = colCur.getString(3);
                String g = colCur.getString(4);
                String h = colCur.getString(5);
                String j = colCur.getString(6);
                str_Division = parent.getItemAtPosition(position).toString();
                Log.e("", "------------------------------>" + a + "-" + s + "-" + d + "-" + f + "-" + g + "-" + h + "-" + j);

                SQLiteDatabase db = baseDatos.getWritableDatabase();
                ContentValues valores = new ContentValues();
                valores.clear();
                db.execSQL("UPDATE "+ Tablas.TBL_LOGIN_USER +" SET int_select= " + 0);
                valores.clear();
                db.execSQL("UPDATE "+ Tablas.TBL_LOGIN_USER +" SET int_select= " + 1 + " WHERE _id= " + a);
                valores.clear();
                //valores.put(cls_Columnas_Login_User.INT_SELECT, 1);
                //db.update(Tablas.TBL_LOGIN_USER, valores, "_id=" + a, null);

                //Log.e("", "================>>>>>>>>>>>>>>" + a + "--" + s + "--" + d + "--" + f + "--" + g + "--" + h + "--" + j);

                if(position == 0){
                    // Showing selected spinner item
                    Toast.makeText(getApplicationContext(),
                            "Selecciona una opción", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "División: " +  str_Div, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Activity_Division.this,Activity_Home.class);
                    startActivity(i);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}