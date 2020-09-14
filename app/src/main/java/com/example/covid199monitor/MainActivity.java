package com.example.covid199monitor;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.DialogInterface;
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
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.bumptech.glide.Glide;

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
    }

    public void Scan() {
        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        findViewById(R.id.escaner).setEnabled(false);

        DataReadAsync read = new DataReadAsync();
        read.execute();
    }

    //------------------------------------------------------

    //Aqui se guarda toda la informacion de la base de datos
    public List<Paciente> pacientes = new ArrayList<>();

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
        int x = 0;

        @Override
        protected Object doInBackground(Object[] objects) {
            readCovidData();

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Toast.makeText(MainActivity.this, "Se escanearon " + Integer.toString(x) + " registros con exito", Toast.LENGTH_LONG).show();
            findViewById(R.id.loading).setVisibility(View.INVISIBLE);
            findViewById(R.id.explorar).setEnabled(true);

            super.onPostExecute(o);
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
            Log.d("DONE", "DONE");
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