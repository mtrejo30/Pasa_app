package pasa.inventarios.com;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class Activity_Consulta_Inventario_Diario extends AppCompatActivity implements View.OnClickListener {

    String str_id = "";
    TextView str_divisionTV;
    EditText str_fechaET;
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
    Button btn_delete_item;
    LinearLayout container;
    boolean result = false;
    boolean val1 = true;
    boolean val2 = true;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private AdaptadorInventarios adaptador;

    HelperInventarios baseDatos;

    private static Activity_Consulta_Inventario_Diario instancia = new Activity_Consulta_Inventario_Diario();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

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
        str_divisionTV = (TextView) findViewById(R.id.txtView_Division);

        dateFormatter = new SimpleDateFormat("dd/MMMM/yyyy", Locale.US);
        str_fechaET = (EditText) findViewById(R.id.editText_Fecha);
        str_fechaET.setInputType(InputType.TYPE_NULL);
        str_fechaET.requestFocus();
        str_fechaET.setOnClickListener(this);
        setDateTimeField();
        finalContainer = container;
        if (finalContainer.getChildCount() != 0) {
            finalContainer.removeAllViews();
        }
        mtd_Query_Tbl_Login_User();
        prepararLista();

        btn_Sincronizar = (Button) findViewById(R.id.btn_Sincronizacion);
        btn_Sincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TareaWSInsertar tare = new TareaWSInsertar();
                tare.execute();
                prepararLista();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void mtd_Query_Tbl_Login_User() {
        Log.e("=========>>>>>>>>", "  Soy el metodo mtd_Query_Tbl_Login_User");
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.TBL_LOGIN_USER + " where int_select = 1", null);
        if (c.moveToFirst()) {
            String str_pass = "";
            String str_app = "";
            String str_valida = "";
            String str_bandera = "";
            do {
                str_branch = c.getString(0);
                str_user = c.getString(1);
                str_pass = c.getString(2);
                str_app = c.getString(3);
                str_division = c.getString(4);
                str_valida = c.getString(5);
                str_bandera = c.getString(6);
                Log.e("", "==>>     " + str_branch + "--" + str_user + "--" + str_pass + "--" + str_app + "--" + str_division + "--" + str_valida + "--" + str_bandera);
                str_divisionTV.setText(str_division);
            } while (c.moveToNext());
        }
    }

    private void setDateTimeField() {
        str_fechaET.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(Activity_Consulta_Inventario_Diario.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                str_fechaET.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void prepararLista() {
        Log.e("=========>>>>>>>>", "  Soy el metodo prepararLista - Inventario Diario");
        finalContainer.removeAllViews();
        final SQLiteDatabase db = baseDatos.getWritableDatabase();
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
                btn_delete_item = (Button) addView.findViewById(R.id.btn_delete_item);
                final String bar = txtViewTit.getText().toString();
                btn_delete_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "El código " + bar + " se eliminó correctamente", Toast.LENGTH_SHORT).show();
                        db.execSQL("DELETE FROM " + HelperInventarios.Tablas.TBL_INVENTARIO_DIARIO + " WHERE " + Contrato.cls_Columnas_Inventario_Diario.STR_BARCODE + "=" + bar);
                        prepararLista();
                    }
                });
                finalContainer.addView(addView);
                str_divisionTV.setText(c.getString(1));
            } while (c.moveToNext());
        } else {
            Toast.makeText(getApplicationContext(), "No hay datos", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onClick(View v) {
        fromDatePickerDialog.show();
    }
/*
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Activity_Consulta_Inventario_Diario Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://pasa.inventarios.com/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Activity_Consulta_Inventario_Diario Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://pasa.inventarios.com/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
*/
    private class TareaWSInsertar extends AsyncTask<String, Integer, Boolean> {
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
                    HttpResponse resp;
                    do {
                        str_id = c.getString(0);
                        str_division = c.getString(1);
                        str_fecha = c.getString(2);
                        str_user = c.getString(3);
                        str_barcode = c.getString(4);
                        str_branch = c.getString(5);
                        Log.e("Muestra", "==>>     " + str_id + "--" + str_division + "--" + str_fecha + "--" + str_user + "--" + str_barcode + "--" + str_branch + "--" + c.getCount());
                        ////////////////////////////////////////////////
                        object.put("equipoFolio", str_barcode.trim());
                        object.put("branchId", str_branch.trim());
                        object.put("user", str_user.trim());
                        message = object.toString();
                        post.setEntity(new StringEntity(message, "UTF8"));
                        post.setHeader("Content-type", "application/json");
                        resp = httpClient.execute(post);
                        if (resp != null) {
                            if (resp.getStatusLine().getStatusCode() == 204) {
                                result = true;
                                Log.e("=====>>>>>", " Soy IF ");
                            } else {
                                Log.e("=====>>>>>", " Soy ELSE " + resp.getStatusLine().getStatusCode());
                            }
                        }
                        String respuesta = "Respuesta default";
                        //assert resp != null;
                        respuesta = EntityUtils.toString(resp.getEntity());
                        //showToast(respuesta);
                        String[] str_validar_msj = respuesta.split(" ");
                        String str_last1 = str_validar_msj[0] + " " + str_validar_msj[1] + " " + str_validar_msj[2];
                        String str_last2 = str_validar_msj[str_validar_msj.length - 1];
                        Log.d("", " =====>>>>> :\"" + str_last1 + "\"");
                        Log.d("", " =====>>>>> :\"" + str_last2 + "\"");
                        if (str_last1.equals("\"Se registro Correctamente")) {
                            String[] args = new String[]{str_id};
                            db.execSQL("DELETE FROM tbl_inventario_diario WHERE _id=?", args);
                        } else {
                            val1 = false;
                            if (str_last2.equals("Existente\"")) {
                                val2 = false;
                                //Folios existentes con el mismo branch y usuario
                            } /*else {
                                val3 = false;
                                //Ha ocuriido un error en el servidor
                            }*/
                        }

                        Log.e("getEntity.getContent", "=====>>>>> " + respuesta);
                        Log.e("Status", "=====>>>>> " + resp.getStatusLine().getStatusCode());
                        Log.e("Status message", "=====>>>>>" + message);
                        Log.e("resp.getStatusLine", "=====>>>>>" + resp.getStatusLine().toString());
                        result = true;
                    } while (c.moveToNext());
                } else {
                    Toast.makeText(getApplicationContext(), "No hay datos", Toast.LENGTH_SHORT);
                }
            } catch (Exception ex) {
                Log.e("ServicioRest", "Error=============>>>>!", ex);
                Log.d("TareaWSInsertar: ", "catch(Exception ex)");
                result = false;
            }
            return result;
        }

        public void showToast(String toast) {
            toast = toast.substring(1, toast.length() - 1);
            final String finalToast = toast;
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Activity_Consulta_Inventario_Diario.this, finalToast, Toast.LENGTH_SHORT).show();
                }
            });
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.e("onPostExecute", "=========" + finalContainer.getChildCount());
                btn_Sincronizar.setBackgroundColor(Color.parseColor("#60000000"));
                btn_Sincronizar.setEnabled(false);
            }
            //Toast.makeText(getApplicationContext(), "Entré al onPostExecute", Toast.LENGTH_SHORT).show();
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
