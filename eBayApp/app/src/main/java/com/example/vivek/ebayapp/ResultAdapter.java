package com.example.vivek.ebayapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResultAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final String[] imgid;
    private final String[] price;
    private final String[] itemURL;
    private final JSONObject json_Array;
    private InputStream is;
    private Bitmap bitmap;

    public ResultAdapter(Activity context, String[] itemname, String[] imgid,String[] price,String[] itemURL, JSONObject json_Array) {
        super(context, R.layout.target_result, itemname);
        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
        this.price=price;
        this.itemURL=itemURL;
        this.json_Array=json_Array;
    }

    private final class ViewHolder {
        TextView txtTitle,txtprice;
        ImageView imageView;
    }

    public View getView(final int position,View view,ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final ViewHolder holder;
        holder = new ViewHolder();
        View rowView = inflater.inflate(R.layout.target_result, null, true);
        try {
            holder.txtTitle = (TextView) rowView.findViewById(R.id.result_title);
            holder.imageView = (ImageView) rowView.findViewById(R.id.result_icon);
            holder.txtprice = (TextView) rowView.findViewById(R.id.result_price);

            new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    URL url;
                    try {
                        url = new URL(params[0]);
                        InputStream in = (url).openStream();
                        bitmap = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return bitmap;
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    super.onPostExecute(result);
                    if (result != null) {
                        holder.imageView.setImageBitmap(result);
                    }
                }

            }.execute(imgid[position]);

            holder.txtTitle.setText(itemname[position]);
            holder.txtprice.setText(price[position]);

        }
        catch(Exception e){
            String s=e.toString();
        }

        holder.imageView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(itemURL[position]));
                context.startActivity(intent);
            }
        });

        holder.txtTitle.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent display_detail =
                        new Intent(context, DetailScreen.class);
                Bundle b = new Bundle();
                b.putString("Array",json_Array.toString());
                b.putString("position", String.valueOf(position));
                display_detail.putExtras(b);
                context.startActivity(display_detail);
            }
        });

        return rowView;

    };


}