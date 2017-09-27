package br.com.ledstock.led_stock.led_stock.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.ledstock.led_stock.R;

import static android.R.attr.data;
import static br.com.ledstock.led_stock.R.string.cliente;

/**
 * Created by Gustavo on 21/10/2016.
 */

public class AmbientesOfEstudoAdapter extends android.support.v7.widget.RecyclerView.Adapter<AmbientesOfEstudoAdapter.AmbientesOfEstudoViewHolder> {
    private final Context context;
    private Cursor c;
    private AmbientesOfEstudoAdapter.AmbientesOfEstudoOnClickListener ambientesOfEstudoOnClickListener;

    public AmbientesOfEstudoAdapter(Context context, Cursor c, AmbientesOfEstudoAdapter.AmbientesOfEstudoOnClickListener ambientesOfEstudoOnClickListener) {
        this.context = context;
        this.c = c;
        this.ambientesOfEstudoOnClickListener = ambientesOfEstudoOnClickListener;
    }

    @Override
    public AmbientesOfEstudoAdapter.AmbientesOfEstudoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_ambientes_of_estudos, parent, false);
        AmbientesOfEstudoAdapter.AmbientesOfEstudoViewHolder holder = new AmbientesOfEstudoAdapter.AmbientesOfEstudoViewHolder(view);
        return holder;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onBindViewHolder(final AmbientesOfEstudoAdapter.AmbientesOfEstudoViewHolder holder, int position) {

        String descricao = null;

        //Atualiza a View
        c.moveToPosition(position);

        descricao = c.getString(c.getColumnIndex("descricao"));

        holder.descricao.setText(descricao);

        //Click
        if (ambientesOfEstudoOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    c.moveToPosition(pos);
                    //A variavel position é final
                    ambientesOfEstudoOnClickListener.onClickAmbienteOfEstudo(holder.itemView, c.getInt(c.getColumnIndex("_id_ambiente_estudo")));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return c.getCount();
    }

    public interface AmbientesOfEstudoOnClickListener {
        void onClickAmbienteOfEstudo(View view, int idx);
    }

    //ViewHolder com as views
    public static class AmbientesOfEstudoViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {

        public TextView descricao;
        CardView carView;


        public AmbientesOfEstudoViewHolder(View view) {
            super(view);
            //Cria as views para salvar no ViewHolder

            descricao = (TextView) view.findViewById(R.id.ambienteofestudo);
            carView = (CardView) view.findViewById(R.id.card_view);
        }
    }
}
