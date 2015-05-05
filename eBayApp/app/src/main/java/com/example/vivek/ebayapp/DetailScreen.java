package com.example.vivek.ebayapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by Vivek on 4/16/2015.
 */
public class DetailScreen extends ActionBarActivity {
    private CallbackManager callbackManager;
    ShareDialog shareDialog;
    JSONObject json_Array = null;
    ImageView main_Image;
    TextView title;
    TextView price;
    TextView location;
    ImageView toprated;
    ImageButton buy_now;
    ImageButton loginButton;
    Button basic_info;
    Button seller;
    Button shipping;
    RelativeLayout layout_basic_info;
    RelativeLayout layout_seller;
    RelativeLayout layout_shipping;
    TextView category_name;
    TextView condition;
    TextView buying_format;
    TextView user_name;
    TextView feedback_score;
    TextView positive_feedback;
    TextView feedback_rating;
    ImageView top_rated;
    TextView store;
    TextView shipping_type;
    TextView handling_time;
    TextView shipping_location;
    ImageView expedited_shipping;
    ImageView one_day_shipping;
    ImageView return_accepted;
    JSONObject item = null;
    JSONObject basicInfo = null;
    JSONObject json_seller = null;
    JSONObject json_shipping = null;
    String position = "";
    private HttpURLConnection connection;
    private InputStream is;
    private Bitmap bitmap;

    String titleval = "";
    String galleryURL = "";
    String viewItemURL = "";
    String pictureURLSuperSize = "";
    String convertedCurrentPrice = "";
    String shippingServiceCost = "";
    String locationVal = "";
    String topRatedListing = "";
    String imageURL = "";
    String priceVal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.screen_details);

        loginButton = (ImageButton)findViewById(R.id.login_button);

        shareDialog = new ShareDialog(this);


        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                facebookLogin();
            }

        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                String content = priceVal +",Location:"+ locationVal;
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(titleval)
                            .setContentDescription(content)
                            .setContentUrl(Uri.parse(viewItemURL))
                            .setImageUrl(Uri.parse(imageURL))
                            .build();

                    shareDialog.show(linkContent);
                }
            }

            @Override
            public void onCancel()
            {

            }

            @Override
            public void onError(FacebookException e)
            {

            }
        });

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        String postID=result.getPostId();
                        if(postID != null && !postID.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Successfully posted "+titleval+",ID:"+postID+"", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Post Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Post Cancelled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getApplicationContext(), "Error while posting", Toast.LENGTH_SHORT).show();
                    }

                });

        main_Image = (ImageView) findViewById(R.id.img_icon);
        title = (TextView) findViewById(R.id.txt_title);
        price = (TextView) findViewById(R.id.txt_price);
        location = (TextView) findViewById(R.id.txt_location);
        toprated = (ImageView) findViewById(R.id.img_top_rated);
        buy_now = (ImageButton) findViewById(R.id.img_buy_now);
        basic_info = (Button) findViewById(R.id.img_basic_info);
        seller = (Button) findViewById(R.id.img_seller);
        shipping = (Button) findViewById(R.id.img_shopping);
        basic_info.setBackgroundColor(Color.GRAY);
        seller.setBackgroundColor(Color.LTGRAY);
        shipping.setBackgroundColor(Color.LTGRAY);
        layout_basic_info =(RelativeLayout) findViewById(R.id.layout_basic_info);
        layout_seller =(RelativeLayout) findViewById(R.id.layout_seller);
        layout_shipping =(RelativeLayout) findViewById(R.id.layout_shipping);

        category_name = (TextView) findViewById(R.id.txt_Category_Name_val);
        condition = (TextView) findViewById(R.id.txt_Condition_val);
        buying_format = (TextView) findViewById(R.id.txt_Buying_Format_val);
        user_name = (TextView) findViewById(R.id.txt_user_name_val);
        feedback_score = (TextView) findViewById(R.id.txt_feedback_score_val);
        positive_feedback = (TextView) findViewById(R.id.txt_positive_feedback_val);
        feedback_rating = (TextView) findViewById(R.id.txt_feedback_rating_val);
        top_rated = (ImageView) findViewById(R.id.txt_top_rated_val);
        store = (TextView) findViewById(R.id.txt_store_val);
        shipping_type = (TextView) findViewById(R.id.txt_shipping_type_val);
        handling_time = (TextView) findViewById(R.id.txt_handling_time_val);
        shipping_location = (TextView) findViewById(R.id.txt_shipping_locations_val);
        expedited_shipping = (ImageView) findViewById(R.id.txt_expedited_shipping_val);
        one_day_shipping = (ImageView) findViewById(R.id.txt_oneday_shipping_val);
        return_accepted = (ImageView) findViewById(R.id.txt_returns_accepted_val);
        try {
            Bundle b = getIntent().getExtras();
            String response = b.getString("Array");
            position = b.getString("position");

            try {
                json_Array = new JSONObject(response);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        CreateStaticView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode, data);
    }

    private void CreateStaticView() {
        String itemNo = "item" + position;
        try {
            item = json_Array.getJSONObject(itemNo);
            basicInfo = item.getJSONObject("basicInfo");
            json_seller = item.getJSONObject("sellerInfo");
            json_shipping = item.getJSONObject("shippingInfo");
        } catch (JSONException e) {
            //error
        }
        titleval = basicInfo.optString("title");
        galleryURL = basicInfo.optString("galleryURL");
        viewItemURL = basicInfo.optString("viewItemURL");
        pictureURLSuperSize = basicInfo.optString("pictureURLSuperSize");
        convertedCurrentPrice = basicInfo.optString("convertedCurrentPrice");
        shippingServiceCost = basicInfo.optString("shippingServiceCost");
        locationVal = basicInfo.optString("location");
        topRatedListing = basicInfo.optString("topRatedListing");

        imageURL = "";
        priceVal = "";
        layout_basic_info.setVisibility(View.VISIBLE);
        layout_seller.setVisibility(View.INVISIBLE);
        layout_shipping.setVisibility(View.INVISIBLE);
        if (galleryURL.equalsIgnoreCase("")) {
            imageURL = pictureURLSuperSize;
        } else {
            imageURL = galleryURL;
        }

        if (shippingServiceCost.equalsIgnoreCase("N/A")) {
            priceVal = "Price: $" + convertedCurrentPrice + " (FREE Shipping)";
        } else {
            if (Float.parseFloat(shippingServiceCost.toString()) > 0) {
                priceVal = "Price: $" + convertedCurrentPrice + " (+ $" + shippingServiceCost + " Shipping)";
            } else {
                priceVal = "Price: $" + convertedCurrentPrice + " (FREE Shipping)";
            }
        }

        title.setText(titleval);
        price.setText(priceVal);
        location.setText(locationVal);
        if (topRatedListing.equalsIgnoreCase("true")) {
            toprated.setVisibility(View.VISIBLE);
        } else {
            toprated.setVisibility(View.INVISIBLE);
        }

        category_name.setText(basicInfo.optString("categoryName"));
        condition.setText(basicInfo.optString("conditionDisplayName"));
        buying_format.setText(basicInfo.optString("listingType"));


        user_name.setText(json_seller.optString("sellerUserName"));
        feedback_score.setText(json_seller.optString("feedbackScore"));
        positive_feedback.setText(json_seller.optString("positiveFeedbackPercent"));
        feedback_rating.setText(json_seller.optString("feedbackRatingStar"));
        if(json_seller.optString("topRatedSeller").equalsIgnoreCase("true")) {
            top_rated.setImageResource(R.drawable.checkgreen);
        }
        String seller_name=(json_seller.optString("sellerStoreName"));
        if(seller_name.equalsIgnoreCase("N/A")){
            store.setText(json_seller.optString("sellerStoreName"));
        }
        else {
            store.setClickable(true);
            store.setMovementMethod(LinkMovementMethod.getInstance());
            String sellerStoreURL = json_seller.optString("sellerStoreURL");
            String text = "<a href='" + sellerStoreURL + "'> " + seller_name + " </a>";
            store.setText(Html.fromHtml(text));
        }


        String shippingtype=json_shipping.optString("shippingType");
        String[] list = shippingtype.split("(?<=[a-z])(?=[A-Z])");
        Integer length=list.length;
        Integer i=1;
        StringBuilder builder = new StringBuilder();
        for(String s : list) {
            if(length>i) {
                builder.append(s);
                builder.append(", ");
            }
            else{
                builder.append(s);
            }
            i=i+1;
        }
        shipping_type.setText(builder);
        handling_time.setText(json_shipping.optString("handlingTime"));
        shipping_location.setText(json_shipping.optString("shipToLocations"));
        if(json_shipping.optString("expeditedShipping").equalsIgnoreCase("true")) {
            expedited_shipping.setImageResource(R.drawable.checkgreen);
        }
        if(json_shipping.optString("oneDayShippingAvailable").equalsIgnoreCase("true")) {
            one_day_shipping.setImageResource(R.drawable.checkgreen);
        }
        if(json_shipping.optString("returnsAccepted").equalsIgnoreCase("true")) {
            return_accepted.setImageResource(R.drawable.checkgreen);
        }
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                URL url;
                try {
                    url = new URL(params[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    is = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                if (result != null)
                    main_Image.setImageBitmap(result);
            }

        }.execute(imageURL);

        buy_now.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(viewItemURL));
                startActivity(intent);
            }
        });

        basic_info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                layout_basic_info.setVisibility(View.VISIBLE);
                layout_seller.setVisibility(View.INVISIBLE);
                layout_shipping.setVisibility(View.INVISIBLE);
                basic_info.setBackgroundColor(Color.GRAY);
                seller.setBackgroundColor(Color.LTGRAY);
                shipping.setBackgroundColor(Color.LTGRAY);
            }
        });

        seller.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                layout_basic_info.setVisibility(View.INVISIBLE);
                layout_seller.setVisibility(View.VISIBLE);
                layout_shipping.setVisibility(View.INVISIBLE);
                basic_info.setBackgroundColor(Color.LTGRAY);
                seller.setBackgroundColor(Color.GRAY);
                shipping.setBackgroundColor(Color.LTGRAY);
            }
        });

        shipping.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                layout_basic_info.setVisibility(View.INVISIBLE);
                layout_seller.setVisibility(View.INVISIBLE);
                layout_shipping.setVisibility(View.VISIBLE);
                basic_info.setBackgroundColor(Color.LTGRAY);
                seller.setBackgroundColor(Color.LTGRAY);
                shipping.setBackgroundColor(Color.GRAY);
            }
        });
    }

    public void facebookLogin()
    {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_friends"));
    }

}
