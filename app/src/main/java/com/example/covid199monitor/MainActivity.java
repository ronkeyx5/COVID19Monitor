package com.example.covid199monitor;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

//org.apache.commons.lang.StringUtils.

public class MainActivity extends AppCompatActivity {

    //Variables globales
    EditText busquedaBox;
    ImageButton busquedaButton;
    ImageButton clearButton;

    private static String file_url = "http://datosabiertos.salud.gob.mx/gobmx/salud/datos_abiertos/datos_abiertos_covid19.zip";
    //private static String file_url = "https://pbs.twimg.com/media/EfbAPOQXoAAsYCM.jpg";

    //Aqui se guarda toda la informacion de la base de datos
    public List<Paciente> pacientes = new ArrayList<>();
    private String fecha = "";
    int x = 0;
    int casos_positivos = 0;
    int fallecimientos = 0;
    public List<Estado> estados = new ArrayList<>();
    List<Integer> edadProm = new ArrayList<>();
    List<Padecimiento> padecimientos = new ArrayList<>();
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        busquedaBox = findViewById(R.id.searchBox);
        busquedaButton = findViewById(R.id.searchButton);

        Button descargar = findViewById(R.id.descargarButton);

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

        descargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Download();
            }
        });

        Test();
        CheckFile();
    }

    private void CheckFile() {
        String root = Environment.getExternalStorageDirectory().toString() + "/COVID19Monitor/";
        File file = new File(root, "covid.csv");

        if (file.exists()) {
            //Leer

            File fileEx = new File(Environment.getExternalStorageDirectory().toString() + "/COVID19Monitor/", "covid.csv");

            //Buffer para poder manipular los datos dentro del archivo
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(fileEx));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String line = "";


            //Leyendo linea por linea y agregando como paciente a la lista

            try {
                reader.readLine();

                while ((line = reader.readLine()) != null) {
                    String[] campos = line.split(",");

                    TextView t = findViewById(R.id.fecha_in);
                    t.setText("Fecha de los datos: " + campos[0]);
                    t.setVisibility(View.VISIBLE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            findViewById(R.id.escaner).setEnabled(false);
        }


    }

    private void Test() {

    }

    private void LimpiarBusqueda() {
        clearButton.setVisibility(View.GONE);
        findViewById(R.id.contentDisplay).setVisibility(View.VISIBLE);
        //findViewById(R.id.busquedas).setVisibility(View.INVISIBLE);

        TextView b_casos = findViewById(R.id.busqueda_casos);
        TextView b_fallecimientos = findViewById(R.id.busqueda_fallecimientos);
        findViewById(R.id.busqueda_estado_completo).setVisibility(View.GONE);

        b_casos.setVisibility(View.GONE);
        b_fallecimientos.setVisibility(View.GONE);
        busquedaBox.getText().clear();

        findViewById(R.id.busqueda_padecimientos).setVisibility(View.GONE);
    }

    private void ViewPieChartC(String q) {
        Intent i = new Intent(this, PieChart.class);
        i.putExtra("type", q);
        i.putExtra("estados", (Serializable) estados);

        startActivity(i);
    }

    public void Scan() {
        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        findViewById(R.id.escaner).setEnabled(false);

        DataReadAsync read = new DataReadAsync();
        read.execute();
    }

    public void Download() {
        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        findViewById(R.id.escaner).setEnabled(false);
        findViewById(R.id.descargarButton).setEnabled(false);

        new DownloadAsync().execute(file_url);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Descargando datos, espere...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    //DESCARGA DE DATOS
    //------------------------------------------------------
    class DownloadAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                //OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+ "/2011.kml");
                String root = Environment.getExternalStorageDirectory().toString();
                OutputStream output = new FileOutputStream(root + "/COVID19Monitor/covid.zip");


                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            dismissDialog(progress_bar_type);
            //Toast.makeText(MainActivity.this, "Extrayendo...", Toast.LENGTH_LONG).show();
            Extraccion1();

            Log.d("Extraccion", "Done");

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            findViewById(R.id.loading).setVisibility(View.GONE);
            findViewById(R.id.escaner).setEnabled(true);

            Renombrar();

            //TOAST
            Toast.makeText(MainActivity.this, "Datos listos para escaneo", Toast.LENGTH_LONG).show();

            super.onPostExecute(file_url);
        }

        @SuppressLint("NewApi")
        private void Renombrar() {
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");

            Log.d("dateForm", formatter.format(today));
            Log.d("dateForm", formatter.format(yesterday));

            String file_name_today = formatter.format(today) + "COVID19MEXICO.csv";
            String file_name_yesterday = formatter.format(yesterday) + "COVID19MEXICO.csv";

            Log.d("FILE name", file_name_today);
            Log.d("FILE name", file_name_yesterday);

            //renombrar
            String root = Environment.getExternalStorageDirectory().toString() + "/COVID19Monitor/";

            File from_t = new File(root, file_name_today);
            File to_t = new File(root, "covid.csv");

            if (from_t.exists()) {
                from_t.renameTo(to_t);
                Log.d("FILE1", "YES");
                return;
            } else {
                Log.d("FILE1", "NO");
            }

            File from_y = new File(root, file_name_yesterday);
            File to_y = new File(root, "covid.csv");

            if (from_y.exists()) {
                from_y.renameTo(to_y);
                Log.d("FILE2", "YES");
                return;
            } else {
                Log.d("FILE2", "NO");
            }

        }

        private boolean Extraer(String path, String zipname) {
            InputStream is;
            ZipInputStream zis;
            try {
                is = new FileInputStream(path + zipname);
                zis = new ZipInputStream(new BufferedInputStream(is));
                ZipEntry ze;

                while ((ze = zis.getNextEntry()) != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int count;

                    String filename = ze.getName();
                    FileOutputStream fout = new FileOutputStream(path + filename);

                    // reading and writing
                    while ((count = zis.read(buffer)) != -1) {
                        baos.write(buffer, 0, count);
                        byte[] bytes = baos.toByteArray();
                        fout.write(bytes);
                        baos.reset();
                    }

                    fout.close();
                    zis.closeEntry();
                }

                Log.d("Control", "Done");
                zis.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        private void Extraccion1() {
            String root = Environment.getExternalStorageDirectory().toString() + "/COVID19Monitor/";
            String f_name = "covid.zip";
            Extraer(root, f_name);
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }
    }


    //LECTURA DE LOS DATOS
    //------------------------------------------------------

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
            findViewById(R.id.descargarButton).setVisibility(View.GONE);
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

            int i = 0;
            while (i < estados.size()) {
                Log.d("control", estados.get(i).getNombre() + ", " + estados.get(i).getCasos() + ", f " + estados.get(i).getFallecimientos());
                i++;
            }

            super.onPostExecute(o);
        }

        private String EstadoMayorCasos() {
            int i = 0, w = 0;

            while (i < estados.size()) {
                if (estados.get(i).getCasos() > estados.get(w).getCasos()) {
                    w = i;
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

            //covid.csv from RAW
            //InputStream file = getResources().openRawResource(R.raw.covid);
            /*
            File f = new File(Environment.getExternalStorageDirectory().toString() + "/COVID19Monitor/", "covid.csv");
            InputStream file = null;
            try {
                file = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            */
            File file = new File(Environment.getExternalStorageDirectory().toString() + "/COVID19Monitor/", "covid.csv");

            //Buffer para poder manipular los datos dentro del archivo
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String line = "";
            Padecimientos();

            try {
                reader.readLine();

                //Leyendo linea por linea y agregando como paciente a la lista
                while ((line = reader.readLine()) != null) {
                    //Diviendo por delimitadores
                    String[] campos = line.split(",");

                    for (int q = 0; q < campos.length; q++) {
                        campos[q] = campos[q].replace("\"", "");
                    }

                    //Guardando los datos en un elemento "Paciente"
                    Paciente p = new Paciente();
                    Estado e = new Estado();
                    int ex = 0;
                    boolean posC = false;
                    boolean posF = false;

                    boolean auxP = false;

                    int aux_i[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

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

                    //String[] padecimientos_s = {"intubado", "embarazo", "neumonia", "diabetes", "epoc", "asma", "inmunosupresion", "hipertension", "cardiovascular", "obesidad", "renal", "tabaquismo"};
                    p.setIntubado(auxP = ToBool(campos[13]));
                    if (auxP) aux_i[0] = 1;
                    p.setEmbarazo(auxP = ToBool(campos[17]));
                    if (auxP) aux_i[1] = 1;
                    p.setNeumonia(auxP = ToBool(campos[14]));
                    if (auxP) aux_i[2] = 1;
                    p.setDiabetes(auxP = ToBool(campos[19]));
                    if (auxP) aux_i[3] = 1;
                    p.setEpoc(auxP = ToBool(campos[20]));
                    if (auxP) aux_i[4] = 1;
                    p.setAsma(auxP = ToBool(campos[21]));
                    if (auxP) aux_i[5] = 1;
                    p.setInmunosupresion(auxP = ToBool(campos[22]));
                    if (auxP) aux_i[6] = 1;
                    p.setHipertension(auxP = ToBool(campos[23]));
                    if (auxP) aux_i[7] = 1;
                    p.setCardiovascular(auxP = ToBool(campos[25]));
                    if (auxP) aux_i[8] = 1;
                    p.setObesidad(auxP = ToBool(campos[26]));
                    if (auxP) aux_i[9] = 1;
                    p.setRenal_cronica(auxP = ToBool(campos[27]));
                    if (auxP) aux_i[10] = 1;
                    p.setTabaquismo(auxP = ToBool(campos[28]));
                    if (auxP) aux_i[11] = 1;

                    p.setResultado(ToBool(campos[30]));

                    p.setCuidados_intensivos(ToBool(campos[34]));

                    pacientes.add(p);
                    x++;

                    if (ToBool(campos[30])) {
                        casos_positivos++;
                        posC = true;
                    }

                    if (ToBoolDate(campos[12])) {
                        fallecimientos++;
                        //Log.d("F", "fallecimiento" + fallecimientos);
                        posF = true;
                    }

                    ex = CheckEstado(EntidadNombre(Integer.parseInt(campos[4])));
                    //existente, actualizar
                    if (ex != -1) {
                        estados.get(ex).setCasos(estados.get(ex).getCasos() + 1);
                        if (posC) {
                            estados.get(ex).setCasos_positivos(estados.get(ex).getCasos_positivos() + 1);
                        }
                        if (posF) {
                            estados.get(ex).setFallecimientos(estados.get(ex).getFallecimientos() + 1);
                        }
                    }
                    //inexistente, agregar
                    else {
                        e.setCasos(1);
                        if (posC)
                            e.setCasos_positivos(1);
                        if (posF)
                            e.setFallecimientos(1);

                        estados.add(e);
                    }

                    //Edad
                    edadProm.add(p.getEdad());

                    //Padecimientos
                    for (int x = 0; x < 12; x++) {
                        if (posC) {
                            padecimientos.get(x).setCasos_t(padecimientos.get(x).getCasos_t() + aux_i[x]);
                            padecimientos.get(x).setCasos_p(padecimientos.get(x).getCasos_p() + aux_i[x]);
                        } else {
                            padecimientos.get(x).setCasos_t(padecimientos.get(x).getCasos_t() + aux_i[x]);
                        }
                    }
                }
            } catch (IOException e) {
                Log.wtf("MyActivity", "Error al leer el archivo en la linea " + line, e);
                e.printStackTrace();
            }
            Log.d("COUNT", "########### " + Integer.toString(x) + " ###########");
            Log.d("DONE", "DONE");

            DatosPorEstado();
        }

        private void Padecimientos() {
            String[] padecimientos_s = {"intubado", "embarazo", "neumonia", "diabetes", "epoc", "asma", "inmunosupresion", "hipertension", "cardiovascular", "obesidad", "renal", "tabaquismo"};
            for (int i = 0; i < 12; i++) {
                Padecimiento ex = new Padecimiento();

                ex.setNombre(padecimientos_s[i]);
                ex.setCasos_p(0);
                ex.setCasos_t(0);

                padecimientos.add(ex);
            }
        }

        private void DatosPorEstado() {
            Log.d("control", Integer.toString(pacientes.size()));
            boolean posC;
            for (int i = 0; i < pacientes.size(); i++) {
                //Log.d("control", Integer.toString(i));
                //Genero
                posC = pacientes.get(i).isResultado();
                int ef;
                ef = CheckEstado(pacientes.get(i).getEntidad());

                if (posC && pacientes.get(i).getSexo() == 'M') {
                    estados.get(ef).setHombres(estados.get(ef).getHombres() + 1);
                } else if (posC && pacientes.get(i).getSexo() == 'F') {
                    estados.get(ef).setMujeres(estados.get(ef).getMujeres() + 1);
                }

                //Padecimientos
                if (pacientes.get(i).isEmbarazo())
                    estados.get(ef).setEmbarazo(estados.get(ef).getEmbarazo() + 1);
                if (pacientes.get(i).isIntubado())
                    estados.get(ef).setIntubado(estados.get(ef).getIntubado() + 1);
                if (pacientes.get(i).isNeumonia())
                    estados.get(ef).setNeumonia(estados.get(ef).getNeumonia() + 1);
                if (pacientes.get(i).isDiabetes())
                    estados.get(ef).setDiabetes(estados.get(ef).getDiabetes() + 1);
                if (pacientes.get(i).isEpoc())
                    estados.get(ef).setEpoc(estados.get(ef).getEpoc() + 1);
                if (pacientes.get(i).isAsma())
                    estados.get(ef).setAsma(estados.get(ef).getAsma() + 1);
                if (pacientes.get(i).isInmunosupresion())
                    estados.get(ef).setInmunosupresion(estados.get(ef).getInmunosupresion() + 1);
                if (pacientes.get(i).isHipertension())
                    estados.get(ef).setHipertension(estados.get(ef).getHipertension() + 1);
                if (pacientes.get(i).isCardiovascular())
                    estados.get(ef).setCardiovascular(estados.get(ef).getCardiovascular() + 1);
                if (pacientes.get(i).isObesidad())
                    estados.get(ef).setObesidad(estados.get(ef).getObesidad() + 1);
                if (pacientes.get(i).isRenal_cronica())
                    estados.get(ef).setRenal_cronica(estados.get(ef).getRenal_cronica() + 1);
                if (pacientes.get(i).isTabaquismo())
                    estados.get(ef).setTabaquismo(estados.get(ef).getTabaquismo() + 1);
            }
        }

        private Boolean ToBoolDate(String campo) {
            //TODO
            if (campo.equals("0") || campo.equals("9999-99-99")) {
                //Log.d("fall", "false");
                return false;
            } else {
                //Log.d("fall", "true");
                return true;
            }
        }

        private int CheckEstado(String entidadNombre) {
            int i = 0;
            while (i < estados.size()) {
                if (entidadNombre.equals(estados.get(i).getNombre())) {
                    return i;
                }
                i++;
            }
            return -1;
        }

        private boolean ToBool(String campo) {
            int f = Integer.parseInt(campo);

            if (f == 1)
                return true;
            else
                return false;
        }

        private Character Genero(int i) {
            if (i == 1)
                return 'M';
            else
                return 'F';
        }

        private String EntidadNombre(int i) {
            String[] entidad = {"Aguascalientes", "Baja California", "Baja California Sur", "Campeche", "Coahuila", "Colima", "Chiapas", "Chihuahua", "Ciudad de Mexico", "Durango", "Nuevo Leon", "Guerrero", "Hidalgo", "Jalisco", "Estado de Mexico", "Michoacan", "Morelos", "Nayarit", "Guanajuato", "Oaxaca", "Puebla", "Queretaro", "Quintana Roo", "San Luis Potosi", "Sinaloa", "Sonora", "Tabasco", "Tamaulipas", "Tlaxcala", "Veracruz", "Yucatan", "Zacatecas"};

            return entidad[i - 1];
        }
    }

    //BUSQUEDA
    //------------------------------------------------------

    private void Busqueda() {
        //Invisible cada uno
        TextView b_casos = findViewById(R.id.busqueda_casos);
        TextView b_fallecimientos = findViewById(R.id.busqueda_fallecimientos);
        LinearLayout content_display = findViewById(R.id.contentDisplay);
        findViewById(R.id.busqueda_estado_completo).setVisibility(View.GONE);
        clearButton.setVisibility(View.GONE);
        b_casos.setVisibility(View.GONE);
        b_fallecimientos.setVisibility(View.GONE);
        content_display.setVisibility(View.GONE);
        findViewById(R.id.busqueda_padecimientos).setVisibility(View.GONE);

        clearButton.setVisibility(View.VISIBLE);

        String busqueda = busquedaBox.getText().toString();
        String[] padecimientos_s = {"intubado", "embarazo", "neumonia", "diabetes", "epoc", "asma", "inmunosupresion", "hipertension", "cardiovascular", "obesidad", "renal", "tabaquismo"};

        List<Paciente> resultados = new ArrayList<>();

        Log.d("control", "BUSQUEDA: " + busqueda);

        //Casos/CasosPositivos/Fallecimientos
        if (busqueda.equalsIgnoreCase("casos")) {
            b_casos.setVisibility(View.VISIBLE);
            b_casos.setText("Casos: " + x + "\n\n Casos Positivos: " + casos_positivos);
            return;
        } else if (busqueda.equalsIgnoreCase("fallecimientos")) {
            b_fallecimientos.setVisibility(View.VISIBLE);
            b_fallecimientos.setText("Fallecimientos: " + fallecimientos);
            return;
        }

        //Estados
        for (int i = 0; i < estados.size(); i++) {
            //if(busqueda.equalsIgnoreCase(estados.get(i).getNombre())) {
            if (estados.get(i).getNombre().equalsIgnoreCase(busqueda) || estados.get(i).getNombre().toLowerCase().contains(busqueda)) {
                Log.d("busqueda", estados.get(i).getNombre() + " || " + busqueda);

                //Tabla de estado
                TextView nombre, a, b, c, d, e, f, g, h, j, k, l, m, n, o, p;
                a = findViewById(R.id.busqueda_est_casos);
                b = findViewById(R.id.busqueda_est_fallecimietos);
                c = findViewById(R.id.busqueda_est_hombres);
                d = findViewById(R.id.busqueda_est_mujeres);
                e = findViewById(R.id.busqueda_est_embarazos);
                f = findViewById(R.id.busqueda_est_intubados);
                g = findViewById(R.id.busqueda_est_neumonia);
                h = findViewById(R.id.busqueda_est_epoc);
                j = findViewById(R.id.busqueda_est_asma);
                k = findViewById(R.id.busqueda_est_inmunosupresion);
                l = findViewById(R.id.busqueda_est_hipertension);
                m = findViewById(R.id.busqueda_est_cardiovascular);
                n = findViewById(R.id.busqueda_est_obesidad);
                o = findViewById(R.id.busqueda_est_renal);
                p = findViewById(R.id.busqueda_est_tabaquismo);
                nombre = findViewById(R.id.busqueda_est_nombre);

                nombre.setText(estados.get(i).getNombre());
                a.setText(Integer.toString(estados.get(i).getCasos_positivos()));
                b.setText(Integer.toString(estados.get(i).getFallecimientos()));
                //Hombres | Mujeres
                c.setText(Integer.toString(estados.get(i).getHombres()));
                d.setText(Integer.toString(estados.get(i).getMujeres()));
                e.setText(Integer.toString(estados.get(i).getEmbarazo()));
                f.setText(Integer.toString(estados.get(i).getIntubado()));
                g.setText(Integer.toString(estados.get(i).getNeumonia()));
                h.setText(Integer.toString(estados.get(i).getEpoc()));
                j.setText(Integer.toString(estados.get(i).getAsma()));
                k.setText(Integer.toString(estados.get(i).getInmunosupresion()));
                l.setText(Integer.toString(estados.get(i).getHipertension()));
                m.setText(Integer.toString(estados.get(i).getCardiovascular()));
                n.setText(Integer.toString(estados.get(i).getObesidad()));
                o.setText(Integer.toString(estados.get(i).getRenal_cronica()));
                p.setText(Integer.toString(estados.get(i).getTabaquismo()));

                findViewById(R.id.busqueda_estado_completo).setVisibility(View.VISIBLE);

                return;
            }
        }

        int aux = 0;
        //Padecimientos
        /*
        for(int i=0; i<padecimientos.length; i++)   {
            if(busqueda.equalsIgnoreCase(padecimientos[i]) || busqueda.contains(padecimientos[i]))    {
                //Resultados de padecimiento
                return;
            }
        }*/

        //Todos los padecimientos
        if (busqueda.equalsIgnoreCase("padecimientos") || "padecimientos".toLowerCase().contains(busqueda)) {
            findViewById(R.id.busqueda_padecimientos).setVisibility(View.VISIBLE);
            TextView a, b, c, d, e, f, g, h, j, k, l;

            a = findViewById(R.id.busqueda_p_embarazos);
            b = findViewById(R.id.busqueda_p_intubados);
            c = findViewById(R.id.busqueda_p_neumonia);
            d = findViewById(R.id.busqueda_p_diabetes);
            e = findViewById(R.id.busqueda_p_epoc);
            f = findViewById(R.id.busqueda_p_asma);
            g = findViewById(R.id.busqueda_p_inmunosupresion);
            h = findViewById(R.id.busqueda_p_cardiovascular);
            j = findViewById(R.id.busqueda_p_obesidad);
            k = findViewById(R.id.busqueda_p_renal);
            l = findViewById(R.id.busqueda_p_tabaquismo);

            //String[] padecimientos = {"intubado", "embarazo", "neumonia", "diabetes", "epoc", "asma", "inmunosupresion", "hipertension", "cardiovascular", "obesidad", "renal", "tabaquismo"};
            a.setText(Integer.toString(padecimientos.get(1).getCasos_p()));
            b.setText(Integer.toString(padecimientos.get(0).getCasos_p()));
            c.setText(Integer.toString(padecimientos.get(2).getCasos_p()));
            d.setText(Integer.toString(padecimientos.get(3).getCasos_p()));
            e.setText(Integer.toString(padecimientos.get(4).getCasos_p()));
            f.setText(Integer.toString(padecimientos.get(5).getCasos_p()));
            g.setText(Integer.toString(padecimientos.get(6).getCasos_p()));
            h.setText(Integer.toString(padecimientos.get(7).getCasos_p()));
            j.setText(Integer.toString(padecimientos.get(8).getCasos_p()));
            k.setText(Integer.toString(padecimientos.get(9).getCasos_p()));
            l.setText(Integer.toString(padecimientos.get(10).getCasos_p()));

            return;
        }

        //ID

    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}