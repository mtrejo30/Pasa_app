package pasa.inventarios.com;

import android.content.ContentValues;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
    HelperInventarios baseDatos;
    boolean result = false;

    Button btn_Inve1;

    ArrayList lista;

    private static Activity_Login instancia = new Activity_Login();
    public Activity_Login obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new HelperInventarios(contexto);
        }
        return instancia;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad__lista__inventarios);

        obtenerInstancia(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.titulo_actividad_actividad_listar);

        prepararLista();

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

                    Log.d("======>>>>>>", "String:::" + "" + " ======>>>>> View:::" + lista.size());

                }
            }
        });

        getSupportLoaderManager().restartLoader(1, null, this);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

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

    private class TareaWSInsertar extends AsyncTask<String,Integer,Boolean> {
        public int contador = 0;

        protected Boolean doInBackground(String... params) {
            String message;

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/altaEquipos");

            post.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            //post.setHeader("content-type", "application/json");
            try {
                int tamaño = lista.size();
                JSONObject object = new JSONObject();
                SQLiteDatabase db = baseDatos.getWritableDatabase();
                ContentValues valores = new ContentValues();
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
                    if (resp != null) {
                        if (resp.getStatusLine().getStatusCode() == 204)
                            result = true;
                    }
                    String[] args = new String[]{splitDat[0].trim()};
                    db.execSQL("DELETE FROM inventario WHERE equipo_Folio=?", args);

                    String respuesta =  EntityUtils.toString(resp.getEntity());
                    showToast(respuesta);

                    result = true;
                }

            } catch (Exception ex) {
                Log.e("ServicioRest", "Error=============>>>>!", ex);
                Log.d("TareaWSInsertar: ", "catch(Exception ex)");
                result = false;
            }
            return result;
        }

        public void showToast(String toast)
        {
            toast = toast.substring(1, toast.length()-1);
            final String finalToast = toast;
            runOnUiThread(new Runnable() {
                public void run()
                {
                    Toast.makeText(Actividad_Lista_Inventarios.this, finalToast, Toast.LENGTH_SHORT).show();
                }
            });
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                prepararLista();
            }
        }
    }
}
