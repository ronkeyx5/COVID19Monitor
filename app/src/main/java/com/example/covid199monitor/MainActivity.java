package com.example.covid199monitor;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        Button button_fallecimientos = findViewById(R.id.button_fallecimientos);
        button_fallecimientos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPieChartC("2");
            }
        });
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

    //Analiza los datos de la pantalla principal
    private void TopData() {

    }

    //------------------------------------------------------

    //Aqui se guarda toda la informacion de la base de datos
    public List<Paciente> pacientes = new ArrayList<>();
    //Variables globales
    private String fecha = "";
    int x = 0;
    int casos_positivos = 0;
    int fallecimientos = 0;
    public List<Estado> estados = new ArrayList<>();

    /*
    Reemplazado por Async

    private void readCovidData() {
        int x = 0;

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

                p.setId(campos[1]);
                p.setEntidad_i(Integer.parseInt(campos[4]));
                p.setEntidad(EntidadNombre(Integer.parseInt(campos[4])));
                p.setSexo(Genero(Integer.parseInt(campos[5])));
                p.setFecha_ingreso(campos[10]);
                p.setFecha_defuncion(campos[12]);
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
                //Log.d("CONTROL", "########## "+p.getId()+" ###########");
            }
        } catch (IOException e) {
            Log.wtf("MyActivity","Error al leer el archivo en la linea " + line, e);
            e.printStackTrace();
        }
        Log.d("COUNT", "########### "+Integer.toString(x)+" ###########");

        //TextView t = (TextView)findViewById(R.id.f);
        //t.setText("Se escanearon " + Integer.toString(x) + " registros exitosamente");

        Toast.makeText(this, "Se escanearon " + Integer.toString(x) + " registros con exito", Toast.LENGTH_LONG).show();
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
            return 'M';
    }

    private String EntidadNombre(int i) {
        String[] entidad = {"Aguascalientes", "Baja California", "Baja California Sur", "Campeche", "Coahuila", "Colima", "Chiapas", "Chihuahua", "Ciudad de Mexico", "Durango", "Nuevo Leon", "Guerrero", "Hidalgo", "Jalisco", "Estado de Mexico", "Michoacan", "Morelos", "Nayarit", "Guanajuato", "Oaxaca", "Puebla", "Queretaro", "Quintana Roo", "San Luis Potosi", "Sinaloa", "Sonora", "Tabasco", "Tamaulipas", "Tlaxcala", "Veracruz", "Yucatan", "Zacatecas"};

        return entidad[i-1];
    }
    */

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
                        Log.d("F", "fallecimiento" + fallecimientos);
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
                }
            } catch (IOException e) {
                Log.wtf("MyActivity","Error al leer el archivo en la linea " + line, e);
                e.printStackTrace();
            }
            Log.d("COUNT", "########### "+Integer.toString(x)+" ###########");
            Log.d("DONE", "DONE");
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
                return 'M';
        }

        private String EntidadNombre(int i) {
            String[] entidad = {"Aguascalientes", "Baja California", "Baja California Sur", "Campeche", "Coahuila", "Colima", "Chiapas", "Chihuahua", "Ciudad de Mexico", "Durango", "Nuevo Leon", "Guerrero", "Hidalgo", "Jalisco", "Estado de Mexico", "Michoacan", "Morelos", "Nayarit", "Guanajuato", "Oaxaca", "Puebla", "Queretaro", "Quintana Roo", "San Luis Potosi", "Sinaloa", "Sonora", "Tabasco", "Tamaulipas", "Tlaxcala", "Veracruz", "Yucatan", "Zacatecas"};

            return entidad[i-1];
        }
    }
}