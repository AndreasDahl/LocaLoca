package com.example.group3.localoca;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Asbjørn on 03-11-2014.
 */
public class CalenderActivity extends Activity {

    ListView lvDay;
    TextView tvBookingTitle, tvBooking;
    SharedPreferences userinfo;
    String usernr;
    String[][] matrix;
    HttpPost httppost;
    StringBuffer buffer;
    String response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    List<String> lvlist = new ArrayList<String>();
    ProgressDialog dialog = null;
    static String[] separated;
    ArrayList<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        userinfo = getSharedPreferences("userinfo", MODE_PRIVATE);
        usernr = userinfo.getString("userNumber", "");
        lvDay = (ListView)findViewById(R.id.lvDay);
        tvBookingTitle = (TextView)findViewById(R.id.tvBookingTitle);
        tvBooking = (TextView)findViewById(R.id.tvBooking);
        tvBookingTitle.setVisibility(View.INVISIBLE);
        tvBooking.setVisibility(View.INVISIBLE);
        lvClick();
        dialog = ProgressDialog.show(this, "One moment please", "Fetching your calender");
        new Thread(new Runnable() {
            public void run() {
                getUserBookings();

            }
        }).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lvRoomsPopulate();
                dialog.dismiss();
            }
        }, 1500);
    }

    private String getDateString(int days) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, days);
        return dateFormat.format(cal.getTime());
    }

    private String getDayString(int days) {
        DateFormat dateFormat = new SimpleDateFormat("EEEE");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_WEEK, days);
        return dateFormat.format(cal.getTime());
    }

    public void getUserBookings(){
        try{

            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://pomsen.com/phpscripts/getuserbookingsPOST.php");
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("usernr", usernr));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httppost, responseHandler);

            Log.d("drixi", response);
            separated = response.split("@");
            matrix = new String[response.length()][];
            for(int i=0; i<separated.length; i++){
                matrix[i] = separated[i].split("#");
            }
        }catch(IOException e){
            Log.e("drixi", "FEJLET");

        }

    }

    private void arrayadapter() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1, lvlist);
        lvDay.setAdapter(arrayAdapter);
    }

    private void lvRoomsPopulate(){
        for(int i = 0; i<7; i++){
            lvlist.add(getDayString(i) + " " + getDateString(i));
            if(separated.length > 0) {
                for (int n = 1; separated.length > n; n++) {
                    if (getDateString(i).equals(matrix[n][8])) {
                        lvlist.add(matrix[n][6] + " " + matrix[n][1] + " \n" + matrix[n][7] + " " + matrix[n][2]);
                        list.add(matrix[n][1]);
                    }
                }
            }
        }
        arrayadapter();
    }

    public void lvClick(){
        lvDay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object o = lvDay.getItemAtPosition(position);
                String[] strO = String.valueOf(o).split(" ");
                System.out.println(strO[1]);
                for(int i = 1 ; separated.length > i ; i++){
                    if(list.contains(matrix[i][1])){
                        Toast.makeText(CalenderActivity.this, "Changing screen", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

}
