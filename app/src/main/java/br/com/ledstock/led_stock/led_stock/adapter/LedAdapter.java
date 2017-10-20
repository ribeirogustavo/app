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
 * Created by Gustavo on 26/09/2016.
 */

public class LedAdapter extends android.support.v7.widget.RecyclerView.Adapter<LedAdapter.LedViewHolder> {
    private final Context context;
    private Cursor c;
    private LedAdapter.LedOnClickListener ledOnClickListener;

    public LedAdapter(Context context, Cursor c, LedAdapter.LedOnClickListener ledOnClickListener) {
        this.context = context;
        this.c = c;
        this.ledOnClickListener = ledOnClickListener;
    }

    @Override
    public LedAdapter.LedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_leds, parent, false);
        LedAdapter.LedViewHolder holder = new LedAdapter.LedViewHolder(view);
        return holder;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onBindViewHolder(final LedAdapter.LedViewHolder holder, int position) {
        String descricao = null;
        String potencia = null;

        //Atualiza a View
        c.moveToPosition(position);

        descricao = c.getString(c.getColumnIndex("descricao"));
        potencia = c.getString(c.getColumnIndex("potencia")) + "W";

        holder.descricao.setText(descricao);
        holder.potencia.setText(potencia);

        //Click
        if (ledOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    c.moveToPosition(pos);
                    //A variavel position é final
                    ledOnClickListener.onClickLed(holder.itemView, c.getInt(c.getColumnIndex("_id_led")));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return c.getCount();
    }

    public interface LedOnClickListener {
        void onClickLed(View view, int idx);
    }

    //ViewHolder com as views
    public static class LedViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public TextView descricao;
        public TextView potencia;

        public LedViewHolder(View view) {
            super(view);
            //Cria as views para salvar no ViewHolder
            descricao = (TextView) view.findViewById(R.id.descricao);
            potencia = (TextView) view.findViewById(R.id.potencia);
        }
    }
}
