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
                Log.i("División", "Item==================: "+ str_Div);
                str_Division = parent.getItemAtPosition(position).toString();
                Log.i("", "---->" + colCur.getString(0) + "-" + colCur.getString(1)
                        + "-" + colCur.getString(2) + "-" + colCur.getString(3) + "-" + colCur.getString(4)
                        + "-" + colCur.getString(5) + "-" + colCur.getString(6));
                SQLiteDatabase db = baseDatos.getWritableDatabase();
                ContentValues valores = new ContentValues();
                valores.clear();
                db.execSQL("UPDATE "+ Tablas.TBL_LOGIN_USER +" SET int_select= " + 0);
                valores.clear();
                db.execSQL("UPDATE "+ Tablas.TBL_LOGIN_USER +" SET int_select= " + 1 + " WHERE _id= " + colCur.getString(0));
                valores.clear();
                if(position == 0){
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