package pasa.inventarios.com;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.util.Random;

import pasa.inventarios.com.Contrato.Inventarios;

public class Actividad_Lista_Inventarios extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
                                        AdaptadorInventarios.OnItemClickListener{

    private static final String TAG = Actividad_Lista_Inventarios.class.getSimpleName();

    // Referencias UI
    private RecyclerView reciclador;
    private LinearLayoutManager layoutManager;
    private AdaptadorInventarios adaptador;
    boolean result = false;

    Button btn_Inve1;
    Button btn_User1;
    Button btn_Cat_Almac1;
    Button btn_Tip_Equip1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad__lista__inventarios);

        // Preparar elementos UI
        // Agregar toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.titulo_actividad_actividad_contactos);

                prepararLista();
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Acciones
                Intent intent = new Intent(Actividad_Lista_Inventarios.this, Activity_AddData.class);
                startActivity(intent);
            }
        });
        */
        btn_Inve1 = (Button) findViewById(R.id.btn_Inve);
        btn_Inve1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TareaWSInsertar tare = new TareaWSInsertar();
                tare.execute();

            }
        });

        getSupportLoaderManager().restartLoader(1, null, this);

        // Reemplaza con tu clave
        UPreferencias.guardarClaveApi(this, "60d5b4e60cb6a70898f0cd17174e9edd");

    }

    private void prepararLista() {
        reciclador = (RecyclerView) findViewById(R.id.reciclador);
        layoutManager = new LinearLayoutManager(this);
        adaptador = new AdaptadorInventarios(this);

        reciclador.setLayoutManager(layoutManager);
        reciclador.setAdapter(adaptador);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                Inventarios.URI_CONTENIDO,
                null, Inventarios.ELIMINADO + "=?", new String[]{"0"}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adaptador.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adaptador.swapCursor(null);

    }
    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_insercion_contacto, menu);
            return true;
        }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    void mostrarDetalles(Uri uri) {
        Intent intent = new Intent(this, Activity_AddData.class);
        if (null != uri) {
            intent.putExtra(Activity_AddData.URI_CONTACTO, uri.toString());
        }
        startActivity(intent);
    }
    @Override
    public void onClick(AdaptadorInventarios.ViewHolder holder, String idContacto) {
        mostrarDetalles(Contrato.Inventarios.construirUriContacto(idContacto));

    }

    //Tarea Asíncrona para llamar al WS de inserción en segundo plano
    private class TareaWSInsertar extends AsyncTask<String,Integer,Boolean> {
        public int contador = 0;

        protected Boolean doInBackground(String... params) {
            String message;

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/altaEquipos");

            post.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            post.setHeader("content-type", "application/json");
            try {
                for (int i =0; i < adaptador.getItemCount(); i++) {
                    Random rand = new Random();
                    int n = rand.nextInt(999999);
                    int nn = rand.nextInt(999999);
                    int nnn = rand.nextInt(999999);
                    String hh = ""+n;
                    JSONObject object = new JSONObject();
                    Log.d("", "========>>>>>>>>>>>>>>>>>>>>" + hh);
                    object.put("equipoFolio", adaptador.getItemId(i));
                    object.put("equipoRFID", "");
                    object.put("tipoEquipoId", nn);
                    object.put("equipoAlmacenId", nnn);
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
                    Log.d("Ins", "==============" + i);

                }
            } catch (Exception ex) {
                Log.e("ServicioRest", "Error=============>>>>!", ex);
                Log.d("TareaWSInsertar: ", "catch(Exception ex)");
                result = false;
            }
            return result;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.d("Éxito: ", "TareaWSInsertar - protected void onPostExecute()");
            }
        }
    }
}
