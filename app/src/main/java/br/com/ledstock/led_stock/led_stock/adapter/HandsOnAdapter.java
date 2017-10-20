package br.com.ledstock.led_stock.led_stock.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.fragments.ItensOfEstudo_HandsOn;
import br.com.ledstock.led_stock.led_stock.fragments.ItensOfEstudo_Lamps;
import br.com.ledstock.led_stock.led_stock.fragments.ItensOfEstudo_Leds;

import static android.R.attr.id;
import static br.com.ledstock.led_stock.R.id.rowitem;
import static br.com.ledstock.led_stock.led_stock.fragments.ItensOfEstudo_Lamps.Lamps;

/**
 * Created by Gustavo on 28/09/2016.
 */

public class HandsOnAdapter extends android.support.v7.widget.RecyclerView.Adapter<HandsOnAdapter.HandsOnViewHolder> {
    private final Context context;
    private Cursor c;
    private HandsOnAdapter.HandsOnOnClickListener handsonOnClickListener;

    public HandsOnAdapter(Context context, Cursor c, HandsOnAdapter.HandsOnOnClickListener handsonOnClickListener) {
        this.context = context;
        this.c = c;
        this.handsonOnClickListener = handsonOnClickListener;
    }

    @Override
    public HandsOnAdapter.HandsOnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_handson, parent, false);
        HandsOnAdapter.HandsOnViewHolder holder = new HandsOnAdapter.HandsOnViewHolder(view);
        return holder;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onBindViewHolder(final HandsOnAdapter.HandsOnViewHolder holder, int position) {
        String descricao;
        String quantidade = null;
        //Atualiza a View
        c.moveToPosition(position);
        String id = c.getString(c.getColumnIndex("_id"));
        descricao = c.getString(c.getColumnIndex("descricao"));
        if (c.getColumnIndex("quantidade") != -1) {
            quantidade = c.getString(c.getColumnIndex("quantidade"));
        }
        holder.descricao.setText(descricao);

        if (quantidade != null) {
            quantidade = "Quant. " + quantidade;
            holder.quantidade.setText(quantidade);
        }else{
            holder.quantidade.setVisibility(View.INVISIBLE);
        }

        //Click
        if (handsonOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    c.moveToPosition(pos);
                    //A variavel position é final
                    handsonOnClickListener.onClickHandsOn(holder.itemView, c.getInt(c.getColumnIndex("_id")));
                }
            });

            //Click Longo
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getAdapterPosition();
                    c.moveToPosition(pos);
                    handsonOnClickListener.onLongClickHandsOn(holder.itemView, c.getInt(c.getColumnIndex("_id")));
                    return true;
                }
            });
        }

        boolean checked = false;
        int index;

        if (ItensOfEstudo_HandsOn.HandsOn != null) {
            index = ItensOfEstudo_HandsOn.HandsOn.indexOf(id);
            if (index != -1) {
                checked = true;
            }
        }

        int corFundo;
        //Verifica se o Anrdoid é HoneyComb
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //Se for, verifica se o item esta checkado
            if (checked) {
                //Caso esteja checado, troca a cor do Background
                corFundo = ContextCompat.getColor(context, R.color.selected);
                holder.rowitem.setBackgroundColor(corFundo);
            } else {
                //Se não estiver checkado
                //É necessário, setar o Backgorund Como Clickable
                //Recurso não disponível se setar o Background Color com White
                //Background contém o Efeito Ripple
                int[] attrs = new int[]{android.R.attr.selectableItemBackground};
                // Retrieving the style attribute
                TypedArray typedArray = context.obtainStyledAttributes(attrs);
                int backgroundResource = typedArray.getResourceId(0, 0);

                // Setando o Recurso do Background Clickable
                holder.rowitem.setBackgroundResource(backgroundResource);
                typedArray.recycle();
            }
        } else {
            //Caso não seja HonyComb, usar Selecionado com Cor Selected ou Branco
            corFundo = ContextCompat.getColor(context, checked ? R.color.selected : R.color.white);
            holder.rowitem.setBackgroundColor(corFundo);
        }
    }

    @Override
    public int getItemCount() {
        return c.getCount();
    }

    public interface HandsOnOnClickListener {
        void onClickHandsOn(View view, int idx);

        void onLongClickHandsOn(View view, int idx);
    }

    //ViewHolder com as views
    public static class HandsOnViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public TextView descricao;
        public LinearLayout rowitem;
        public TextView quantidade;

        public HandsOnViewHolder(View view) {
            super(view);
            //Cria as views para salvar no ViewHolder
            descricao = (TextView) view.findViewById(R.id.descricao);
            rowitem = (LinearLayout) view.findViewById(R.id.rowitem);
            quantidade = (TextView) view.findViewById(R.id.quant);
        }
    }


}
