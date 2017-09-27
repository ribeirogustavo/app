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
import br.com.ledstock.led_stock.led_stock.fragments.ItensOfEstudo_Lamps;
import br.com.ledstock.led_stock.led_stock.fragments.ItensOfEstudo_Leds;

/**
 * Created by Gustavo on 20/09/2016.
 */

public class ItensOfEstudoLampLedAdapter extends android.support.v7.widget.RecyclerView.Adapter<ItensOfEstudoLampLedAdapter.LampsViewHolder> {
    private final Context context;
    private Cursor c;
    private LampsOnClickListener lampsOnClickListener;

    public ItensOfEstudoLampLedAdapter(Context context, Cursor c, LampsOnClickListener lampsOnClickListener) {
        this.context = context;
        this.c = c;
        this.lampsOnClickListener = lampsOnClickListener;
    }

    @Override
    public LampsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_itens_of_estudo_lamps, parent, false);
        LampsViewHolder holder = new LampsViewHolder(view);
        return holder;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onBindViewHolder(final LampsViewHolder holder, int position) {
        String descricao;
        String quantidade;
        String horas;
        String desc;
        String potencia;
        String id;

        //Atualiza a View
        c.moveToPosition(position);

        id = c.getString(c.getColumnIndex("_id"));
        desc = c.getString(c.getColumnIndex("descricao"));
        potencia = c.getString(c.getColumnIndex("potencia")) + "W";
        quantidade = c.getString(c.getColumnIndex("quantidade"));
        horas = c.getString(c.getColumnIndex("horas"));

        descricao = desc + " - " + potencia;

        holder.descricao.setText(descricao);
        holder.quantidade.setText(quantidade);
        holder.horas.setText(horas);


        //Click
        if (lampsOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    c.moveToPosition(pos);
                    //A variavel position é final
                    lampsOnClickListener.onClickLamps(holder.itemView, c.getInt(c.getColumnIndex("_id")));
                }
            });

            //Click Longo
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getAdapterPosition();
                    c.moveToPosition(pos);
                    lampsOnClickListener.onLongClickLamps(holder.itemView, c.getInt(c.getColumnIndex("_id")));
                    return true;
                }
            });
        }

        boolean checked = false;
        int index;

        if (ItensOfEstudo_Lamps.Lamps != null) {
            index = ItensOfEstudo_Lamps.Lamps.indexOf(id);
            if (index != -1) {
                checked = true;
            }
        }else if(ItensOfEstudo_Leds.Leds != null){
            index = ItensOfEstudo_Leds.Leds.indexOf(id);
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
            holder.rowitem.setBackgroundColor (corFundo);
        }
    }

    @Override
    public int getItemCount() {
        return c.getCount();
    }

    public interface LampsOnClickListener {
        void onClickLamps(View view, int idx);

        void onLongClickLamps(View view, int idx);
    }

    //ViewHolder com as views
    public static class LampsViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public TextView descricao;
        public TextView quantidade;
        public TextView horas;
        public LinearLayout rowitem;

        public LampsViewHolder(View view) {
            super(view);
            //Cria as views para salvar no ViewHolder
            descricao = (TextView) view.findViewById(R.id.descricao);
            quantidade = (TextView) view.findViewById(R.id.quant);
            horas = (TextView) view.findViewById(R.id.hrs);
            rowitem = (LinearLayout) view.findViewById(R.id.rowitem);
        }
    }
}
