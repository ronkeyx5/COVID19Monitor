package com.example.covid199monitor;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPOutputStream;

import com.anychart.core.annotations.Line;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    EditText busquedaBox;
    ImageButton busquedaButton;
    ImageButton clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        busquedaBox = findViewById(R.id.searchBox);
        busquedaButton = findViewById(R.id.searchButton);

        Button b = findViewById(R.id.escaner);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Scan();
            }
        });

        Button button_casos = findViewById(R.id.button_casos);
        button_casos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPieChartC("1");
            }
        });

        clearButton = findViewById(R.id.busqueda_clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LimpiarBusqueda();
            }
        });

        Button button_fallecimientos = findViewById(R.id.button_fallecimientos);
        button_fallecimientos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPieChartC("2");
            }
        });

        busquedaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Busqueda();
                hideKeyboard();
            }
        });

        /*busquedaBox.setKeyListener(new KeyListener() {
            @Override
            public int getInputType() {
                return 0;
            }
            @Override
            public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_ENTER){
                    Busqueda();
                }

                return false;
            }

            @Override
            public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
                return false;
            }

            @Override
            public boolean onKeyOther(View view, Editable text, KeyEvent event) {
                return false;
            }

            @Override
            public void clearMetaKeyState(View view, Editable content, int states) {

            }
        });*/
    }

    private void LimpiarBusqueda() {
        clearButton.setVisibility(View.GONE);
        findViewById(R.id.contentDisplay).setVisibility(View.VISIBLE);
        //findViewById(R.id.busquedas).setVisibility(View.INVISIBLE);

        TextView b_casos = findViewById(R.id.busqueda_casos);
        TextView b_fallecimientos = findViewById(R.id.busqueda_fallecimientos);
        findViewById(R.id.busqueda_estado_completo).setVisibility(View.GONE);

        b_casos.setVisibility(View.GONE); b_fallecimientos.setVisibility(View.GONE);
        busquedaBox.getText().clear();
    }

    private void ViewPieChartC(String q) {
        Intent i = new Intent(this, PieChart.class);
        i.putExtra("type", q);
        i.putExtra("estados", (Serializable)estados);

        startActivity(i);
    }

    public void Scan() {
        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        findViewById(R.id.escaner).setEnabled(false);

        DataReadAsync read = new DataReadAsync();
        read.execute();
    }

    //------------------------------------------------------
    //LECTURA DE LOS DATOS

    //Aqui se guarda toda la informacion de la base de datos
    public List<Paciente> pacientes = new ArrayList<>();
    //Variables globales
    private String fecha = "";
    int x = 0;
    int casos_positivos = 0;
    int fallecimientos = 0;
    public List<Estado> estados = new ArrayList<>();
    List<Integer> edadProm = new ArrayList<>();

    class DataReadAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            readCovidData();

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Toast.makeText(MainActivity.this, "Se escanearon " + Integer.toString(x) + " registros con exito", Toast.LENGTH_LONG).show();
            findViewById(R.id.loading).setVisibility(View.GONE);
            findViewById(R.id.escaner).setVisibility(View.GONE);
            findViewById(R.id.searchZone).setVisibility(View.VISIBLE);
            findViewById(R.id.info).setVisibility(View.GONE);
            findViewById(R.id.contentDisplay).setVisibility(View.VISIBLE);

            //Mostrar info dentro del view
            TextView fechaT = findViewById(R.id.fecha);
            fechaT.setText(fecha);

            TextView casosT = findViewById(R.id.casos);
            casosT.setText(Integer.toString(casos_positivos));

            TextView fallecimientosT = findViewById(R.id.fallecimientos);
            fallecimientosT.setText(Integer.toString(fallecimientos));

            TextView estadoT = findViewById(R.id.estado);
            estadoT.setText(EstadoMayorCasos());

            int i=0;
            while(i < estados.size()){
                Log.d("control", estados.get(i).getNombre() + ", " + estados.get(i).getCasos() + ", f " + estados.get(i).getFallecimientos());
                i++;
            }

            super.onPostExecute(o);
        }

        private String EstadoMayorCasos() {
            int i=0, w=0;

            while (i<estados.size()){
                if(estados.get(i).getCasos()>estados.get(w).getCasos()){
                    w=i;
                }

                i++;
            }

            return estados.get(w).getNombre();
        }

        private void readCovidData() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Abrimos el archivo "covid.csv" dentro de la carpeta raw de la app para flujo i/o
            InputStream file = getResources().openRawResource(R.raw.covid);
            //Buffer para poder manipular los datos dentro del archivo
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file, Charset.forName("UTF-8"))
            );

            String line = "";

            try {
                reader.readLine();

                //Leyendo linea por linea y agregando como paciente a la lista
                while ((line = reader.readLine()) != null) {
                    //Diviendo por delimitadores
                    String[] campos = line.split(",");

                    //Guardando los datos en un elemento "Paciente"
                    Paciente p = new Paciente();
                    Estado e = new Estado();
                    int ex = 0;
                    boolean posC = false;
                    boolean posF = false;

                    fecha = campos[0];

                    p.setId(campos[1]);
                    p.setEntidad_i(Integer.parseInt(campos[4]));
                    p.setEntidad(EntidadNombre(Integer.parseInt(campos[4])));
                    e.setNombre(EntidadNombre(Integer.parseInt(campos[4])));
                    p.setSexo(Genero(Integer.parseInt(campos[5])));
                    p.setFecha_ingreso(campos[10]);
                    p.setFecha_defuncion(campos[12]);
                    p.setDefuncion(ToBoolDate(campos[12]));
                    p.setEdad(Integer.parseInt(campos[15]));

                    p.setEmbarazo(ToBool(campos[17]));
                    p.setIntubado(ToBool(campos[13]));
                    p.setNeumonia(ToBool(campos[14]));
                    p.setDiabetes(ToBool(campos[19]));
                    p.setEpoc(ToBool(campos[20]));
                    p.setAsma(ToBool(campos[21]));
                    p.setInmunosupresion(ToBool(campos[22]));
                    p.setHipertension(ToBool(campos[23]));
                    p.setCardiovascular(ToBool(campos[25]));
                    p.setObesidad(ToBool(campos[26]));
                    p.setRenal_cronica(ToBool(campos[27]));
                    p.setTabaquismo(ToBool(campos[28]));

                    p.setResultado(ToBool(campos[30]));

                    p.setCuidados_intensivos(ToBool(campos[34]));

                    pacientes.add(p);
                    x++;

                    if(ToBool(campos[30])) {
                        casos_positivos++;
                        posC=true;
                    }

                    if(ToBoolDate(campos[12])) {
                        fallecimientos++;
                        //Log.d("F", "fallecimiento" + fallecimientos);
                        posF=true;
                    }

                    ex = CheckEstado(EntidadNombre(Integer.parseInt(campos[4])));
                    //existente, actualizar
                    if(ex != -1){
                        estados.get(ex).setCasos(estados.get(ex).getCasos()+1);
                        if(posC){
                            estados.get(ex).setCasos_positivos(estados.get(ex).getCasos_positivos()+1);
                        }
                        if(posF){
                            estados.get(ex).setFallecimientos(estados.get(ex).getFallecimientos()+1);
                        }
                    }
                    //inexistente, agregar
                    else {
                        e.setCasos(1);
                        if(posC)
                            e.setCasos_positivos(1);
                        if(posF)
                            e.setFallecimientos(1);

                        estados.add(e);
                    }

                    //Edad
                    edadProm.add(p.getEdad());

                    /*
                    //Genero
                    if(p.getSexo()=='M') {
                        estados.get(p.getEntidad_i()).setHombres(estados.get(p.getEntidad_i()).getHombres()+1);

                    }
                    else {
                        estados.get(p.getEntidad_i()).setMujeres(estados.get(p.getEntidad_i()).getMujeres()+1);
                    }

                    //Padecimientos
                    if(p.isEmbarazo())
                        estados.get(p.getEntidad_i()).setEmbarazo(estados.get(p.getEntidad_i()).getEmbarazo()+1);
                    if(p.isIntubado())
                        estados.get(p.getEntidad_i()).setIntubado(estados.get(p.getEntidad_i()).getIntubado()+1);
                    if(p.isNeumonia())
                        estados.get(p.getEntidad_i()).setNeumonia(estados.get(p.getEntidad_i()).getNeumonia()+1);
                    if(p.isDiabetes())
                        estados.get(p.getEntidad_i()).setDiabetes(estados.get(p.getEntidad_i()).getDiabetes()+1);
                    if(p.isEpoc())
                        estados.get(p.getEntidad_i()).setEpoc(estados.get(p.getEntidad_i()).getEpoc()+1);
                    if(p.isAsma())
                        estados.get(p.getEntidad_i()).setAsma(estados.get(p.getEntidad_i()).getAsma()+1);
                    if(p.isInmunosupresion())
                        estados.get(p.getEntidad_i()).setInmunosupresion(estados.get(p.getEntidad_i()).getInmunosupresion()+1);
                    if(p.isHipertension())
                        estados.get(p.getEntidad_i()).setHipertension(estados.get(p.getEntidad_i()).getHipertension()+1);
                    if(p.isCardiovascular())
                        estados.get(p.getEntidad_i()).setCardiovascular(estados.get(p.getEntidad_i()).getCardiovascular()+1);
                    if(p.isObesidad())
                        estados.get(p.getEntidad_i()).setObesidad(estados.get(p.getEntidad_i()).getObesidad()+1);
                    if(p.isRenal_cronica())
                        estados.get(p.getEntidad_i()).setRenal_cronica(estados.get(p.getEntidad_i()).getRenal_cronica()+1);
                    if(p.isTabaquismo())
                        estados.get(p.getEntidad_i()).setTabaquismo(estados.get(p.getEntidad_i()).getTabaquismo()+1);
                        */
                }
            } catch (IOException e) {
                Log.wtf("MyActivity","Error al leer el archivo en la linea " + line, e);
                e.printStackTrace();
            }
            Log.d("COUNT", "########### "+Integer.toString(x)+" ###########");
            Log.d("DONE", "DONE");

            DatosPorEstado();
        }

        private void DatosPorEstado() {
            Log.d("control", Integer.toString(pacientes.size()));
            boolean posC;
            for(int i=0; i<pacientes.size(); i++)   {
                //Log.d("control", Integer.toString(i));
                //Genero
                posC = pacientes.get(i).isResultado();
                int ef;
                ef=CheckEstado(pacientes.get(i).getEntidad());

                if(posC && pacientes.get(i).getSexo()=='M') {
                    estados.get(ef).setHombres(estados.get(ef).getHombres()+1);
                }
                else if(posC && pacientes.get(i).getSexo()=='F') {
                    estados.get(ef).setMujeres(estados.get(ef).getMujeres()+1);
                }

                //Padecimientos
                if(pacientes.get(i).isEmbarazo())
                    estados.get(ef).setEmbarazo(estados.get(ef).getEmbarazo()+1);
                if(pacientes.get(i).isIntubado())
                    estados.get(ef).setIntubado(estados.get(ef).getIntubado()+1);
                if(pacientes.get(i).isNeumonia())
                    estados.get(ef).setNeumonia(estados.get(ef).getNeumonia()+1);
                if(pacientes.get(i).isDiabetes())
                    estados.get(ef).setDiabetes(estados.get(ef).getDiabetes()+1);
                if(pacientes.get(i).isEpoc())
                    estados.get(ef).setEpoc(estados.get(ef).getEpoc()+1);
                if(pacientes.get(i).isAsma())
                    estados.get(ef).setAsma(estados.get(ef).getAsma()+1);
                if(pacientes.get(i).isInmunosupresion())
                    estados.get(ef).setInmunosupresion(estados.get(ef).getInmunosupresion()+1);
                if(pacientes.get(i).isHipertension())
                    estados.get(ef).setHipertension(estados.get(ef).getHipertension()+1);
                if(pacientes.get(i).isCardiovascular())
                    estados.get(ef).setCardiovascular(estados.get(ef).getCardiovascular()+1);
                if(pacientes.get(i).isObesidad())
                    estados.get(ef).setObesidad(estados.get(ef).getObesidad()+1);
                if(pacientes.get(i).isRenal_cronica())
                    estados.get(ef).setRenal_cronica(estados.get(ef).getRenal_cronica()+1);
                if(pacientes.get(i).isTabaquismo())
                    estados.get(ef).setTabaquismo(estados.get(ef).getTabaquismo()+1);
            }
        }

        private Boolean ToBoolDate(String campo) {
            if(campo.equals("0")) {
                //Log.d("fall", "false");
                return false;
            }
            else{
                //Log.d("fall", "true");
                return true;
            }
        }

        private int CheckEstado(String entidadNombre) {
            int i=0;
            while(i < estados.size()){
                if(entidadNombre.equals(estados.get(i).getNombre())) {
                    return i;
                }
                i++;
            }
            return -1;
        }

        private boolean ToBool(String campo) {
            int f = Integer.parseInt(campo);

            if(f==1)
                return true;
            else
                return false;
        }

        private Character Genero(int i) {
            if(i==1)
                return 'M';
            else
                return 'F';
        }

        private String EntidadNombre(int i) {
            String[] entidad = {"Aguascalientes", "Baja California", "Baja California Sur", "Campeche", "Coahuila", "Colima", "Chiapas", "Chihuahua", "Ciudad de Mexico", "Durango", "Nuevo Leon", "Guerrero", "Hidalgo", "Jalisco", "Estado de Mexico", "Michoacan", "Morelos", "Nayarit", "Guanajuato", "Oaxaca", "Puebla", "Queretaro", "Quintana Roo", "San Luis Potosi", "Sinaloa", "Sonora", "Tabasco", "Tamaulipas", "Tlaxcala", "Veracruz", "Yucatan", "Zacatecas"};

            return entidad[i-1];
        }
    }

    //------------------------------------------------------
    //BUSQUEDA

    private void Busqueda() {
        //TODO
        //Invisible cada uno
        TextView b_casos = findViewById(R.id.busqueda_casos);
        TextView b_fallecimientos = findViewById(R.id.busqueda_fallecimientos);
        LinearLayout content_display = findViewById(R.id.contentDisplay);
        findViewById(R.id.busqueda_estado_completo).setVisibility(View.GONE);
        clearButton.setVisibility(View.GONE);
        b_casos.setVisibility(View.GONE); b_fallecimientos.setVisibility(View.GONE); content_display.setVisibility(View.GONE);

        clearButton.setVisibility(View.VISIBLE);

        String busqueda = busquedaBox.getText().toString();
        String[] padecimientos = {"intubado", "embarazo", "neumonia", "diabetes", "epoc", "asma", "inmunosupresion", "hipertension", "cardiovascular", "obesidad", "renal", "tabaquismo"};

        List<Paciente> resultados = new ArrayList<>();

        Log.d("control", "BUSQUEDA: " + busqueda);

        //Casos/CasosPositivos/Fallecimientos
        if(busqueda.equalsIgnoreCase("casos")){
            b_casos.setVisibility(View.VISIBLE);
            b_casos.setText("Casos: " + x + "\n\n Casos Positivos: " + casos_positivos);
            return;
        }
        else if(busqueda.equalsIgnoreCase("fallecimientos"))  {
            b_fallecimientos.setVisibility(View.VISIBLE);
            b_fallecimientos.setText("Fallecimientos: " + fallecimientos);
            return;
        }

        //Estados
        for(int i=0; i<estados.size(); i++) {
            if(busqueda.equalsIgnoreCase(estados.get(i).getNombre())) {
                Log.d("busqueda", estados.get(i).getNombre() + " || " + busqueda);

                //Tabla de estado
                TextView nombre, a,b,c,d,e,f,g,h,j,k,l,m,n,o,p;
                a=findViewById(R.id.busqueda_est_casos); b=findViewById(R.id.busqueda_est_fallecimietos); c=findViewById(R.id.busqueda_est_hombres);
                d=findViewById(R.id.busqueda_est_mujeres); e=findViewById(R.id.busqueda_est_embarazos); f=findViewById(R.id.busqueda_est_intubados);
                g=findViewById(R.id.busqueda_est_neumonia); h=findViewById(R.id.busqueda_est_epoc); j=findViewById(R.id.busqueda_est_asma);
                k=findViewById(R.id.busqueda_est_inmunosupresion); l=findViewById(R.id.busqueda_est_hipertension); m=findViewById(R.id.busqueda_est_cardiovascular);
                n=findViewById(R.id.busqueda_est_obesidad); o=findViewById(R.id.busqueda_est_renal); p=findViewById(R.id.busqueda_est_tabaquismo);
                nombre=findViewById(R.id.busqueda_est_nombre);

                nombre.setText(estados.get(i).getNombre());
                a.setText(Integer.toString(estados.get(i).getCasos_positivos()));
                b.setText(Integer.toString(estados.get(i).getFallecimientos()));
                //Hombres | Mujeres
                c.setText(Integer.toString(estados.get(i).getHombres())); d.setText(Integer.toString(estados.get(i).getMujeres()));
                e.setText(Integer.toString(estados.get(i).getEmbarazo())); f.setText(Integer.toString(estados.get(i).getIntubado())); g.setText(Integer.toString(estados.get(i).getNeumonia()));
                h.setText(Integer.toString(estados.get(i).getEpoc())); j.setText(Integer.toString(estados.get(i).getAsma())); k.setText(Integer.toString(estados.get(i).getInmunosupresion()));
                l.setText(Integer.toString(estados.get(i).getHipertension())); m.setText(Integer.toString(estados.get(i).getCardiovascular())); n.setText(Integer.toString(estados.get(i).getObesidad()));
                o.setText(Integer.toString(estados.get(i).getRenal_cronica())); p.setText(Integer.toString(estados.get(i).getTabaquismo()));

                findViewById(R.id.busqueda_estado_completo).setVisibility(View.VISIBLE);

                return;
            }
        }

        //Todos los estados
        if(busqueda.equalsIgnoreCase("estados"))  {

            return;
        }

        //Padecimientos
        for(int i=0; i<padecimientos.length; i++)   {
            if(busqueda.equalsIgnoreCase(padecimientos[i]) || busqueda.contains(padecimientos[i]))    {
                //Resultados de padecimiento

                return;
            }
        }

        //Todos los padecimientos
        if(busqueda.equalsIgnoreCase("padecimientos"))    {

            return;
        }

        //ID

    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}