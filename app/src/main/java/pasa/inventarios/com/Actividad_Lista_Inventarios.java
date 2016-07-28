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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
        implements View.OnClickListener {

    private static final String TAG = Actividad_Lista_Inventarios.class.getSimpleName();
    // Referencias UI

    private RecyclerView reciclador;
    private LinearLayoutManager layoutManager;
    private AdaptadorInventarios adaptador;
    HelperInventarios baseDatos;
    Button btn_Inve1;
    ArrayList lista;
    String str_id = "";
    TextView str_divisionTV;
    EditText str_fechaET;
    String str_division = "";
    String str_fecha = "";
    String str_user = "";
    String str_barcode = "";
    String str_branch = "";
    Button btn_delete_item;
    ImageButton btn_search;
    Button btn_show_all;
    LinearLayout container;
    boolean result = false;
    boolean val1 = true;
    boolean val2 = true;
    boolean val3 = true;
    View addView;
    TextView txtViewTit;
    TextView txtViewSub;
    ViewGroup finalContainer = null;
    DbDataSource dataSource;

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
        container = (LinearLayout) findViewById(R.id.lin_AddEditTextEscaner);
        finalContainer = container;


        dataSource = new DbDataSource(this);






        btn_Inve1 = (Button) findViewById(R.id.btn_Inve);
        //preparaButtons();
        btn_Inve1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*  Inicio validacion del internet    */
                if (!estaConectado()) {
                } else {
                    //TareaWSInsertar tare = new TareaWSInsertar();
                    //tare.execute();

                    mtd_dialog_alert();

                    prepararLista();




                    /*lista = new ArrayList<String[]>();
                    for (int i = 0; i < reciclador.getChildCount(); i++) {
                        View view = reciclador.getChildAt(i);
                        String str = "";
                        TextView editText1 = (TextView) ((TextView) view);
                        str = editText1.getText().toString();
                        String[] splitDat = str.split("-");
                        Log.d("Entg000000000e", " === " + splitDat[0] + "----" + splitDat[1]);
                        lista.add(splitDat);
                        Log.d("======>>>>>>", "String:::" + "" + " ======>>>>> View:::" + lista.size());
                    }*/

                }
                /*  Fin validacion del internet    */

            }
        });
/*
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
*/
        prepararLista();
    }

    public void mtd_dialog_alert(){
        final android.support.v7.app.AlertDialog.Builder dialogo1 = new android.support.v7.app.AlertDialog.Builder(this);
        dialogo1.setTitle("Advertencia... ");
        dialogo1.setMessage("El folio: ya está cerrado, elije otro");
        dialogo1.setIcon(R.drawable.ic_information_black_18dp);
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                mtd_dialog_elije();
            }
        });
        dialogo1.show();

    }

    public void mtd_dialog_elije(){

        int i = 0;
        final SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("select vch_folio_inventario_diario from " + HelperInventarios.Tablas.TBL_CATALOGO_ALMACENES + " where vch_folio_inventario_diario <> '' and vch_folio_inventario_diario <> 'INV/EQUIP-000010-072016'", null);
        final String[] items = new String[c.getCount()];
        if(c.moveToFirst()){
            do {
                items[i] = c.getString(0);
                Log.e("", items[i]);
                i++;
            }while (c.moveToNext());
        }
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(Actividad_Lista_Inventarios.this);
        builder.setTitle("Elije el nuevo folio:");
        //builder.setIcon(R.drawable.ic_information_black_18dp);
        //builder.setMessage("El folio: " + "INV/EQUIP-000010-072016" + " ya está cerrado, Elije otro");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Toast.makeText(getApplicationContext(), "El nuevo folio es: \n" + items[item], Toast.LENGTH_LONG).show();
            }
        });
        builder.show();

    }



    private void prepararLista() {
        finalContainer.removeAllViews();
        final SQLiteDatabase db = baseDatos.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from " + HelperInventarios.Tablas.INVENTARIO, null);
        if (c.moveToFirst()) {
            do {
                str_id = c.getString(0);
                str_division = c.getString(1);
                str_fecha = c.getString(2);
                str_user = c.getString(3);
                str_barcode = c.getString(4);
                str_branch = c.getString(5);
                //Log.e("", "==>>     " + str_id + "--" + str_division + "--" + str_fecha + "--" + str_user + "--" + str_barcode + "--" + str_branch);
                Log.e("", "==>>     " + c.getString(0) + "--" + c.getString(1) + "--" + c.getString(2) + "--" + c.getString(3) + "--" + c.getString(4) + "--" + c.getString(4) + "--" + c.getString(5) + "--" + c.getString(6) + "--" + c.getString(7) + "--" + c.getString(8) + "--" + c.getString(9) + "--");

                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                addView = layoutInflater.inflate(R.layout.listitem_titular, null);
                txtViewTit = (TextView) addView.findViewById(R.id.LblTitulo);
                txtViewTit.setText(str_division);
                txtViewSub = (TextView) addView.findViewById(R.id.LblSubTitulo);
                txtViewSub.setText(str_id);
                btn_delete_item = (Button) addView.findViewById(R.id.btn_delete_item);
                final String bar = txtViewTit.getText().toString();
                btn_delete_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "El código " + bar + " se eliminó correctamente", Toast.LENGTH_SHORT).show();
                        db.execSQL("DELETE FROM " + HelperInventarios.Tablas.INVENTARIO + " WHERE " + Inventarios.EQUIPO_FOLIO + " = '" + bar + "'");
                        prepararLista();
                    }
                });
                finalContainer.addView(addView);
                //str_divisionTV.setText(c.getString(1));
            } while (c.moveToNext());

            btn_Inve1.setBackgroundColor(Color.parseColor("#017A42"));
            btn_Inve1.setEnabled(true);
        } else {
            btn_Inve1.setBackgroundColor(Color.parseColor("#60000000"));
            btn_Inve1.setEnabled(false);
            Toast.makeText(getApplicationContext(), "No hay datos", Toast.LENGTH_SHORT).show();
        }
    }

/*
    private void prepararLista() {
        reciclador = (RecyclerView) findViewById(R.id.reciclador);
        layoutManager = new LinearLayoutManager(this);
        adaptador = new AdaptadorInventarios(this);
        reciclador.setLayoutManager(layoutManager);
        reciclador.setAdapter(adaptador);
        Log.e("PreparaLista", "=========" + reciclador.getChildCount());

        //preparaButtons();
    }
*/

    /*  Inicio validacion del internet    */
    protected Boolean estaConectado() {
        if (conectadoWifi()) {
            return true;
        } else {
            if (conectadoRedMovil()) {
                return true;
            } else {
                showAlertDialog(this, "Revisa tu conexión a Internet",
                        "Tu Dispositivo no tiene Conexión a Internet.", false);
                return false;
            }
        }
    }
    /*  Fin validacion del internet    */

    /*  Inicio validacion del internet    */
    protected Boolean conectadoWifi() {
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
    protected Boolean conectadoRedMovil() {
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

    @Override
    public void onClick(View v) {

    }
    /*  Fin validacion del internet    */


    private class TareaWSInsertar extends AsyncTask<String, Integer, Boolean> {
        public int contador = 0;

        protected Boolean doInBackground(String... params) {
            String message;
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://pruebas-servicios.pasa.mx:89/ApisPromotoraAmbiental/api/Inventario/altaEquipos");
            post.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials("adminLogistica", "Pasa123!"), "UTF-8", false));
            //post.setHeader("content-type", "application/json");
            try {
                String str_equipo_folio = "";
                String str_equipo_RFID = "";
                String str_tipo_Equipo_Id = "";
                String str_equipo_Almacen_Id = "";
                String str_equipo_Estatus_Id = "";
                String str_equipo_Propio = "";
                String str_branch_Id = "";
                SQLiteDatabase db = baseDatos.getWritableDatabase();
                Cursor c = db.rawQuery("Select equipo_Folio, equipo_RFID, tipo_Equipo_Id, equipo_Almacen_Id, equipo_Estatus_Id, equipo_Propio, branch_Id from " + HelperInventarios.Tablas.INVENTARIO, null);
                if (c.moveToFirst()) {
                    LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    addView = layoutInflater.inflate(R.layout.listitem_titular, null);
                    JSONObject object = new JSONObject();
                    ContentValues valores = new ContentValues();
                    HttpResponse resp;
                    do {
                        str_equipo_folio = c.getString(0);
                        str_equipo_RFID = c.getString(1);
                        str_tipo_Equipo_Id = c.getString(2);
                        str_equipo_Almacen_Id = c.getString(3);
                        str_equipo_Estatus_Id = c.getString(4);
                        str_equipo_Propio = c.getString(5);
                        str_branch_Id = c.getString(6);

                        object.put("equipoFolio", str_equipo_folio);
                        object.put("equipoRFID", "");
                        object.put("tipoEquipoId", str_tipo_Equipo_Id);
                        object.put("equipoAlmacenId", str_equipo_Almacen_Id);
                        object.put("equipoEstatusId", str_equipo_Estatus_Id);
                        object.put("equipoPropio", str_equipo_Propio);
                        object.put("branchId", str_branch_Id);
                        message = object.toString();
                        post.setEntity(new StringEntity(message, "UTF8"));
                        post.setHeader("Content-type", "application/json");
                        resp = httpClient.execute(post);
                        if (resp != null) {
                            if (resp.getStatusLine().getStatusCode() == 204)
                                result = true;
                        }
                        String respuesta = EntityUtils.toString(resp.getEntity());
                        String[] str_validar_msj = respuesta.split(" ");
                        String str_last2 = str_validar_msj[str_validar_msj.length - 1];
                        if (str_last2.equals("correctamente\"")) {
                            String[] args = new String[]{str_equipo_folio};
                            db.execSQL("DELETE FROM inventario WHERE equipo_Folio=?", args);
                        } else {
                            val1 = false;
                            if (str_last2.equals("registrado\"")) {
                                val2 = false;
                            } else {
                                val3 = false;
                            }
                        }
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
                    Toast.makeText(Actividad_Lista_Inventarios.this, finalToast, Toast.LENGTH_SHORT).show();
                }
            });
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                btn_Inve1.setBackgroundColor(Color.parseColor("#60000000"));
                btn_Inve1.setEnabled(false);
            }
            if (val1) {
                Toast.makeText(getApplicationContext(), "Todos los datos se sincronizaron de manera correcta", Toast.LENGTH_LONG).show();
            } else {
                if (!val2) {
                    Toast.makeText(getApplicationContext(), "Folios existentes con el mismo branch y usuario", Toast.LENGTH_LONG).show();
                } else {
                    if (!val3) {
                        Toast.makeText(getApplicationContext(), "Ha ocurrido un error, repórtelo con la empresa", Toast.LENGTH_LONG).show();
                    }
                }
            }
            prepararLista();
        }
    }
}
