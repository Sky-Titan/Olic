package com.example.vacancyclassroom;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;

import android.app.ProgressDialog;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendToMySQL extends AppCompatActivity {

    SQLiteDatabase db;
    public SendToMySQL() {
    }

    public void sending(){

        InsertData task = new InsertData();

        db = openOrCreateDatabase(
                "lecture_list.db",
                SQLiteDatabase.CREATE_IF_NECESSARY,
                null);

        Cursor cursor=db.rawQuery("SELECT * FROM lecture",null);
        int i=0;
        while(cursor.moveToNext())
        {
            i++;
            String code=cursor.getString(0);
            String title=cursor.getString(1);
            String classroom=cursor.getString(2);
            String time=cursor.getString(3);
            System.out.println(i+"번째 sql 전송");
            task.execute("http://121.182.35.52/vacancyclassroom/insert_lecture.php",code,title,classroom,time);
        }
        if(db!=null){
            db.close();
        }
    }
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

        }


        @Override
        protected String doInBackground(String... params) {

            String code = (String)params[1];
            String title = (String)params[2];
            String classroom = (String)params[3];
            String time = (String)params[4];

            String serverURL = (String)params[0];

            String postParameters = "code="+code+"&title=" + title + "&classroom="+classroom + "&time="+time;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("euckr"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                return new String("Error: " + e.getMessage());
            }

        }
    }
}
