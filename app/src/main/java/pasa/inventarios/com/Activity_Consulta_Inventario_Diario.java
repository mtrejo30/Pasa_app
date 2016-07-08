package pasa.inventarios.com;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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


public class Activity_Consulta_Inventario_Diario extends AppCompatActivity {

    String str_id = "";
    String str_division = "";
    String str_fecha = "";
    String str_user = "";
    String str_barcode = "";
    String str_branch = "";
    View addView;
    TextView txtViewTit;
    TextView txtViewSub;
    ViewGroup finalContainer = null;
    Button btn_Sincronizar;
    LinearLayout container;
    boolean result = false;
    private AdaptadorInventarios adaptador;

    HelperInventarios baseDatos;

    private static Activity_Consulta_Inventario_Diario instancia = new Activity_Consulta_Inventario_Diario();

    public Activity_Consulta_Inventario_Diario() {
    }
    public Activity_Consulta_Inventario_Diario obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new HelperInventarios(contexto);
        }
        return instancia;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__consulta__inventario__diario);
        obtenerInstancia(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.titulo_actividad_actividad_listar);
        container = (LinearLayout) findViewById(R.id.lin_AddEditTextEscaner);
        finalContainer = container;
        if(finalContainer.getChildCount()!= 0) {
            finalContainer.removeAllViews();
        }
        prepararLista();
        btn_Sincronizar = (Button) findViewById(R.id.btn_Sincronizacion);
        btn_Sincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TareaWSInsertar tare = new TareaWSInsertar();
                tare.execute();
            }
        });
    }

    private void prepararLista() {
        Log.e("=========>>>>>>>>", "  Soy el metodo prepararLista - Inventario Diario");
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO, null);
        if (c.moveToFirst()) {
            do {
                str_id = c.getString(0);
                str_division = c.getString(1);
                str_fecha = c.getString(2);
                str_user = c.getString(3);
                str_barcode = c.getString(4);
                str_branch = c.getString(5);
                Log.e("", "==>>     " + str_id + "--" + str_division + "--" + str_fecha + "--" + str_user + "--" + str_barcode + "--" + str_branch);
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                addView = layoutInflater.inflate(R.layout.listitem_titular, null);
                txtViewTit = (TextView) addView.findViewById(R.id.LblTitulo);
                txtViewTit.setText(str_barcode);
                txtViewSub = (TextView) addView.findViewById(R.id.LblSubTitulo);
                txtViewSub.setText(str_user);
                finalContainer.addView(addView);
            } while (c.moveToNext());
        } else {
            Toast.makeText(getApplicationContext(), "No hay datos", Toast.LENGTH_SHORT);
        }
    }

    private class TareaWSInsertar extends AsyncTask<String,Integer,Boolean> {
        protected Boolean doInBackground(String... params) {
            String message;
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/altaEquiposInsertReporte");
            post.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            try {

                Log.e("=========>>>>>>>>", "  Soy el metodo TareaWSInsertar - Inventario Diario");
                SQLiteDatabase db = baseDatos.getWritableDatabase();
                Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO, null);
                if (c.moveToFirst()) {
                    LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    addView = layoutInflater.inflate(R.layout.listitem_titular, null);
                    JSONObject object = new JSONObject();
                    ContentValues valores = new ContentValues();
                    do {
                        str_id = c.getString(0);
                        str_division = c.getString(1);
                        str_fecha = c.getString(2);
                        str_user = c.getString(3);
                        str_barcode = c.getString(4);
                        str_branch = c.getString(5);
                        Log.e("", "==>>     " + str_id + "--" + str_division + "--" + str_fecha + "--" + str_user + "--" + str_barcode + "--" + str_branch + "--" + c.getCount());
                        ////////////////////////////////////////////////
                            object.put("equipoFolio", str_barcode);
                            object.put("branchId", str_branch);
                            object.put("user", str_user);
                            message = object.toString();
                            post.setEntity(new StringEntity(message, "UTF8"));
                            post.setHeader("Content-type", "application/json");
                            HttpResponse resp = httpClient.execute(post);
                            if (resp != null) {
                                if (resp.getStatusLine().getStatusCode() == 204)
                                    result = true;
                            }
                            String[] args = new String[]{str_id};
                            db.execSQL("DELETE FROM tbl_inventario_diario WHERE _id=?", args);
                            String respuesta =  EntityUtils.toString(resp.getEntity());
                            showToast(respuesta);
                        result = true;
                        ////////////////////////////////////////////////
                    } while (c.moveToNext());
                } else {
                    Toast.makeText(getApplicationContext(), "No hay datos", Toast.LENGTH_SHORT);
                }
            } catch (Exception ex) {
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
                    Toast.makeText(Activity_Consulta_Inventario_Diario.this, finalToast, Toast.LENGTH_SHORT).show();
                }
            });

        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.e("onPostExecute", "=========" + finalContainer.getChildCount());
                btn_Sincronizar.setBackgroundColor(Color.parseColor("#60000000"));
                btn_Sincronizar.setEnabled(false);
                prepararLista();
            }
            prepararLista();
            finalContainer.removeAllViews();
        }
    }

}
