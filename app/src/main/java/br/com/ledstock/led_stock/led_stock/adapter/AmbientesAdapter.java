package br.com.ledstock.led_stock.led_stock.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.ledstock.led_stock.R;

/**
 * Created by Gustavo on 28/09/2016.
 */

public class AmbientesAdapter extends android.support.v7.widget.RecyclerView.Adapter<AmbientesAdapter.AmbientesViewHolder> {
    private final Context context;
    private Cursor c;
    private AmbientesAdapter.AmbientesOnClickListener ambientesOnClickListener;

    public AmbientesAdapter(Context context, Cursor c, AmbientesAdapter.AmbientesOnClickListener ambientesOnClickListener) {
        this.context = context;
        this.c = c;
        this.ambientesOnClickListener = ambientesOnClickListener;
    }

    @Override
    public AmbientesAdapter.AmbientesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_ambientes, parent, false);
        AmbientesAdapter.AmbientesViewHolder holder = new AmbientesAdapter.AmbientesViewHolder(view);
        return holder;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onBindViewHolder(final AmbientesAdapter.AmbientesViewHolder holder, int position) {
       String descricao = null;
      //  String potencia = null;

        //Atualiza a View
        c.moveToPosition(position);

        descricao = c.getString(c.getColumnIndex("descricao"));
        //potencia = c.getString(c.getColumnIndex("potencia")) + "W";

        holder.descricao.setText(descricao);
        //holder.potencia.setText(potencia);

        //Click
        if (ambientesOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    c.moveToPosition(pos);
                    //A variavel position é final
                    ambientesOnClickListener.onClickAmbiente(holder.itemView, c.getInt(c.getColumnIndex("_id_ambiente")));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return c.getCount();
    }

    public interface AmbientesOnClickListener {
        void onClickAmbiente(View view, int idx);
    }

    //ViewHolder com as views
    public static class AmbientesViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public TextView descricao;
        //public TextView potencia;

        public AmbientesViewHolder(View view) {
            super(view);
            //Cria as views para salvar no ViewHolder
            descricao = (TextView) view.findViewById(R.id.descricao);
        }
    }
}
