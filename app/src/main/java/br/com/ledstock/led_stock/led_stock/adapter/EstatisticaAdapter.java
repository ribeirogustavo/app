package br.com.ledstock.led_stock.led_stock.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.domain.Stat;

/**
 * Created by Gustavo on 25/04/2016.
 */
public class EstatisticaAdapter extends RecyclerView.Adapter<EstatisticaAdapter.EstatisticsViewHolder> {

    private final List<Stat> stats;
    private final Context context;
    private EstatisticasOnClickListener estatisticasOnClickListener;

    public EstatisticaAdapter(Context context, List<Stat> stats, EstatisticasOnClickListener estatisicasOnClickListener) {
        this.context = context;
        this.stats = stats;
        this.estatisticasOnClickListener = estatisicasOnClickListener;
    }

    public int getItemCount() {
        return this.stats != null ? this.stats.size() : 0;
    }

    public EstatisticsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_statistics, viewGroup, false);
        EstatisticsViewHolder holder = new EstatisticsViewHolder(view);
        return holder;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onBindViewHolder(final EstatisticsViewHolder holder, final int position) {
        //Atualiza a View

        DecimalFormat df = new DecimalFormat("#.00");

        Stat c = stats.get(position);
        holder.tdesc.setText(c.ambi);

        //String Potencia_atual = context.getResources().getString(R.string.potencia_atual);
        //String pot_atual = Potencia_atual + " " + c.Pot_Atual + "W";
        //holder.tpot_atual.setText(pot_atual);

        String pot_atual = c.Pot_Atual + "W";
        holder.tpot_atual.setText(pot_atual);

        String pot_led = c.Pot_Led + "W";
        holder.tpot_led.setText(pot_led);

        String cont_atual = "R$: " + df.format(c.conta_atual);
        holder.tconta_atual.setText(cont_atual);

        String cont_ideal = "R$: " + df.format(c.conta_ideal);
        holder.tconta_ideal.setText(cont_ideal);

        String inves = "R$: " + df.format(c.investimento);
        holder.tinvestimento.setText(inves);

        String econo = "R$: " + df.format(c.economia);
        holder.teconomia.setText(econo);

        String retorno = df.format(c.retorno);
        holder.tretorno.setText(retorno);

        String mdo = "R$: " + df.format(c.mdo);
        holder.tmdo.setText(mdo);

        //Click
        if (estatisticasOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //A variavel position é final
                    estatisticasOnClickListener.onClickStati(holder.itemView, position);

                }
            });

            //Click Longo
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    estatisticasOnClickListener.onLongClickStati(holder.itemView, position);
                    return true;
                }
            });
        }

        //Pinta a linha de azul caso o carro esteja selecionado
        // int corFundo = context.getResources().getColor(c.selected ? R.color.primary : R.color.white);
        //holder.cardView.setCardBackgroundColor(corFundo);

        //A cor do texto é branca ou azul, dependendo da cor de fundo
        //int corFonte = context.getResources().getColor(c.selected ? R.color.white : R.color.primary);
        //holder.tNome.setTextColor(corFonte);
    }

    public interface EstatisticasOnClickListener {
        void onClickStati(View view, int idx);

        void onLongClickStati(View view, int idx);
    }

    //ViewHolder com as views
    public static class EstatisticsViewHolder extends RecyclerView.ViewHolder {
        public TextView tdesc;
        public TextView tpot_atual;
        public TextView tpot_led;
        public TextView tconta_atual;
        public TextView tconta_ideal;
        public TextView teconomia;
        public TextView tretorno;
        public TextView tinvestimento;
        public TextView tmdo;
        CardView cardView;

        public EstatisticsViewHolder(View view) {
            super(view);
            //Cria as views para salvar no ViewHolder
            tdesc = (TextView) view.findViewById(R.id.ambiente);
            tpot_atual = (TextView) view.findViewById(R.id.potencia_atual);
            tpot_led = (TextView) view.findViewById(R.id.potencia_led);
            tconta_atual = (TextView) view.findViewById(R.id.conta_atual);
            tconta_ideal = (TextView) view.findViewById(R.id.conta_ideal);
            teconomia = (TextView) view.findViewById(R.id.economia);
            tretorno = (TextView) view.findViewById(R.id.retorno);
            tinvestimento = (TextView) view.findViewById(R.id.investimento);
            tmdo = (TextView) view.findViewById(R.id.mob);
            //img = (ImageView) view.findViewById(img);
            //progress = (ProgressBar) view.findViewById(R.id.progressImg);
            cardView = (CardView) view.findViewById(R.id.card_view);
        }
    }
}
