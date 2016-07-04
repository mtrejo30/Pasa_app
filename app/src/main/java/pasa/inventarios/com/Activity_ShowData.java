package pasa.inventarios.com;

import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Activity_ShowData extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button btnListar;
    private TextView lblResultado;
    private ListView lstClientes;
    boolean result = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__show_data);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        btnListar = (Button)findViewById(R.id.btnListar);

        lblResultado = (TextView)findViewById(R.id.lblResultado);
        lstClientes = (ListView)findViewById(R.id.lstClientes);


        TareaWSInsertar tarea1 = new TareaWSInsertar();
        tarea1.execute();

        btnListar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TareaWSListar tarea = new TareaWSListar();
                tarea.execute();
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

    //Tarea Asíncrona para llamar al WS de inserción en segundo plano
    private class TareaWSInsertar extends AsyncTask<String,Integer,Boolean> {
        public int contador = 0;

        protected Boolean doInBackground(String... params) {
            String message;

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/altaEquipos");

            post.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            //post.setHeader("content-type", "application/json");
            try {
                JSONObject object = new JSONObject();
                object.put("equipoFolio", "Test06");
                object.put("equipoRFID", "");
                object.put("tipoEquipoId", 112);
                object.put("equipoAlmacenId", 5);
                object.put("equipoEstatusId", 1);
                object.put("equipoPropio", 0);
                object.put("branchId", 52);

                message = object.toString();

                post.setEntity(new StringEntity(message, "UTF8"));
                post.setHeader("Content-type", "application/json");
                HttpResponse resp = httpClient.execute(post);

                if (resp != null) {
                    if (resp.getStatusLine().getStatusCode() == 204)
                        result = true;
                }

                Log.d("Status line", "" + message);
                Log.d("Status line", "" + resp.getStatusLine().getStatusCode());
                Log.d("Error: ", "" + resp.getStatusLine().toString());
                Log.d("Error: ", "" + resp.getEntity().getContent());

            } catch (Exception ex) {
                Log.e("ServicioRest", "Error!", ex);
                Log.d("TareaWSInsertar: ", "catch(Exception ex)");
                result = false;
            }
            return result;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.d("Éxito: ", "TareaWSInsertar - protected void onPostExecute()");
                lblResultado.setText("Insertado");
            } else {
                lblResultado.setText("No insertado");
            }
        }
    }
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
                for(int i=0; i<respJSON.length(); i++)	        	{
                    JSONObject obj = respJSON.getJSONObject(i);
                    int idCli = obj.getInt("tipoEquipoId");
                    String nombCli = obj.getString("tipoEquipoClave");
                    String telefCli = obj.getString("tipoEquipoDescripcion");
                    clientes[i] = "" + idCli + "-" + nombCli + "-" + telefCli;

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
                        new ArrayAdapter<String>(Activity_ShowData.this,
                                android.R.layout.simple_list_item_1, clientes);
                lstClientes.setAdapter(adaptador);
            }
        }
    }
}
