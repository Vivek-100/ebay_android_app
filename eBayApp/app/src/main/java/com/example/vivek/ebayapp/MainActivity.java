package com.example.vivek.ebayapp;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Color;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import java.io.InputStream;
import java.io.BufferedInputStream;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;


public class MainActivity extends ActionBarActivity {
    Button search;
    Button clear;
    EditText keyword;
    EditText from_value;
    EditText to_value;
    TextView error;
    Spinner sort_by;
    String string_url="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keyword=(EditText)findViewById(R.id.txt_keyword);
        from_value=(EditText)findViewById(R.id.txt_price_from);
        to_value=(EditText)findViewById(R.id.txt_price_to);
        error=(TextView)findViewById(R.id.lbl_error);
        sort_by=(Spinner)findViewById(R.id.ddl_sort_by);
        addListenerOnButton();
    }


    class MyTask extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            //display progress dialog.
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            //initialize
            JSONObject jArray = null;
            String response ="";
            try{
                URL url = new URL(string_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                InputStream in = new BufferedInputStream(conn.getInputStream());
                response = IOUtils.toString(in, "UTF-8");

                //try parse the string to a JSON object
                try{
                    jArray = new JSONObject(response);
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
            }
            return jArray;
        }


        @Override
        protected void onPostExecute(JSONObject jArray) {

            String checkResult=jArray.optString("ack");
            if((checkResult.equalsIgnoreCase("Success"))){
                error.setVisibility(View.INVISIBLE);
                Intent display_result =
                        new Intent(MainActivity.this, ResultScreen.class);
                Bundle b = new Bundle();
                b.putString("Array",jArray.toString());
                b.putString("keyword", keyword.getText().toString());
                display_result.putExtras(b);
                startActivity(display_result);
            }
            else{
                error.setVisibility(View.VISIBLE);
                error.setText("No Result found");
                error.setTextColor(Color.parseColor("#FF662EA2"));
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addListenerOnButton() {

        search = (Button) findViewById(R.id.btn_search);
        search.setOnClickListener(startListener);

        clear = (Button)findViewById(R.id.btn_clear);
        clear.setOnClickListener(stopListener);

    }

    //Create an anonymous implementation of OnClickListener
    private OnClickListener stopListener = new OnClickListener() {
        public void onClick(View v) {
            error.setVisibility(View.INVISIBLE);
            keyword.setText("");
            from_value.setText("");
            to_value.setText("");
            sort_by.setSelection(0);
        }
    };

    // Create an anonymous implementation of OnClickListener
    protected OnClickListener startListener = new OnClickListener() {
        public void onClick(View v) {
            if (validationSuccess()) {
                //error.setVisibility(View.INVISIBLE);
                String sort_by_value = sort_by.getSelectedItem().toString();
                String final_sort_by_value="";
                if(sort_by_value.toString().equalsIgnoreCase("Best Match")){
                    final_sort_by_value="BestMatch";
                }
                if(sort_by_value.toString().equalsIgnoreCase("Price: highest first")){
                    final_sort_by_value="CurrentPriceHighest";
                }
                if(sort_by_value.toString().equalsIgnoreCase("Price + Shipping: highest first")){
                    final_sort_by_value="PricePlusShippingHighest";
                }
                if(sort_by_value.toString().equalsIgnoreCase("Price + Shipping: lowest first")){
                    final_sort_by_value="PricePlusShippingLowest";
                }
                String keywords=keyword.getText().toString();
                String from=from_value.getText().toString();
                String tofield=to_value.getText().toString();

                try {
                    String urlencode_keywords = URLEncoder.encode(keywords, "utf-8");
                    string_url = "http://vivekusc-env.elasticbeanstalk.com/?keywords=" + urlencode_keywords + "&resultperpage=5&sortby=" + final_sort_by_value + "&pagenumber=1&from=" + from + "&tofield=" + tofield+"";
                    //Toast.makeText(getApplicationContext(),string_url,Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    //Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                }
                //call to AWS to get json
                new MyTask().execute();
            }
        }

        private Boolean validationSuccess(){
            if(keyword.getText().toString().equalsIgnoreCase("")){
                error.setVisibility(View.VISIBLE);
                error.setText("Please enter a keyword");
                error.setTextColor(Color.parseColor("#ffa20811"));
                //Toast.makeText(getApplicationContext(),"Please enter a keyword",Toast.LENGTH_LONG).show();
                 return false;
            }
            if( from_value.getText().toString() != null && !from_value.getText().toString().isEmpty()){
                Boolean IsInteger = isInteger(from_value.getText().toString());
                if(!IsInteger) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Price should be a valid number");
                    error.setTextColor(Color.parseColor("#ffa20811"));
                    return false;
                }
                if(Integer.parseInt(from_value.getText().toString()) < 0){
                    error.setVisibility(View.VISIBLE);
                    error.setText("Minimum price cannot be below 0");
                    error.setTextColor(Color.parseColor("#ffa20811"));
                    return false;
                }
            }
            if( to_value.getText().toString() != null && !to_value.getText().toString().isEmpty()){
                Boolean IsInteger = isInteger(to_value.getText().toString());
                if(!IsInteger) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Price should be a valid number");
                    error.setTextColor(Color.parseColor("#ffa20811"));
                    return false;
                }
                if(Integer.parseInt(to_value.getText().toString()) < 0){
                    error.setVisibility(View.VISIBLE);
                    error.setText("Maximum price cannot be less than minimum price or below zero");
                    error.setTextColor(Color.parseColor("#ffa20811"));
                    return false;
                }
                if( from_value.getText().toString() != null && !from_value.getText().toString().isEmpty() && Integer.parseInt(to_value.getText().toString()) < Integer.parseInt(from_value.getText().toString())) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Maximum price cannot be less than minimum price or below zero");
                    error.setTextColor(Color.parseColor("#ffa20811"));
                    return false;
                }
            }
            return true;
        }
    };

    public boolean isInteger( String input )
    {
        try
        {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e)
        {
            return false;
        }
    }



}
