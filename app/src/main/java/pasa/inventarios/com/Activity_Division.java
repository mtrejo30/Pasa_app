package pasa.inventarios.com;

import android.content.Intent;
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

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.OnItemSelected;

public class Activity_Division extends AppCompatActivity {

    // uicontrols
    Spinner spn_Division;
    String arr_Division[] = { ""};
    ArrayAdapter<String> adap_Division;
    String str_Division;

    SimpleCursorAdapter user_SpinnerAdapter;
    DbDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__division);

        dataSource = new DbDataSource(this);

        /*Intent intent = getIntent();
        String user = intent.getStringExtra("par_User");
        String pass = intent.getStringExtra("par_Pass");
        String branch = intent.getStringExtra("par_Branch");
        String name = intent.getStringExtra("par_Name");
        String valida = intent.getStringExtra("par_Valida");*/
        /*Toast.makeText(getApplicationContext(),
                "Us-" + user +":Pass-" + pass +":Bran-" + branch +":Nam-" + name +":Val-" + valida,
                                                                    Toast.LENGTH_SHORT).show();*/

        spn_Division = (Spinner) findViewById(R.id.spn_Division);
/*
        TareaWSListar tarea1 = new TareaWSListar();
        tarea1.execute();
*/
        // Initialize and set Adapter
        adap_Division = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                arr_Division);
        adap_Division.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_Division.setAdapter(adap_Division);


        /*
        Creando Adaptador para GenreSpinner
         */

        user_SpinnerAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,//Layout simple
                dataSource.getUser(),//Todos los registros
                new String[]{Contrato.cls_Columnas_Login_User.STR_NAME},//Mostrar solo el nombre
                new int[]{android.R.id.text1},//View para el nombre
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//Observer para el refresco
        spn_Division.setAdapter(user_SpinnerAdapter);


        // Business Type Item Selected Listener
        spn_Division.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Cursor colCur=(Cursor)spn_Division.getSelectedItem();
                String str_Div = colCur.getString(colCur.getColumnIndex(Contrato.cls_Columnas_Login_User.STR_NAME));
                Log.e("División", "Item==================: "+ str_Div);

                String col=colCur.getString(0);
                String col0=colCur.getString(1);
                String col1=colCur.getString(2);
                String col2=colCur.getString(3);
                String col3=colCur.getString(4);
                String col4=colCur.getString(5);
                str_Division = parent.getItemAtPosition(position).toString();
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
/*
    //Tarea Asíncrona para llamar al WS de listado en segundo plano
    private class TareaWSListar extends AsyncTask<String,Integer,Boolean> {
        private String[] clientes;

        protected Boolean doInBackground(String... params) {
            boolean resul = true;

            DefaultHttpClient httpClient = new DefaultHttpClient();

            HttpGet del =
                    new HttpGet("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/getCatalogoTipoEquipo");
            del.addHeader(BasicScheme.authenticate( new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            del.setHeader("content-type", "application/json");

            try
            {
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());
                JSONArray respJSON = new JSONArray(respStr);
                clientes = new String[respJSON.length()];
                for(int i=0; i<respJSON.length(); i++)               {
                    JSONObject obj = respJSON.getJSONObject(i);
                    int idCli = obj.getInt("tipoEquipoId");
                    String nombCli = obj.getString("tipoEquipoClave");
                    String telefCli = obj.getString("tipoEquipoDescripcion");
                    clientes[i] = telefCli;

                    Log.d("TareaWSListar", "Metodo - for: " + i);
                }

            }
            catch(Exception ex)
            {
                Log.e("ServicioRest","Error!", ex);
                resul = false;
            }
            return resul;
        }
        protected void onPostExecute(Boolean result) {
            if (result)
            {
                //Rellenamos la lista con los nombres de los clientes
                //Rellenamos la lista con los resultados
                ArrayAdapter<String> adaptador =
                        new ArrayAdapter<String>(Activity_Division.this,
                                android.R.layout.simple_list_item_1, clientes);
                spn_Division.setAdapter(adaptador);
            }
        }
    }*/
}