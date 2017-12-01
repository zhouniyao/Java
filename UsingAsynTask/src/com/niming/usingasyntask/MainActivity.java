package com.niming.usingasyntask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		text = (TextView) findViewById(R.id.textView1);

		findViewById(R.id.read).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ReadURL("www.baidu.com");
			}
		});
	}

	public void ReadURL(String url) {
		new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				try {
					URL url = new URL("");
					URLConnection connection = url.openConnection();
					InputStream iStream = connection.getInputStream(); 
					InputStreamReader isr = new InputStreamReader(iStream);
					BufferedReader br = new BufferedReader(isr);
					String line;
					StringBuilder builder = new StringBuilder();
					while((line=br.readLine()) != null){
						builder.append(line);
					}
					br.close();
					iStream.close();
					return builder.toString();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "¿ªÊ¼¶ÁÈ¡", Toast.LENGTH_SHORT).show();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String result) {
				text.setText(result);
				super.onPostExecute(result);
			}

			@Override
			protected void onProgressUpdate(Void... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
			}

			@Override
			protected void onCancelled(String result) {
				// TODO Auto-generated method stub
				super.onCancelled(result);
			}

			@Override
			protected void onCancelled() {
				// TODO Auto-generated method stub
				super.onCancelled();
			}
			
		}.execute(url);
	}

}
