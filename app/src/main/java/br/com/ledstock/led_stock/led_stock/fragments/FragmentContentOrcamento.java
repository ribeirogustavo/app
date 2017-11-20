package br.com.ledstock.led_stock.led_stock.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.adapter.TabsEstudoAdapter;
import br.com.ledstock.led_stock.led_stock.adapter.TabsOrcamentoAdapter;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;
import br.com.ledstock.led_stock.led_stock.utils.Global;
import br.com.ledstock.led_stock.led_stock.utils.IOUtils;

import static br.com.ledstock.led_stock.led_stock.fragments.ItensOfEstudo.ID_ESTUDO;

/**
 * Created by Gustavo on 21/10/2016.
 */

public class FragmentContentOrcamento extends Fragment {

    private static long ID_ORCAMENTO;
    ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ID_ORCAMENTO = getArguments().getLong("id_orcamento", 0);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_orcamento, container, false);

        // context = getActivity();

        //Informar que este fragment contem menu na ToolBar
        setHasOptionsMenu(true);

        //Instancia a ActionBar (Que na verdade é a ToolBar)
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            //Adicionar o Icone de Menu
            actionBar.setElevation(0);
        }

        //FragmentManager precisa ser Child porque é um Fragment dentro de um fragment
        FragmentManager fm = getChildFragmentManager();
        //Seta ViewPager
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.ViewPager);
        viewPager.setAdapter(new TabsOrcamentoAdapter(getContext(), fm));

        //Seta as Tabs
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.TabLayout);
        if (tabLayout != null) {
            //Cria as Tabs com o mesmo adapter utilizado pelo ViewPager
            tabLayout.setupWithViewPager(viewPager);
            //int cor = ContextCompat.getColor(getContext(), R.color.white);
            // tabLayout.setTabTextColors(cor,cor);
            tabLayout.getTabAt(0).setIcon(R.drawable.ic_place);
            tabLayout.getTabAt(1).setIcon(R.drawable.ic_timeline_white);
        }

        // Attach the page change listener inside the activity
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {

                if (position == 1) {
                    getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                    Intent intent = new Intent();
                    intent.setAction("REFRESH_ESTATISTICAS_ORCAMENTO");
                    getActivity().sendBroadcast(intent);
                } else {
                    getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
                }
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

        getActivity().findViewById(R.id.fab).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tabLayout != null) {
                            if (tabLayout.getSelectedTabPosition() == 0) {
                                Dialog_Itens_Of_Orcamento.show(getChildFragmentManager(), ID_ORCAMENTO, getActivity());
                            }
                        }
                    }
                }
        );

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_orcamento, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.pedido) {

            Dialog_Pedido_Orcamento_fragment.show(getChildFragmentManager(), String.valueOf(ID_ORCAMENTO), getActivity());

        } else if (item.getItemId() == R.id.info) {

            Dialog_Info_Client_fragment.show(getChildFragmentManager(), String.valueOf(ID_ORCAMENTO), getActivity());

        } else if (item.getItemId() == R.id.desconto) {

            Dialog_AplicarDesconto_fragment.show(getChildFragmentManager(), String.valueOf(ID_ORCAMENTO), getActivity());

        } else if (item.getItemId() == R.id.enviar) {


            try {
                GerarRelatorio();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (DocumentException e2) {
                //e2.printStackTrace();
            }

            return true;
        } else if (item.getItemId() == R.id.excluir) {

            DeletarOrcamentoDialog.show(getFragmentManager(), new DeletarOrcamentoDialog.Callback() {
                public void onClickYes() {
                    //Instancia o Banco de Dados
                    LedStockDB delete_orcamento = new LedStockDB(getActivity());
                    delete_orcamento.DeleteOrcamento(String.valueOf(ID_ORCAMENTO));

                    //Instancia o Serviço para Deletar Remotamente
                    LedService service = new LedService();
                    service.DeleteOrcamentoRemote(String.valueOf(ID_ORCAMENTO));

                    Intent intent = new Intent();
                    intent.setAction("REFRESH_ORCAMENTOS");
                    getActivity().sendBroadcast(intent);

                    //Fecha a Activity
                    getActivity().finish();
                }
            });

            return true;
        } else if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            intent.setAction("REFRESH_ORCAMENTOS");
            getActivity().sendBroadcast(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void GerarRelatorio() throws IOException, DocumentException {


        final int[] NumberOfRows = {0};

        LedStockDB db = new LedStockDB(getActivity());
        long ID_ORCAMENTO_REMOTE = db.SelectOrcamentoRemoteIDById(String.valueOf(ID_ORCAMENTO));
        Cursor c_orcamento = db.SelectRelatorioOrcamento(String.valueOf(ID_ORCAMENTO), String.valueOf(ID_ORCAMENTO_REMOTE), 1);
        Cursor c_orcamento2 = db.SelectRelatorioOrcamento(String.valueOf(ID_ORCAMENTO), String.valueOf(ID_ORCAMENTO_REMOTE), 2);

        if (c_orcamento != null) {
            if ((c_orcamento.getCount() > 0) || (c_orcamento2.getCount() > 0)) {

                progress = new ProgressDialog(getActivity());
                progress.setMessage("Aguarde, carregando Relatório");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.show();

                new Thread() {
                    public void run() {

                        String content;
                        String conteudo = null;
                        String sendereco, snum, scomp, sbairro, scidade, address;
                        double valor_total_led = 0.0;
                        double valor_total_mdo = 0.0;
                        int contain_mdo = 0;
                        int contain_led = 0;


                        //Este metodo consegue pegar a String do index.html
                        //Resources resources = getActivity().getResources();
                        //InputStream in = resources.openRawResource(R.raw.index);
                        //Log.e("TESTE","Caminho: " + IOUtils.toString(in, "UTF-8"));

                        Resources resources = getActivity().getResources();
                        InputStream in = resources.openRawResource(R.raw.orcamento);

                        try {
                            //Captura todo conteudo do index.html
                            content = IOUtils.toString(in, "UTF-8");

                            LedStockDB db = new LedStockDB(getActivity());
                            Cursor c = db.SelectClienteOfOrcamento(String.valueOf(ID_ORCAMENTO));

                            if (c != null) {

                                sendereco = c.getString(c.getColumnIndex("endereco"));
                                snum = c.getString(c.getColumnIndex("numero"));
                                scomp = c.getString(c.getColumnIndex("comp"));
                                sbairro = c.getString(c.getColumnIndex("bairro"));
                                scidade = c.getString(c.getColumnIndex("cidade"));

                                address = "";

                                if (sendereco.trim().length() != 0) {
                                    address += sendereco;
                                }
                                if (snum.trim().length() != 0) {
                                    address += ", " + snum;
                                }
                                if (scomp.trim().length() != 0) {
                                    address += " - " + scomp;
                                }
                                if (sbairro.trim().length() != 0) {
                                    address += " - " + sbairro;
                                }
                                if (scidade.trim().length() != 0) {
                                    address += " - " + scidade;
                                }

                                conteudo = content.replace("{CLIENTE}", c.getString(c.getColumnIndex("nome")));
                                if (address != null) {
                                    conteudo = conteudo.replace("{ENDERECO}", address);
                                } else {
                                    conteudo = conteudo.replace("{ENDERECO}", " ");
                                }

                                String tel1 = c.getString(c.getColumnIndex("tel"));
                                String tel2 = c.getString(c.getColumnIndex("tel2"));

                                String telefone = null;

                                if (!tel1.equals("") && (tel1 != null)) {
                                    telefone = tel1;
                                }
                                if (!tel2.equals("") && (tel2 != null)) {
                                    telefone += " / " + tel2;
                                }
                                conteudo = conteudo.replace("{CONTATO}", c.getString(c.getColumnIndex("contato")));
                                if (telefone != null) {
                                    conteudo = conteudo.replace("{TELEFONE}", telefone);
                                } else {
                                    conteudo = conteudo.replace("{TELEFONE}", " ");
                                }

                                String email1 = c.getString(c.getColumnIndex("email"));
                                String email2 = c.getString(c.getColumnIndex("email2"));

                                String email = null;

                                if (!email1.equals("") && (email1 != null)) {
                                    email = email1;
                                }
                                if (!email2.equals("") && (email2 != null)) {
                                    email += " / " + email2;
                                }

                                if (email != null) {
                                    conteudo = conteudo.replace("{EMAIL}", email);
                                } else {
                                    conteudo = conteudo.replace("{EMAIL}", " ");
                                }

                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                String date = df.format(calendar.getTime());

                                conteudo = conteudo.replace("{DATAORCAMENTO}", " " + date);
                                c.close();
                            }

                            String row_orcamento = null;

                            long ID_ORCAMENTO_REMOTE = db.SelectOrcamentoRemoteIDById(String.valueOf(ID_ORCAMENTO));
                            Cursor c_list = db.SelectRelatorioOrcamento(String.valueOf(ID_ORCAMENTO), String.valueOf(ID_ORCAMENTO_REMOTE), 1);

                            if (c_list != null) {
                                if (c_list.getCount() > 0) {
                                    contain_led = 1;

                                    String descricao;
                                    String descricao_desconto;
                                    Double sum_valor = 0.0;
                                    int sum_qnt = 0;

                                    do {

                                        descricao = c_list.getString(c_list.getColumnIndex("descricao"));
                                        descricao_desconto = " - Desconto de ";

                                        Double desconto = c_list.getDouble(c_list.getColumnIndex("desconto"));
                                        sum_valor += c_list.getDouble(c_list.getColumnIndex("valor_com_desconto"));

                                        if (!String.valueOf(desconto).equals("0.0")) {
                                            descricao += descricao_desconto + String.valueOf(desconto) + "%";
                                        }

                                        sum_qnt += c_list.getInt(c_list.getColumnIndex("quantidade"));

                                        Double valor_unidade = c_list.getDouble(c_list.getColumnIndex("valor")) / c_list.getInt(c_list.getColumnIndex("quantidade"));

                                        row_orcamento += "<tr>" +
                                                "<td class='align_left border_thick_left commun'>" + descricao + "</td>" +
                                                "<td  class='align_center commun'>" + c_list.getString(c_list.getColumnIndex("quantidade")) + "</td>" +
                                                "<td  class='align_right commun'>" + "R$ " + String.format(Locale.getDefault(), "%.2f", valor_unidade) + "</td>" +
                                                "<td  class='align_right border_thick_right commun'>" + "R$ " + String.format(Locale.getDefault(), "%.2f", c_list.getDouble(c_list.getColumnIndex("valor_com_desconto"))) + "</td>" +
                                                "</tr>";


                                    } while (c_list.moveToNext());

                                    valor_total_led = sum_valor;

                                    assert conteudo != null;
                                    conteudo = conteudo.replace("{CONTEUDO_LEDS}", row_orcamento);

                                    conteudo = conteudo.replace("{QNT}", String.valueOf(sum_qnt));

                                    conteudo = conteudo.replace("{VALOR}", "R$ " + String.format(Locale.getDefault(), "%.2f", sum_valor));

                                    c_list.close();
                                }else{
                                    assert conteudo != null;
                                    conteudo = conteudo.replace("{CONTEUDO_LEDS}", "<tr><td colspan='4' class='border_thick_right border_thick_left'>&nbsp;</td></tr>");

                                    conteudo = conteudo.replace("{QNT}", "");

                                    conteudo = conteudo.replace("{VALOR}", "");
                                }
                            }


                            Cursor c_handson = db.SelectRelatorioOrcamento(String.valueOf(ID_ORCAMENTO), String.valueOf(ID_ORCAMENTO_REMOTE), 2);

                            if (c_handson != null) {
                                if (c_handson.getCount() > 0) {
                                    contain_mdo = 1;
                                    String row_handson = "<table class='table_content mdo' cellspacing='0'>" + "<thead>" +
                                            "<tr class='header'><td colspan='4' class='border_thick_top  border_thick_left border_thick_right'>Mão de Obra</td></tr>" +
                                            "<tr class='header'>" +
                                            "<td class='border_thick_top border_thick_bottom extra_long'>Descrição</td>" +
                                            "<td class='border_thick_left border_thick_top border_thick_bottom'>Qnt.</td>" +
                                            "<td class='border_thick_top border_thick_bottom'>Valor Uni.</td>" +
                                            "<td class='border_thick_right border_thick_top border_thick_bottom'>Valor</td>" +
                                            "</tr>" +
                                            "</thead><tbody>";

                                    String descricao;
                                    String descricao_desconto;
                                    Double sum_valor = 0.0;
                                    int sum_qnt = 0;

                                    do {

                                        descricao = c_handson.getString(c_handson.getColumnIndex("descricao"));
                                        descricao_desconto = " - Desconto de ";

                                        Double desconto = c_handson.getDouble(c_handson.getColumnIndex("desconto"));
                                        sum_valor += c_handson.getDouble(c_handson.getColumnIndex("valor_com_desconto"));

                                        if (!String.valueOf(desconto).equals("0.0")) {
                                            descricao += descricao_desconto + String.valueOf(desconto) + "%";
                                        }

                                        sum_qnt += c_handson.getInt(c_handson.getColumnIndex("quantidade"));

                                        Double valor_unidade = c_handson.getDouble(c_handson.getColumnIndex("valor")) / c_handson.getInt(c_handson.getColumnIndex("quantidade"));

                                        row_handson += "<tr>" +
                                                "<td class='align_left border_thick_left commun'>" + descricao + "</td>" +
                                                "<td  class='align_center commun short'>" + c_handson.getString(c_handson.getColumnIndex("quantidade")) + "</td>" +
                                                "<td  class='align_right commun short'>" + "R$ " + String.format(Locale.getDefault(), "%.2f", valor_unidade) + "</td>" +
                                                "<td  class='align_right border_thick_right commun short'>" + "R$ " + String.format(Locale.getDefault(), "%.2f", c_handson.getDouble(c_list.getColumnIndex("valor_com_desconto"))) + "</td>" +
                                                "</tr>";

                                    } while (c_handson.moveToNext());

                                    valor_total_mdo = sum_valor;

                                    row_handson += "</tbody><tfoot>" +
                                            "<tr>" +
                                            "<td class='border_thick_left border_thick_bottom border_thick_top total'><b>TOTAL</b></td>" +
                                            "<td class='border_thick_bottom border_thick_left border_thick_top align_center'>" + String.valueOf(sum_qnt) + "</td>" +
                                            "<td class='align_right border_thick_right border_thick_bottom border_thick_top border_thick_left' colspan='2'><b> R$ " + String.format(Locale.getDefault(), "%.2f", sum_valor) + "</b></td>" +
                                            "</tr>" +
                                            "</tfoot>" +
                                            "</table>";

                                    assert conteudo != null;
                                    conteudo = conteudo.replace("{TABELAMAODEOBRA}", row_handson);

                                    c_handson.close();
                                } else {
                                    assert conteudo != null;
                                    conteudo = conteudo.replace("{TABELAMAODEOBRA}", "");
                                }
                            } else {
                                assert conteudo != null;
                                conteudo = conteudo.replace("{TABELAMAODEOBRA}", "");
                            }

                            String row_total = "<table id='tot_tot' class='table_content' cellspacing='0'>" + "<thead>" +
                                    "<tr class='header'>" +
                                    "<td class='border_thick_top border_thick_left border_thick_right border_thick_bottom extra_long' colspan='2'>TOTAL</td>" +
                                    "</tr>" +
                                    "</thead><tbody>";

                            if (contain_led == 1) {

                                row_total += "<tr>" +
                                        "<td class='align_left border_thick_left commun'>Solução LED</td>" +
                                        "<td  class='align_right border_thick_right commun short'>" + "R$ " + String.format(Locale.getDefault(), "%.2f", valor_total_led) + "</td>" +
                                        "</tr>";
                            }

                            if (contain_mdo == 1) {
                                row_total += "<tr>" +
                                        "<td class='align_left border_thick_left commun'>Mão de Obra</td>" +
                                        "<td  class='align_right border_thick_right commun short'>" + "R$ " + String.format(Locale.getDefault(), "%.2f", valor_total_mdo) + "</td>" +
                                        "</tr>";
                            }

                            Cursor c_descount = db.SelectRelatorioOrcamento(String.valueOf(ID_ORCAMENTO), String.valueOf(ID_ORCAMENTO_REMOTE), 3);

                            Double desconto = 0.0;

                            assert c_descount != null;
                            if (c_descount.getCount() > 0) {
                                if (c_descount.getDouble(c_descount.getColumnIndex("descount")) != 0.0) {

                                    row_total += "<tr>" +
                                            "<td class='align_left border_thick_left commun'>Desconto</td>" +
                                            "<td  class='align_right border_thick_right commun short'>" + String.format(Locale.getDefault(), "%.2f", c_descount.getDouble(c_descount.getColumnIndex("descount"))) + "%</td>" +
                                            "</tr>";

                                    desconto = c_descount.getDouble(c_descount.getColumnIndex("descount"));

                                }
                            }

                            Double valor_total = (valor_total_led + valor_total_mdo) - ((valor_total_led + valor_total_mdo) * desconto / 100);

                            row_total += "</tbody><tfoot><tr>" +
                                    "<td class='align_left border_thick_left border_thick_top border_thick_bottom commun'>Valor Total</td>" +
                                    "<td  class='align_right border_thick_right  border_thick_bottom border_thick_top commun short'>" + "R$ " + String.format(Locale.getDefault(), "%.2f", valor_total) + "</td>" +
                                    "</tr></tfoot></table>";

                            assert conteudo != null;
                            conteudo = conteudo.replace("{TABELATOTAL}", row_total);

                            Global global = new Global();
                            String nome_user = db.SelectNomeOfUserByUserandPass(global.getUsuario(), global.getSenha());

                            if (nome_user != null) {
                                conteudo = conteudo.replace("{RESPONSAVEL}", nome_user);
                            } else {
                                conteudo = conteudo.replace("{RESPONSAVEL}", "LedStock");
                            }

                            db.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // int padraoA4 = 596;
                        //int PaginaHeight = padraoA4;

                        //  if (NumberOfRows[0] > 1) {
                        //      PaginaHeight = padraoA4 + (int) (NumberOfRows[0] * 0.2 * 72);
                        // }

                        // Rectangle envelope = new Rectangle(843, PaginaHeight);
                        // step 1
                        Document document = new Document();
                        // step 2
                        //File path = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                        PdfWriter writer = null;

                        File out = new File(path.toString() + "/ledstock/" + "Orcamento_LedStock.pdf");
                        try {
                            if (!out.exists()) {
                                out.getParentFile().mkdirs();
                            }
                            writer = PdfWriter.getInstance(document, new FileOutputStream(out));
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            // e.printStackTrace();
                        }
                        // step 3
                        document.open();
                        document.newPage();

                        InputStream ims = getActivity().getResources().openRawResource(R.raw.logo);
                        Bitmap bmp = BitmapFactory.decodeStream(ims);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

                        try {
                            Image img = Image.getInstance(stream.toByteArray());
                            document.add(img);
                        } catch (BadElementException e1) {
                            e1.printStackTrace();
                        } catch (IOException e2) {
                            //e.printStackTrace();
                        } catch (DocumentException e3) {
                            //e.printStackTrace();
                            Log.e("ERROR", e3.getMessage());
                        }

                        try {
                            if (conteudo != null) {
                                InputStream ips = new ByteArrayInputStream(conteudo.getBytes("UTF-8"));

                                // step 4
                                XMLWorkerHelper.getInstance().parseXHtml(writer, document, ips);
                            /*new FileInputStream(IOUtils.toString(in, "UTF-8")));*/


                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //step 5
                        document.close();

                        progress.cancel();

                        try {
                            sleep(400);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(out), "application/pdf");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent);
                        }
                    }
                }.start();
                c_orcamento.close();
            } else {
                Toast.makeText(getActivity(), "O orçamento ainda não possui nenhum item !", Toast.LENGTH_SHORT).show();
            }
        }
        db.close();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
    }

}
