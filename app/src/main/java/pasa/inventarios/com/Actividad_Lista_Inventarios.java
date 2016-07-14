package pasa.inventarios.com;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Entity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
    boolean val1 = true;
    boolean val2 = true;

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
        //preparaButtons();
        btn_Inve1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*  Inicio validacion del internet    */
                if (!estaConectado()) {
                } else {
                    TareaWSInsertar tare = new TareaWSInsertar();
                    tare.execute();
                    lista = new ArrayList<String[]>();
                    for (int i = 0; i < reciclador.getChildCount(); i++) {
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
                /*  Fin validacion del internet    */

            }
        });

        if (reciclador.getChildCount() > 0) {
            Log.e("PreparaBotones", "=========" + reciclador.getChildCount());
            btn_Inve1.setBackgroundColor(Color.parseColor("#60000000"));
            btn_Inve1.setEnabled(false);
        }else {
            btn_Inve1.setBackgroundColor(Color.parseColor("#017A42"));
            btn_Inve1.setEnabled(true);
        }

        getSupportLoaderManager().restartLoader(1, null, this);
        UPreferencias.guardarClaveApi(this, "60d5b4e60cb6a70898f0cd17174e9edd");
    }

    private void prepararLista() {
        reciclador = (RecyclerView) findViewById(R.id.reciclador);
        layoutManager = new LinearLayoutManager(this);
        adaptador = new AdaptadorInventarios(this);
        reciclador.setLayoutManager(layoutManager);
        reciclador.setAdapter(adaptador);
        Log.e("PreparaLista", "=========" + reciclador.getChildCount());

        //preparaButtons();
    }

    /*  Inicio validacion del internet    */
    protected Boolean estaConectado(){
        if(conectadoWifi()){
            return true;
        }else{
            if(conectadoRedMovil()){
                return true;
            }else{
                showAlertDialog(this, "Revisa tu conexión a Internet",
                        "Tu Dispositivo no tiene Conexión a Internet.", false);
                return false;
            }
        }
    }
    /*  Fin validacion del internet    */

    /*  Inicio validacion del internet    */
    protected Boolean conectadoWifi(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
    /*  Fin validacion del internet    */

    /*  Inicio validacion del internet    */
    protected Boolean conectadoRedMovil(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
    /*  Fin validacion del internet    */

    /*  Inicio validacion del internet    */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(title);

        alertDialog.setMessage(message);

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();
    }
    /*  Fin validacion del internet    */

    private void preparaButtons(){
        if (reciclador.getChildCount() > 0) {
            Log.e("PreparaBotones", "=========" + reciclador.getChildCount());
            btn_Inve1.setBackgroundColor(Color.parseColor("#60000000"));
            btn_Inve1.setEnabled(false);
        }else {
            btn_Inve1.setBackgroundColor(Color.parseColor("#017A42"));
            btn_Inve1.setEnabled(true);
        }
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
                    String respuesta =  EntityUtils.toString(resp.getEntity());
                    //showToast(respuesta);
                    String[] str_validar_msj = respuesta.split(" ");
                    String str_last2 = str_validar_msj[str_validar_msj.length - 1];

                    if (str_last2.equals("correctamente\"")) {
                        String[] args = new String[]{splitDat[0].trim()};
                        db.execSQL("DELETE FROM inventario WHERE equipo_Folio=?", args);
                    } else {
                        val1 = false;
                        if (str_last2.equals("registrado\"")) {
                            val2 = false;
                            //Folios existentes con el mismo branch y usuario
                        } /*else {
                                val3 = false;
                                //Ha ocuriido un error en el servidor
                            }*/
                    }

/*
                    String[] args = new String[]{splitDat[0].trim()};
                    db.execSQL("DELETE FROM inventario WHERE equipo_Folio=?", args);
*/

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
                Log.e("onPostExecute", "=========" + reciclador.getChildCount());
                btn_Inve1.setBackgroundColor(Color.parseColor("#60000000"));
                btn_Inve1.setEnabled(false);
            }
            if (val1 == true) {
                Toast.makeText(getApplicationContext(), "Todos los datos se sincronizaron de manera correcta", Toast.LENGTH_SHORT).show();
            } else {
                if (val2 == false) {
                    Toast.makeText(getApplicationContext(), "Folios existentes con el mismo branch y usuario", Toast.LENGTH_SHORT).show();
                }
                /*
                if (!val3) {
                    Toast.makeText(getApplicationContext(), "Ha ocurrido un error en el servidor", Toast.LENGTH_LONG).show();
                }
                */
            }
            prepararLista();
        }
    }
}
