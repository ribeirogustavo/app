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

/**
 * Created by Gustavo on 21/10/2016.
 */

public class EstudosAdapter extends android.support.v7.widget.RecyclerView.Adapter<EstudosAdapter.EstudosViewHolder> {
    private final Context context;
    private Cursor c;
    private EstudosAdapter.EstudosOnClickListener estudosOnClickListener;

    public EstudosAdapter(Context context, Cursor c, EstudosAdapter.EstudosOnClickListener estudosOnClickListener) {
        this.context = context;
        this.c = c;
        this.estudosOnClickListener = estudosOnClickListener;
    }

    @Override
    public EstudosAdapter.EstudosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_estudos, parent, false);
        EstudosAdapter.EstudosViewHolder holder = new EstudosAdapter.EstudosViewHolder(view);
        return holder;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onBindViewHolder(final EstudosAdapter.EstudosViewHolder holder, int position) {

        String descricao = null;
        String cliente = null;
        String data = null;

        //Atualiza a View
        c.moveToPosition(position);

        descricao = c.getString(c.getColumnIndex("descricao"));
        cliente = c.getString(c.getColumnIndex("cliente"));
        data = c.getString(c.getColumnIndex("data"));

        holder.descricao.setText(descricao);
        holder.cliente.setText(cliente);
        holder.data.setText(data);

        //Click
        if (estudosOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    c.moveToPosition(pos);
                    //A variavel position é final
                    estudosOnClickListener.onClickEstudo(holder.itemView, c.getInt(c.getColumnIndex("_id_estudo")));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return c.getCount();
    }

    public interface EstudosOnClickListener {
        void onClickEstudo(View view, int idx);
    }

    //ViewHolder com as views
    public static class EstudosViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {

        public TextView descricao;
        public TextView cliente;
        public TextView data;
        CardView carView;


        public EstudosViewHolder(View view) {
            super(view);
            //Cria as views para salvar no ViewHolder

            descricao = (TextView) view.findViewById(R.id.nome_estudo);
            cliente = (TextView) view.findViewById(R.id.cliente_estudo);
            data = (TextView) view.findViewById(R.id.data_estudo);
            carView = (CardView) view.findViewById(R.id.card_view);
        }
    }
}
