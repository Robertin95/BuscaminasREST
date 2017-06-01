package com.androidya.buscaminas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.server.converter.StringToIntConverter;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends Activity implements OnTouchListener {

	DBranking db;
	String timeout;
	private Tablero fondo;
	int x, y;
	private Casilla[][] casillas;
	private boolean activo = true;
	public Chronometer cronometro;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LinearLayout layout = (LinearLayout) findViewById(R.id.layout2);
		fondo = new Tablero(this);
		fondo.setOnTouchListener(this);
		layout.addView(fondo);
		casillas = new Casilla[8][8];
		for (int f = 0; f < 8; f++) {
			for (int c = 0; c < 8; c++) {
				casillas[f][c] = new Casilla();
			}
		}

		cronometro = (Chronometer) findViewById(R.id.chronometer);

		this.disponerBombas();
		this.contarBombasPerimetro();
		cronometro.start();
		db = new DBranking(getApplicationContext());

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}


	public void presionar(View v) {


		casillas = new Casilla[8][8];
		for (int f = 0; f < 8; f++) {
			for (int c = 0; c < 8; c++) {
				casillas[f][c] = new Casilla();
			}
		}
		cronometro.setBase(SystemClock.elapsedRealtime());

		cronometro.start();
		this.disponerBombas();
		this.contarBombasPerimetro();
		activo = true;

		fondo.invalidate();
	}

	public void mostrarDialog() {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
		View myView = getLayoutInflater().inflate(R.layout.dialog_ganador, null);
		final EditText mNombre = (EditText) myView.findViewById(R.id.id_nombre);
		Button mGuardar = (Button) myView.findViewById(R.id.id_guardar);

		mBuilder.setView(myView);
		final AlertDialog dialog = mBuilder.create();
		mGuardar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mNombre.getText().toString().isEmpty()) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                HttpClient httpClient = new DefaultHttpClient();

                                HttpPost post = new HttpPost("http://192.168.43.136:3500/api/jugadores/");

                                post.setHeader("content-type", "application/json");

                                JSONObject dato = new JSONObject();

                                dato.put("nombre", mNombre.getText().toString());
                                dato.put("tiempo", timeout);
                                StringEntity entity = new StringEntity(dato.toString());
                                post.setEntity(entity);
                                httpClient.execute(post);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (ClientProtocolException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    //db.agregar(mNombre.getText().toString(), timeout);
					//timeout = ""+0;
					dialog.dismiss();
					Intent i = new Intent(getApplicationContext(), Resultado.class);
					startActivity(i);
				} else {
					Toast.makeText(MainActivity.this, "Llene el campo, por favor", Toast.LENGTH_SHORT).show();
				}
			}

		});



		dialog.show();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (activo)
			for (int f = 0; f < 8; f++) {
				for (int c = 0; c < 8; c++) {
					if (casillas[f][c].dentro((int) event.getX(),
							(int) event.getY())) {
						casillas[f][c].destapado = true;
						if (casillas[f][c].contenido == 80) {

							long elapsedMillis = SystemClock.elapsedRealtime() - cronometro.getBase();
							timeout = String.valueOf(elapsedMillis);

							cronometro.stop();
							Toast.makeText(this, "Booooooooommmmmmmmmmmm",
									Toast.LENGTH_LONG).show();
							activo = false;

							mostrarDialog();

						} else if (casillas[f][c].contenido == 0)
							recorrer(f, c);
						fondo.invalidate();
					}
				}
			}
		if (gano() && activo) {

			cronometro.stop();
			Toast.makeText(this, "Ganaste", Toast.LENGTH_LONG).show();
			activo = false;

		}

		return true;
	}

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	public Action getIndexApiAction() {
		Thing object = new Thing.Builder()
				.setName("Main Page") // TODO: Define a title for the content shown.
				// TODO: Make sure this auto-generated URL is correct.
				.setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
				.build();
		return new Action.Builder(Action.TYPE_VIEW)
				.setObject(object)
				.setActionStatus(Action.STATUS_TYPE_COMPLETED)
				.build();
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		AppIndex.AppIndexApi.start(client, getIndexApiAction());
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		AppIndex.AppIndexApi.end(client, getIndexApiAction());
		client.disconnect();
	}


	class Tablero extends View {

		public Tablero(Context context) {
			super(context);
		}

		protected void onDraw(Canvas canvas) {
			canvas.drawRGB(0, 0, 0);
			int ancho = 0;
			if (canvas.getWidth() < canvas.getHeight())
				ancho = fondo.getWidth();
			else
				ancho = fondo.getHeight();
			int anchocua = ancho / 8;
			Paint paint = new Paint();
			paint.setTextSize(20);
			Paint paint2 = new Paint();
			paint2.setTextSize(20);
			paint2.setTypeface(Typeface.DEFAULT_BOLD);
			paint2.setARGB(255, 0, 0, 255);
			Paint paintlinea1 = new Paint();
			paintlinea1.setARGB(255, 255, 255, 255);
			int filaact = 0;
			for (int f = 0; f < 8; f++) {
				for (int c = 0; c < 8; c++) {
					casillas[f][c].fijarxy(c * anchocua, filaact, anchocua);
					if (casillas[f][c].destapado == false)
						paint.setARGB(153, 204, 204, 204);
					else
						paint.setARGB(255, 153, 153, 153);
					canvas.drawRect(c * anchocua, filaact, c * anchocua
							+ anchocua - 2, filaact + anchocua - 2, paint);
					// linea blanca
					canvas.drawLine(c * anchocua, filaact, c * anchocua
							+ anchocua, filaact, paintlinea1);
					canvas.drawLine(c * anchocua + anchocua - 1, filaact, c
									* anchocua + anchocua - 1, filaact + anchocua,
							paintlinea1);

					if (casillas[f][c].contenido >= 1
							&& casillas[f][c].contenido <= 8
							&& casillas[f][c].destapado)
						canvas.drawText(
								String.valueOf(casillas[f][c].contenido), c
										* anchocua + (anchocua / 2) - 8,
								filaact + anchocua / 2, paint2);

					if (casillas[f][c].contenido == 80
							&& casillas[f][c].destapado) {
						Paint bomba = new Paint();
						bomba.setARGB(255, 255, 0, 0);
						canvas.drawCircle(c * anchocua + (anchocua / 2),
								filaact + (anchocua / 2), 8, bomba);
					}

				}
				filaact = filaact + anchocua;
			}
		}
	}


	private void disponerBombas() {
		int cantidad = 8;
		do {
			int fila = (int) (Math.random() * 8);
			int columna = (int) (Math.random() * 8);
			if (casillas[fila][columna].contenido == 0) {
				casillas[fila][columna].contenido = 80;
				cantidad--;
			}
		} while (cantidad != 0);
	}

	private boolean gano() {
		int cant = 0;
		for (int f = 0; f < 8; f++)
			for (int c = 0; c < 8; c++)
				if (casillas[f][c].destapado)
					cant++;
		if (cant == 56)
			return true;
		else
			return false;
	}

	private void contarBombasPerimetro() {
		for (int f = 0; f < 8; f++) {
			for (int c = 0; c < 8; c++) {
				if (casillas[f][c].contenido == 0) {
					int cant = contarCoordenada(f, c);
					casillas[f][c].contenido = cant;
				}
			}
		}
	}

	int contarCoordenada(int fila, int columna) {
		int total = 0;
		if (fila - 1 >= 0 && columna - 1 >= 0) {
			if (casillas[fila - 1][columna - 1].contenido == 80)
				total++;
		}
		if (fila - 1 >= 0) {
			if (casillas[fila - 1][columna].contenido == 80)
				total++;
		}
		if (fila - 1 >= 0 && columna + 1 < 8) {
			if (casillas[fila - 1][columna + 1].contenido == 80)
				total++;
		}

		if (columna + 1 < 8) {
			if (casillas[fila][columna + 1].contenido == 80)
				total++;
		}
		if (fila + 1 < 8 && columna + 1 < 8) {
			if (casillas[fila + 1][columna + 1].contenido == 80)
				total++;
		}

		if (fila + 1 < 8) {
			if (casillas[fila + 1][columna].contenido == 80)
				total++;
		}
		if (fila + 1 < 8 && columna - 1 >= 0) {
			if (casillas[fila + 1][columna - 1].contenido == 80)
				total++;
		}
		if (columna - 1 >= 0) {
			if (casillas[fila][columna - 1].contenido == 80)
				total++;
		}
		return total;
	}

	private void recorrer(int fil, int col) {
		if (fil >= 0 && fil < 8 && col >= 0 && col < 8) {
			if (casillas[fil][col].contenido == 0) {
				casillas[fil][col].destapado = true;
				casillas[fil][col].contenido = 50;
				recorrer(fil, col + 1);
				recorrer(fil, col - 1);
				recorrer(fil + 1, col);
				recorrer(fil - 1, col);
				recorrer(fil - 1, col - 1);
				recorrer(fil - 1, col + 1);
				recorrer(fil + 1, col + 1);
				recorrer(fil + 1, col - 1);
			} else if (casillas[fil][col].contenido >= 1
					&& casillas[fil][col].contenido <= 8) {
				casillas[fil][col].destapado = true;
			}
		}
	}


}
