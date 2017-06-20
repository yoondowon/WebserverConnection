package com.example.user.webserverconnection;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_Get;
    Button btn_Post;
    EditText edit_ID;
    EditText edit_Name;
    EditText edit_Age;
    TextView text_ID;
    TextView text_Name;
    TextView text_Age;
    TextView text_Gender;
    CheckBox check_Man;
    CheckBox check_Woman;
    String gender = "";
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_Get = (Button) findViewById(R.id.btn_get);
        btn_Get.setOnClickListener(this);
        btn_Post = (Button) findViewById(R.id.btn_post);
        btn_Post.setOnClickListener(this);
        edit_ID = (EditText) findViewById(R.id.edit_id);
        edit_Name = (EditText) findViewById(R.id.edit_name);
        edit_Age = (EditText) findViewById(R.id.edit_age);
        text_ID = (TextView) findViewById(R.id.text_id);
        text_Name = (TextView) findViewById(R.id.text_name);
        text_Age = (TextView) findViewById(R.id.text_age);
        text_Gender= (TextView) findViewById(R.id.text_gender);
        check_Man = (CheckBox) findViewById(R.id.check_man);
        check_Man.setOnClickListener(this);
        check_Woman = (CheckBox) findViewById(R.id.check_woman);
        check_Woman.setOnClickListener(this);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.db_list_view);
        listView.setAdapter(arrayAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get:
                new getData().execute();
                break;

            case R.id.btn_post:
                String id = edit_ID.getText().toString();
                String name = edit_Name.getText().toString();
                String age = edit_Age.getText().toString();
                String[] sendData = {id, name, age, gender};
                new postData().execute(sendData);
                break;

            case R.id.check_man:
                check_Woman.setChecked(false);
                gender = "Man";
                break;

            case R.id.check_woman:
                check_Man.setChecked(false);
                gender = "Woman";
                break;
        }
    }

    class getData extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... Params) {
            try{
                URL server = new URL("http://ip.address/gettest.php");
                HttpURLConnection urlConnection = (HttpURLConnection) server.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                br.close();
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result){
            if(result.startsWith("Exception")){
                Log.e("getPeriod onPost: ", "Error: " + result);
            }else{
                Log.d("getPeriod onPost: ", "Get Result: " + result);
                ArrayList<String> list = new ArrayList<String>();
                String[] list_DB = result.split("<br>");
                for (int i=0; i<list_DB.length; i++){
                    String[] tempResult = list_DB[i].split(",");
                    String listResult = "ID: " + tempResult[0] + "\nNAME: " + tempResult[1] + "\nAGE: " + tempResult[2] + "\nGENDER: " + tempResult[3];
                    list.add(listResult);
                }
                arrayAdapter.clear();
                arrayAdapter.addAll(list);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }


    class postData extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... Params) {
            StringBuffer sb = new StringBuffer();
            sb.append("id").append("=").append(Params[0]).append("&");
            sb.append("name").append("=").append(Params[1]).append("&");
            sb.append("age").append("=").append(Params[2]).append("&");
            sb.append("gender").append("=").append(Params[3]);
            Log.d("sendData", "String Buffer: " + sb.toString());
            try{
                URL server = new URL("http://ip.address/posttest.php");
                Log.d("sendData", "URL: " + server.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) server.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                OutputStream os = urlConnection.getOutputStream();
                os.write(sb.toString().getBytes("EUC-KR"));
                os.flush();
                os.close();
                String response = "";
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.d("sendData", "Connect to server");
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                }else{
                    Log.d("sendData", "DO NOT connect to server");
                }
                urlConnection.disconnect();
                return response;
            }catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result){
            Log.d("sendData", "Result: " + result);
        }
    }
}

