package pasa.inventarios.com;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import pasa.inventarios.com.Contrato.Inventarios;

/**
 * Created by Abraham on 27/06/2016.
 */
public class AdaptadorInventarios
                    extends RecyclerView.Adapter<AdaptadorInventarios.ViewHolder> {
    private Cursor items;
    // Instancia de escucha
    private OnItemClickListener escucha;
    /**
     * Interfaz para escuchar clicks del recycler
     */
    interface OnItemClickListener {
        public void onClick(ViewHolder holder, String idContacto);
    }
    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // Campos respectivos de un item
        public TextView nombre;
        public ViewHolder(View v) {
            super(v);
            nombre = (TextView) itemView.findViewById(R.id.nombre_contacto);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            escucha.onClick(this, obtenerIdContacto(getAdapterPosition()));
        }
    }
    private String obtenerIdContacto(int posicion) {
        if (items != null) {
            if (items.moveToPosition(posicion)) {
                return UConsultas.obtenerString(items, Inventarios.ID_PASA);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    public AdaptadorInventarios(OnItemClickListener escucha) {
        this.escucha = escucha;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacto, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        items.moveToPosition(position);
        String equipoFolio;
        String tipoEquipoAlmacen;
        String equipoAlmacen;
        String fkBranch;
        equipoFolio = UConsultas.obtenerString(items, Inventarios.EQUIPO_FOLIO);
        tipoEquipoAlmacen = UConsultas.obtenerString(items, Inventarios.FK_TIPO_EQUIPO_ID);
        equipoAlmacen = UConsultas.obtenerString(items, Inventarios.FK_EQUIPO_ALMACEN_ID);
        fkBranch = UConsultas.obtenerString(items, Inventarios.FK_BRANCH_ID);
        holder.nombre.setText(String.format("%s - %s - %s - %s", equipoFolio, tipoEquipoAlmacen, equipoAlmacen, fkBranch));
    }
    @Override
    public int getItemCount() {
        if (items != null)
            return items.getCount();
        return 0;
    }
    public void swapCursor(Cursor nuevoCursor) {
        if (nuevoCursor != null) {
            items = nuevoCursor;
            notifyDataSetChanged();
        }
    }
    public Cursor getCursor() {
        return items;
    }
}