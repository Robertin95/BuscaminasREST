package com.androidya.buscaminas;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Resultado extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);
        cargar(this);
    }
    public void imprimir(String[] arreglo){
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arreglo);
        ListView lista = (ListView) findViewById(R.id.lista);
        lista.setAdapter(adapter);
    }
    public void cargar(final Resultado a){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet del = new HttpGet("http://192.168.43.136:3500/api/jugadores/");
                del.setHeader("content-type", "application/json");
                List entityResult;
                try
                {
                    HttpResponse resp = httpClient.execute(del);
                    StatusLine statusLine = resp.getStatusLine();

                    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                        InputStream in = resp.getEntity().getContent();
                        BufferedReader buffered = new BufferedReader(new InputStreamReader(in));
                        StringBuilder fullLines = new StringBuilder();

                        String line;

                        while ((line = buffered.readLine()) != null){
                            fullLines.append(line);
                        }

                        String result = fullLines.toString();

                        JSONArray objetos = new JSONArray(result);
                        final String[] arreglo = new String[objetos.length()];

                        for(int i=0; i<objetos.length();i++){
                            JSONObject objeto = objetos.getJSONObject(i);
                            arreglo[i] = (i+1)+". "+objeto.getString("nombre")+"  "+objeto.getInt("tiempo")+" milis";
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                a.imprimir(arreglo);
                            }
                        });

                    }
                }
                catch(Exception ex)
                {
                    Log.e("ServicioRest","Error!", ex);
                }
            }
        });
        /*DBranking db = new DBranking(getApplicationContext());
        SQLiteDatabase o = db.getReadableDatabase();
        if(o != null){
            Cursor c = o.rawQuery("SELECT * FROM puntuaciones ORDER BY tiempo LIMIT 10", null);
            int cant = c.getCount();
            int i = 0;
            String[] arreglo = new String[cant];
            if(c.moveToFirst()){
                do {
                    float res = c.getFloat(2)/1000;
                    String linea = (i+1)+". "+c.getString(1)+" "+res+" seg.";
                    arreglo[i] = linea;
                    i++;
                }while(c.moveToNext());
            }
            ArrayAdapter<String>adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arreglo);
            ListView lista = (ListView) findViewById(R.id.lista);
            lista.setAdapter(adapter);
        }*/
    }
}
