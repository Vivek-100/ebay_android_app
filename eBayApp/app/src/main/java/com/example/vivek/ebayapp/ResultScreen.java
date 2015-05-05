package com.example.vivek.ebayapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.String;

/**
 * Created by Vivek on 4/16/2015.
 */
public class ResultScreen extends ActionBarActivity {
    JSONObject json_Array = null;
    JSONObject item = null;
    JSONObject basicInfo = null;
    String keyword="";
    TextView heading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_result);

        Bundle b = getIntent().getExtras();
        String response=b.getString("Array");
        keyword=b.getString("keyword");
        heading=(TextView)findViewById(R.id.lbl_heading);
        heading.setText("Results for '"+keyword+"'");

        try{
            json_Array = new JSONObject(response);
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
        }

        CreateListView();

    }
    private void CreateListView()
    {
        String[] listImageUri = new String[5];
        String[] listTitle = new String[5];
        String[] listPrice = new String[5];
        String[] listItemUrl = new String[5];
        for (int i = 0; i < 5; ++i) {
            String itemNo="item"+i;
            try {
                item = json_Array.getJSONObject(itemNo);
                basicInfo = item.getJSONObject("basicInfo");
            }
            catch(JSONException e){
                //error
            }
            String title=basicInfo.optString("title");
            String galleryURL=basicInfo.optString("galleryURL");
            String viewItemURL=basicInfo.optString("viewItemURL");
            String pictureURLSuperSize=basicInfo.optString("pictureURLSuperSize");
            String convertedCurrentPrice=basicInfo.optString("convertedCurrentPrice");
            String shippingServiceCost=basicInfo.optString("shippingServiceCost");
            String imageURL="";
            String price="";
            if(galleryURL.equalsIgnoreCase("")) {
                imageURL=pictureURLSuperSize;
            }
            else{
                imageURL=galleryURL;
            }

            if(shippingServiceCost.equalsIgnoreCase("N/A")){
                price = "Price: $" + convertedCurrentPrice + " (FREE Shipping)";
            }
            else {
                if (Float.parseFloat(shippingServiceCost.toString()) > 0) {
                    price = "Price: $" + convertedCurrentPrice + " (+ $" + shippingServiceCost + " Shipping)";
                } else {
                    price = "Price: $" + convertedCurrentPrice + " (FREE Shipping)";
                }
            }
            listImageUri[i]=(imageURL);
            listTitle[i]=(title);
            listPrice[i]=(price);
            listItemUrl[i]=viewItemURL;
        }
        ResultAdapter adapter=new ResultAdapter(this, listTitle, listImageUri,listPrice,listItemUrl,json_Array);
        final ListView listview = (ListView) findViewById(R.id.lst_result);
        listview.setAdapter(adapter);
    }
}
