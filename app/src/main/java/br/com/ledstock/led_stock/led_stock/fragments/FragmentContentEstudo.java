package br.com.ledstock.led_stock.led_stock.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
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
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
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
import br.com.ledstock.led_stock.R;
import br.com.ledstock.led_stock.led_stock.adapter.TabsEstudoAdapter;
import br.com.ledstock.led_stock.led_stock.domain.LedStockDB;
import br.com.ledstock.led_stock.led_stock.services.LedService;
import br.com.ledstock.led_stock.led_stock.utils.Global;
import br.com.ledstock.led_stock.led_stock.utils.IOUtils;

/**
 * Created by Gustavo on 21/10/2016.
 */

public class FragmentContentEstudo extends Fragment {

    private static long ID_ESTUDO;
    ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ID_ESTUDO = getArguments().getInt("id_estudo", 0);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_estudo, container, false);

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
        viewPager.setAdapter(new TabsEstudoAdapter(getContext(), fm));

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
                    intent.setAction("REFRESH_ESTATISTICAS_ESTUDO");
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
                                Dialog_AmbientedoEstudo_fragment.show(getChildFragmentManager(), ID_ESTUDO, getActivity());
                            } else if (tabLayout.getSelectedTabPosition() == 1) {
                                //Dialog_LED_fragment.show(getChildFragmentManager(), 0, getActivity());
                            }

                            /*
                                Dialog_Place_fragment.show(getChildFragmentManager(), 0, getActivity());
                            }else if (tabLayout.getSelectedTabPosition() == 4) {
                                Dialog_User_fragment.show(getChildFragmentManager(), 0, getActivity());
                            }*/
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
        inflater.inflate(R.menu.menu_estudo, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.pedido) {

            Dialog_Pedido_fragment.show(getChildFragmentManager(), String.valueOf(ID_ESTUDO), getActivity());

            return true;
        } else if (item.getItemId() == R.id.editar) {

            Dialog_Estudo_fragment.show(getChildFragmentManager(), 1, ID_ESTUDO, getActivity());

            return true;
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

            DeletarEstudoDialog.show(getFragmentManager(), new DeletarEstudoDialog.Callback() {
                public void onClickYes() {
                    //Instancia o Banco de Dados
                    LedStockDB delete_estudo = new LedStockDB(getActivity());
                    delete_estudo.DeleteEstudo(String.valueOf(ID_ESTUDO));

                    //Instancia o Serviço para Deletar Remotamente
                    LedService service = new LedService();
                    service.DeleteEstudoRemote(String.valueOf(ID_ESTUDO));

                    Intent intent = new Intent();
                    intent.setAction("REFRESH_ESTUDOS");
                    getActivity().sendBroadcast(intent);

                    //Fecha a Activity
                    getActivity().finish();
                }
            });

            return true;
        } else if (item.getItemId() == android.R.id.home) {
            /*
            Intent intent = new Intent(getContext(), Container_Main.class);
            // intent.putExtra("action", "del_client");
            startActivity(intent);
            getActivity().finish();*/
        }
        return super.onOptionsItemSelected(item);
    }

    private void GerarRelatorio() throws IOException, DocumentException {

        final int[] NumberOfRows = {0};

        LedStockDB db = new LedStockDB(getActivity());
        long ID_ESTUDO_REMOTE = db.SelectEstudoRemoteIDById(String.valueOf(ID_ESTUDO));
        Cursor c_lamps = db.SelectRelatorioLamps(String.valueOf(ID_ESTUDO), String.valueOf(ID_ESTUDO_REMOTE));

        if (c_lamps != null) {

            progress = new ProgressDialog(getActivity());
            progress.setMessage("Aguarde, carregando Relatório");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();

            new Thread() {
                public void run() {

                    String content = null;
                    String conteudo = null;
                    String sendereco, snum, scomp, sbairro, scidade, address;


                    //Este metodo consegue pegar a String do index.html
                    //Resources resources = getActivity().getResources();
                    //InputStream in = resources.openRawResource(R.raw.index);
                    //Log.e("TESTE","Caminho: " + IOUtils.toString(in, "UTF-8"));

                    Resources resources = getActivity().getResources();
                    InputStream in = resources.openRawResource(R.raw.index);

                    try {
                        //Captura todo conteudo do index.html
                        content = IOUtils.toString(in, "UTF-8");

                        LedStockDB db = new LedStockDB(getActivity());
                        Cursor c = db.SelectClienteOfEstudo(String.valueOf(ID_ESTUDO));

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

                            conteudo = conteudo.replace("{DATAESTUDO}", " " + date);
                            c.close();
                        }

                        String row_estudo = null;

                        long ID_ESTUDO_REMOTE = db.SelectEstudoRemoteIDById(String.valueOf(ID_ESTUDO));
                        Cursor c_lamps = db.SelectRelatorioLamps(String.valueOf(ID_ESTUDO), String.valueOf(ID_ESTUDO_REMOTE));

                        double investimento = 0.0;
                        double conta_atual = 0.0;
                        double conta_ideal = 0.0;
                        double economia = 0.0;
                        double retorno = 0.0;
                        double pot_lamp, pot_led;
                        int horas, quantidade;

                        double investimento_total = 0.0;
                        double conta_atual_total = 0.0;
                        double conta_ideal_total = 0.0;
                        double economia_total = 0.0;

                        DecimalFormat decimalFormat = new DecimalFormat("#.00");

                        double Preco_KWH = db.Select_KWh();

                        if (c_lamps != null) {

                            NumberOfRows[0] = c_lamps.getCount();

                            do {

                                investimento = c_lamps.getDouble(c_lamps.getColumnIndex("investimento"));

                                horas = c_lamps.getInt(c_lamps.getColumnIndex("horas"));
                                quantidade = c_lamps.getInt(c_lamps.getColumnIndex("quantidade"));

                                pot_lamp = c_lamps.getDouble(c_lamps.getColumnIndex("pot_lamp"));
                                conta_atual = (((pot_lamp / 1000) * horas * 30) * Preco_KWH) * quantidade;

                                pot_led = c_lamps.getDouble(c_lamps.getColumnIndex("pot_led"));
                                conta_ideal = (((pot_led / 1000) * horas * 30) * Preco_KWH) * quantidade;

                                economia = conta_atual - conta_ideal;

                                investimento_total += investimento;
                                conta_atual_total += conta_atual;
                                conta_ideal_total += conta_ideal;
                                economia_total += economia;
                                retorno = investimento / economia;

                                row_estudo += "<tr>" +
                                        "<td class='align_left border_thick_left commun'>" + c_lamps.getString(c_lamps.getColumnIndex("descricao")) + "</td>" +
                                        "<td class='align_left commun'>" + c_lamps.getString(c_lamps.getColumnIndex("lamp")) + "</td>" +
                                        "<td class='align_left commun'>" + c_lamps.getString(c_lamps.getColumnIndex("led")) + "</td>" +
                                        "<td  class='align_center commun'>" + c_lamps.getString(c_lamps.getColumnIndex("quantidade")) + "</td>" +
                                        "<td  class='align_center commun'>" + c_lamps.getString(c_lamps.getColumnIndex("horas")) + "</td>" +
                                        "<td  class='align_right border_thick_left commun'>" + c_lamps.getString(c_lamps.getColumnIndex("valor")) + "</td>" +
                                        "<td  class='align_right border_thick_right commun'>" + decimalFormat.format(investimento) + "</td>" +
                                        "<td  class='align_right commun'>" + decimalFormat.format(conta_atual) + "</td>" +
                                        "<td  class='align_right commun'>" + decimalFormat.format(conta_ideal) + "</td>" +
                                        "<td  class='align_right commun'>" + decimalFormat.format(economia) + "</td>" +
                                        "<td  class='align_center border_thick_right commun'>" + decimalFormat.format(retorno) + "</td>" +
                                        "</tr>";

                            } while (c_lamps.moveToNext());

                            assert conteudo != null;
                            conteudo = conteudo.replace("{CONTEUDO_LAMPADAS}", row_estudo);

                            conteudo = conteudo.replace("{INVEST}", "R$ " + decimalFormat.format(investimento_total));
                            conteudo = conteudo.replace("{CATUAL}", "R$ " + decimalFormat.format(conta_atual_total));
                            conteudo = conteudo.replace("{CIDEAL}", "R$ " + decimalFormat.format(conta_ideal_total));
                            conteudo = conteudo.replace("{ECON}", "R$ " + decimalFormat.format(economia_total));
                            conteudo = conteudo.replace("{RETM}", decimalFormat.format(investimento_total / economia_total));
                            conteudo = conteudo.replace("{KWH}", String.valueOf(Preco_KWH));
                            c_lamps.close();
                        }

                        Cursor c_handson = db.SelectRelatorioHandsOn(String.valueOf(ID_ESTUDO), String.valueOf(ID_ESTUDO_REMOTE));

                        if (c_handson != null) {

                            String row_handson = "<table class='table_content mdo' cellspacing='0'>" + "<thead><tr>" +
                                    "<td class='noborder' colspan='2'>&nbsp;</td>" +
                                    "<td class='border_thick_left border_thick_top border_thick_right title mao' colspan='3'><b>Mão de Obra</b></td>" +
                                    "</tr>" +
                                    "<tr class='header'>" +
                                    "<td class='border_thick_left border_thick_top border_thick_bottom long'>Ambiente</td>" +
                                    "<td class='border_thick_top border_thick_bottom extra_long'>Mão de Obra</td>" +
                                    "<td class='border_thick_left border_thick_top border_thick_bottom'>Quant.</td>" +
                                    "<td class='border_thick_top border_thick_bottom'>V. UNIT.</td>" +
                                    "<td class='border_thick_right border_thick_top border_thick_bottom'>V. TOTAL</td>" +
                                    "</tr>" +
                                    "</thead><tbody>";

                            NumberOfRows[0] += c_handson.getCount();

                            double valor_total = 0.0;

                            do {
                                valor_total += c_handson.getDouble(c_handson.getColumnIndex("valor_total"));

                                row_handson += "<tr>" +
                                        "<td class='align_left border_thick_left commun'>" + c_handson.getString(c_handson.getColumnIndex("descricao")) + "</td>" +
                                        "<td class='align_left commun'>" + c_handson.getString(c_handson.getColumnIndex("maodeobra")) + "</td>" +
                                        "<td  class='align_center border_thick_left commun'>" + c_handson.getString(c_handson.getColumnIndex("quantidade")) + "</td>" +
                                        "<td  class='align_right commun'>" + decimalFormat.format(c_handson.getDouble(c_handson.getColumnIndex("valor"))) + "</td>" +
                                        "<td  class='align_right border_thick_right commun'>" + decimalFormat.format(c_handson.getDouble(c_handson.getColumnIndex("valor_total"))) + "</td>" +
                                        "</tr>";

                            } while (c_handson.moveToNext());

                            row_handson += "</tbody><tfoot>" +
                                    "<tr>" +
                                    "<td class='border_thick_left border_thick_bottom border_thick_top total' colspan='2'><b>TOTAL</b></td>" +
                                    "<td class='border_thick_bottom border_thick_left cell_null border_thick_top'>&nbsp;</td>" +
                                    "<td class='align_right border_thick_bottom border_thick_top border_thick_left cell_null'>&nbsp;</td>" +
                                    "<td class='align_right border_thick_right border_thick_bottom border_thick_top border_thick_left'><b> R$ " + decimalFormat.format(valor_total) + "</b></td>" +
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
                    int padraoA4 = 596;
                    int PaginaHeight = padraoA4;

                    if (NumberOfRows[0] > 1) {
                        PaginaHeight = padraoA4 + (int) (NumberOfRows[0] * 0.2 * 72);
                    }

                    Rectangle envelope = new Rectangle(843, PaginaHeight);
                    // step 1
                    Document document = new Document(envelope);
                    // step 2
                    //File path = getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File path = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                    PdfWriter writer = null;

                    File out = new File(path.toString() + "/ledstock/" + "Estudo_LedStock.pdf");
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
                    }finally {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(out), "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                }
            }.start();
            c_lamps.close();
        } else {
            Toast.makeText(getActivity(), "O Estudo ainda não possui nenhum item !", Toast.LENGTH_SHORT).show();
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
