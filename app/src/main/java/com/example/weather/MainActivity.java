package com.example.weather;

import android.app.Activity;
import android.app.AsyncNotedAppOp;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static android.widget.Toast.*;


public class MainActivity extends AppCompatActivity {

    EditText city;
    TextView weatherforecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city=findViewById(R.id.city);
        weatherforecast=findViewById(R.id.weatherforecast);
    }

    public void start(View View)
    {
        Log.i("Info","Button Pressed");

        try {
            String encodedcity = URLEncoder.encode(city.getText().toString());
            LocationWeather task = new LocationWeather();
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedcity + "&appid=439d4b804bc8187953eb36d2a8c26a02");

            //hide the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e)
        {
            e.printStackTrace();
            makeText(this,"Couldn't found the weather", LENGTH_SHORT).show();
        }
    }


    class LocationWeather extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String ...urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                 url = new URL(urls[0]);
                 urlConnection = (HttpURLConnection) url.openConnection();
                 InputStream input = urlConnection.getInputStream();
                 InputStreamReader reader = new InputStreamReader(input);
                 int data = reader.read();

                 while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                 }
                 return result;
            } catch (Exception e) {
                e.printStackTrace();
               //makeText(getApplicationContext(), "Couldn't found the weather", Toast.LENGTH_SHORT).show();
                return "failed";
            }
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);

            try{
                JSONObject json=new JSONObject(s);
                String weatherinfo=json.getString("weather");
                Log.i("Weather Content",weatherinfo);

                JSONArray arr=new JSONArray(weatherinfo);
                String msg="";
                String main="";
                String description="";

                for(int i=0; i< arr.length();i++)
                {
                    JSONObject jsonpart=arr.getJSONObject(i);

                    Log.i("id",jsonpart.getString("id"));
                    Log.i("main",jsonpart.getString("main"));
                    Log.i("description",jsonpart.getString("description"));

                    main=jsonpart.getString("main");
                    description=jsonpart.getString("description");
                    msg=main +" : "+ description +"\r\n" ;
                }
                if(!msg.equals("")){

                    weatherforecast.setText(msg);
                }
            }catch (Exception e)
            {
                e.printStackTrace();

                makeText(getApplicationContext(),"Couldn't found the weather", LENGTH_SHORT).show();
            }
        }

        }
    }
