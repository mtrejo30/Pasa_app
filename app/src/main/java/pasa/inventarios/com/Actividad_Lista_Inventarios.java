package pasa.inventarios.com;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import java.util.ArrayList;
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

    ArrayList lista;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad__lista__inventarios);

        // Preparar elementos UI
        // Agregar toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.titulo_actividad_actividad_listar);

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
                //tare.setReciclador(reciclador);
                tare.execute();

                lista  = new ArrayList<String[]>();
                for (int i = 0; i < reciclador.getChildCount(); i++){
                    View view = reciclador.getChildAt(i);
                    String str = "";
                    TextView editText1 = (TextView) ((TextView) view);
                    str = editText1.getText().toString();
                    String[] splitDat = str.split("-");
                    Log.d("Entg000000000e", " === " + splitDat[0] + "----" + splitDat[1]);
                    lista.add(splitDat);
                    //}
                    Log.d("======>>>>>>", "String:::" + "" + " ======>>>>> View:::" + lista.size());

                }
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

        /*private RecyclerView reciclador;

        public RecyclerView getReciclador() {
            return reciclador;
        }

        public void setReciclador(RecyclerView reciclador) {
            this.reciclador = reciclador;
        }*/

        protected Boolean doInBackground(String... params) {
            String message;

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/altaEquipos");

            post.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            //post.setHeader("content-type", "application/json");
            try {
                int tamaño = lista.size();
                Log.d("ggggg","" + tamaño);




                JSONObject object = new JSONObject();
                for (int i = 0; i < tamaño; i++){
                    String[] splitDat = (String[]) lista.get(i);
                    Log.d("Subir informacion", " === " + splitDat[0] + "----" + splitDat[1]);
                    object.put("equipoFolio", splitDat[0].trim());
                    object.put("equipoRFID", "");
                    object.put("tipoEquipoId", splitDat[2].trim());
                    object.put("equipoAlmacenId", splitDat[3].trim());
                    object.put("equipoEstatusId", splitDat[4].trim());
                    object.put("equipoPropio", splitDat[5].trim());
                    object.put("branchId", splitDat[6].trim());
                    message = object.toString();
                    post.setEntity(new StringEntity(message, "UTF8"));
                    post.setHeader("Content-type", "application/json");
                    HttpResponse resp = httpClient.execute(post);
                    //ResponseHandler<String> handler = new BasicResponseHandler();
                    //String Body = httpClient.execute(post, handler);
                    if (resp != null) {
                        if (resp.getStatusLine().getStatusCode() == 204)
                            result = true;
                    }
                    Log.d("Status line", "" + resp);
                    Log.d("Status line", "" + resp.getStatusLine().getStatusCode());
                    Log.d(": ", "" + resp.getStatusLine().toString());
                    Log.d(": ", "" + resp.getStatusLine().getStatusCode());
                    Log.d(": ", "" + resp.getEntity().getContent());
                    Log.d("Ins", "==============");

                }
                //for (int i =0; i < adaptador.getItemCount(); i++) {
                //}
            } catch (Exception ex) {
                Log.e("ServicioRest", "Error=============>>>>!", ex);
                Log.d("TareaWSInsertar: ", "catch(Exception ex)");
                result = false;
            }
            return result;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.d("Éxito: ", "==================== Se insertó");
            }
        }
    }
}
