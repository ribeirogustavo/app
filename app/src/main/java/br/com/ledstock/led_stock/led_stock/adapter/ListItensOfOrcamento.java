package br.com.ledstock.led_stock.led_stock.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.fragments.FragmentListOrcamento;
import br.com.ledstock.led_stock.led_stock.fragments.ItensOfEstudo_Lamps;
import br.com.ledstock.led_stock.led_stock.fragments.ItensOfEstudo_Leds;

/**
 * Created by Gustavo on 21/10/2016.
 */

public class ListItensOfOrcamento extends android.support.v7.widget.RecyclerView.Adapter<ListItensOfOrcamento.ListItensOfOrcamentoViewHolder> {
    private final Context context;
    private Cursor c;
    private ListItensOfOrcamento.ListItensOfOrcamentoOnClickListener listItensOfOrcamentoOnClickListener;
    private int count;

    public ListItensOfOrcamento(Context context, Cursor c, ListItensOfOrcamento.ListItensOfOrcamentoOnClickListener listItensOfOrcamentoOnClickListener) {
        this.context = context;
        this.c = c;
        this.listItensOfOrcamentoOnClickListener = listItensOfOrcamentoOnClickListener;
        this.count = 0;
    }

    @Override
    public ListItensOfOrcamento.ListItensOfOrcamentoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_list_of_orcamentos, parent, false);
        ListItensOfOrcamento.ListItensOfOrcamentoViewHolder holder = new ListItensOfOrcamento.ListItensOfOrcamentoViewHolder(view);
        return holder;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onBindViewHolder(final ListItensOfOrcamento.ListItensOfOrcamentoViewHolder holder, int position) {

        String descricao;
        String descricao_mao;
        String desconto;
        String quantidade;
        String id;

        //Atualiza a View
        c.moveToPosition(position);

        id = c.getString(c.getColumnIndex("_id"));
        descricao = c.getString(c.getColumnIndex("descricao"));
        descricao_mao = c.getString(c.getColumnIndex("descricao_mao"));
        desconto = c.getString(c.getColumnIndex("descount"));
        quantidade = c.getString(c.getColumnIndex("quantidade"));

        if (c.getPosition() == 0){
            holder.title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            holder.title.setText(R.string.listaleds);

        }else{
            holder.title.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        }

        if ((descricao == null) && (descricao_mao != null)){
            if (count == 0){
                count = 1;
                holder.title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                holder.title.setText(R.string.listahandson);
            }else{
                holder.title.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            }

        }

        if ((descricao_mao == null) || (descricao_mao.equals(""))) {
            holder.descricao.setText(descricao);
        } else {
            holder.descricao.setText(descricao_mao);
        }
        holder.quantidade.setText(quantidade);

        if ((desconto != null) && (!desconto.equals(""))) {
            desconto += "%";
            holder.desconto.setText(desconto);
        } else {
            desconto = "---";
            holder.desconto.setText(desconto);
            //holder.desconto.setVisibility(View.INVISIBLE);
            //holder.descr_desconto.setVisibility(View.INVISIBLE);
        }

        //Click
        if (listItensOfOrcamentoOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    c.moveToPosition(pos);
                    //A variavel position é final
                    listItensOfOrcamentoOnClickListener.onClickListItensOfOrcamento(holder.itemView, c.getInt(c.getColumnIndex("_id")));
                }
            });

            //Click Longo
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getAdapterPosition();
                    c.moveToPosition(pos);
                    listItensOfOrcamentoOnClickListener.onClickLongListItensOfOrcamento(holder.itemView, c.getInt(c.getColumnIndex("_id")));
                    return true;
                }
            });
        }

        boolean checked = false;
        int index;

        if (FragmentListOrcamento.Itens != null) {
            index = FragmentListOrcamento.Itens.indexOf(id);
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

    public interface ListItensOfOrcamentoOnClickListener {
        void onClickListItensOfOrcamento(View view, int idx);

        void onClickLongListItensOfOrcamento(View view, int idx);
    }

    //ViewHolder com as views
    public static class ListItensOfOrcamentoViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {

        public TextView title;
        public TextView descricao;
        public TextView desconto;
        public TextView quantidade;
        CardView carView;
        public LinearLayout rowitem;
        public TextView descr_desconto;
        public LinearLayout linearLayout;


        public ListItensOfOrcamentoViewHolder(View view) {
            super(view);
            //Cria as views para salvar no ViewHolder

            descricao = (TextView) view.findViewById(R.id.itens_descricao);
            desconto = (TextView) view.findViewById(R.id.itens_desconto);
            quantidade = (TextView) view.findViewById(R.id.itens_quantidade);
            carView = (CardView) view.findViewById(R.id.card_view);
            rowitem = (LinearLayout) view.findViewById(R.id.row_item_orcamento);
            descr_desconto = (TextView) view.findViewById(R.id.descr_desconto);
            title = (TextView) view.findViewById(R.id.title);
            linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout);
        }
    }
}
