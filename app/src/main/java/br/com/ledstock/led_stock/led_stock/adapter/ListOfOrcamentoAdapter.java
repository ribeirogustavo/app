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

public class ListOfOrcamentoAdapter extends android.support.v7.widget.RecyclerView.Adapter<ListOfOrcamentoAdapter.ListOfOrcamentoViewHolder> {
    private final Context context;
    private Cursor c;
    private ListOfOrcamentoAdapter.ListOfOrcamentoOnClickListener listOfOrcamentoOnClickListener;

    public ListOfOrcamentoAdapter(Context context, Cursor c, ListOfOrcamentoAdapter.ListOfOrcamentoOnClickListener listOfOrcamentoOnClickListener) {
        this.context = context;
        this.c = c;
        this.listOfOrcamentoOnClickListener = listOfOrcamentoOnClickListener;
    }

    @Override
    public ListOfOrcamentoAdapter.ListOfOrcamentoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_list_of_orcamentos, parent, false);
        ListOfOrcamentoAdapter.ListOfOrcamentoViewHolder holder = new ListOfOrcamentoAdapter.ListOfOrcamentoViewHolder(view);
        return holder;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onBindViewHolder(final ListOfOrcamentoAdapter.ListOfOrcamentoViewHolder holder, int position) {

        String descricao = null;

        //Atualiza a View
        c.moveToPosition(position);

        descricao = c.getString(c.getColumnIndex("descricao"));

        holder.descricao.setText(descricao);

        //Click
        if (listOfOrcamentoOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getAdapterPosition();
                    c.moveToPosition(pos);
                    //A variavel position é final
                    listOfOrcamentoOnClickListener.onClickListOfOrcamento(holder.itemView, c.getInt(c.getColumnIndex("_id_list_of_orcamento")));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return c.getCount();
    }

    public interface ListOfOrcamentoOnClickListener {
        void onClickListOfOrcamento(View view, int idx);
    }

    //ViewHolder com as views
    public static class ListOfOrcamentoViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {

        public TextView descricao;
        CardView carView;


        public ListOfOrcamentoViewHolder(View view) {
            super(view);
            //Cria as views para salvar no ViewHolder

            descricao = (TextView) view.findViewById(R.id.listoforcamento);
            carView = (CardView) view.findViewById(R.id.card_view);
        }
    }
}
