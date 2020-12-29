package com.animixer.weatherly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.skydoves.transformationlayout.TransformationLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLEngineResult;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FusedLocationProviderClient fusedLocationClient;
    private TextView mTemp, mPlace, mCondition, mHumidity, mHumidityValue, mSunraiseValue, mSunsetValue, mWindspeed,mPlaceTime,dbCurrentPlace,dbCurrentCountry,mHintText;
    private ConstraintLayout mBackgroundColour, mInstaFollow,mLocationAddLayout,mLocSearchBtn,mNotFoundLayout,dbCurrentPlaceLayout;
    private MotionLayout mMainLayout;
    private DrawerLayout mActivityLoyout;
    private LottieAnimationView mAnimationView,mSearchAnimation;
    private ImageView mMenuBar, mMenuClose, mLocationIcon, mLocationLyoutClose;
    private LocationCallback locationCallback;
    private LocationRequest request;
    private NavigationView mNavigationView;
    private TransformationLayout mSearchButton;
    private EditText mLocSearchText;
    private String savedUrl;
    private ArrayList<Integer> visibilityList = new ArrayList<>();
    private ArrayList<Integer> dbCurrentPlaceList = new ArrayList<>();
    private RecyclerView mSearchList;

    private CityAdapter cityAdapter ;
    private List<PlaceList> listItems ;
    final ArrayList<String> LatLongList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));

        mTemp = findViewById(R.id.main_temp);
        mPlace = findViewById(R.id.place_name);
        mCondition = findViewById(R.id.weather_condition);
        mHumidity = findViewById(R.id.humidity);
        mHumidityValue = findViewById(R.id.humidity_value);
        mSunraiseValue = findViewById(R.id.sunraise_time);
        mSunsetValue = findViewById(R.id.sunset_time);
        mWindspeed = findViewById(R.id.windspeed_value);
        mPlaceTime = findViewById(R.id.place_time);
        mBackgroundColour = findViewById(R.id.day_time_layout);
        mAnimationView = findViewById(R.id.weather_animation);
        mMenuBar = findViewById(R.id.menu_icon);
        mLocationIcon = findViewById(R.id.location_icon);
        mMainLayout = findViewById(R.id.main_layout);
        mActivityLoyout = findViewById(R.id.main_activity_layout);
        mNavigationView = findViewById(R.id.main_navigation_drawer);
        mMenuClose = findViewById(R.id.navigation_close);
        mInstaFollow = findViewById(R.id.follow_insta);
        mSearchButton = findViewById(R.id.search_icon_layout);
        mLocationLyoutClose = findViewById(R.id.loc_search_close);
        mLocationAddLayout = findViewById(R.id.loc_search_layout);
        mNotFoundLayout = findViewById(R.id.not_found_layout);
        dbCurrentCountry = findViewById(R.id.db_current_place_country);
        dbCurrentPlace = findViewById(R.id.db_current_loc_name);
        dbCurrentPlaceLayout = findViewById(R.id.db_current_loc);
        mLocSearchBtn = findViewById(R.id.loc_search_btn);
        mLocSearchText = findViewById(R.id.search_loc_place);
        mSearchAnimation = findViewById(R.id.loc_search_animation);
        mHintText = findViewById(R.id.loc_search_hint);
        mSearchList = findViewById(R.id.loc_search_recyclerview);

        mSearchList.setHasFixedSize(true);
        mSearchList.setLayoutManager(new LinearLayoutManager(this));

        mNotFoundLayout.setVisibility(View.INVISIBLE);

        mNavigationView.bringToFront();
        mNavigationView.setNavigationItemSelectedListener(this);

        mLocSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mLocSearchText.getText().toString();
                if (name.length()==0){
                    Toast.makeText(MainActivity.this,"Enter city name",Toast.LENGTH_LONG).show();
                }
                else{
                    String url = "https://nominatim.openstreetmap.org/search?q="+name+"&format=geojson&accept-language=en";
                    GetLoc(url);
                    hideSoftKeyboard(MainActivity.this);
                    mHintText.setVisibility(View.INVISIBLE);
                    mSearchAnimation.setVisibility(View.VISIBLE);
                    mSearchAnimation.playAnimation();
                    mNotFoundLayout.setVisibility(View.INVISIBLE);
                    mSearchList.setAdapter(null);
                }
            }
        });

        mMenuBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivityLoyout.openDrawer(GravityCompat.START);
            }
        });

        mMenuClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivityLoyout.closeDrawer(GravityCompat.START);
            }
        });

        mInstaFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "https://instagram.com/animixerstudio";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                mActivityLoyout.closeDrawer(GravityCompat.START);
            }
        });

        mSearchButton.bindTargetView(mLocationAddLayout);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mActivityLoyout.closeDrawer(GravityCompat.START);
                mNotFoundLayout.setVisibility(View.INVISIBLE);
                mSearchList.setAdapter(null);
                mLocSearchText.setText("");
                visibilityList.add(1);
                mSearchButton.startTransform();
                mSearchAnimation.setVisibility(View.VISIBLE);
                mSearchAnimation.playAnimation();
                LatLongList.clear();
            }
        });

        mLocationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetWeather(savedUrl,"azbycx");
            }
        });

        dbCurrentPlaceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetWeather(savedUrl,"azbycx");
                mActivityLoyout.closeDrawer(GravityCompat.START);
            }
        });

        mLocationLyoutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchButton.finishTransform();
                visibilityList.remove(0);

            }
        });

        mSearchList.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), mSearchList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (!LatLongList.get((position*3)+1).equals(null) && !LatLongList.get((position*3)+2).equals(null) && !LatLongList.get(position*3).equals(null)){

                    String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + LatLongList.get((position*3)+1) + "&lon=" + LatLongList.get((position*3)+2) + "&appid=cf7278572a17c744221a0dd89c3b2ac3" + "&units=Metric";
                    GetWeather(url,LatLongList.get(position*3));
                    mSearchButton.finishTransform();
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(MainActivity.this,"Location "+(position+1)+" of "+cityAdapter.getItemCount(),Toast.LENGTH_SHORT).show();
            }
        }));

        /*-------------------Fused Location----------------------------*/
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        request = LocationRequest.create();
        request.setInterval(4000);
        request.setFastestInterval(2000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location loc = locationResult.getLastLocation();
                fusedLocationClient.removeLocationUpdates(locationCallback);
                double lon = loc.getLongitude();
                double lat = loc.getLatitude();

                String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + getString(R.string.api_key) + "&units=Metric";
                savedUrl = url;
                GetWeather(url,"azbycx");



            }
        };

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double lon = location.getLongitude();
                    double lat = location.getLatitude();

                    //Toast.makeText(MainActivity.this, String.valueOf(lon), Toast.LENGTH_LONG).show();

                    String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + getString(R.string.api_key) + "&units=Metric";
                    savedUrl = url;
                    GetWeather(url,"azbycx");

                } else {
                         if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                            return;
                        }

                        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
                        }
                    }
                });


    }


    private void GetWeather(String url, final String CityName) {


        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {

                    JSONObject main_obj = response.getJSONObject("main");
                    JSONObject system_obj = response.getJSONObject("sys");
                    JSONObject wind_obj = response.getJSONObject("wind");
                    JSONArray main_array = response.getJSONArray("weather");
                    JSONObject weather = main_array.getJSONObject(0);

                    double tm = main_obj.getDouble("temp");
                    double hum = main_obj.getDouble("humidity");
                    String loc = response.getString("name");
                    String country = system_obj.getString("country");
                    long sunraise = system_obj.getLong("sunrise");
                    long sunset = system_obj.getLong("sunset");
                    long dt = response.getLong("dt");
                    long tz = response.getLong("timezone");
                    double wind = wind_obj.getDouble("speed");
                    String condition = weather.getString("description");
                    int id = weather.getInt("id");

                    Date sun_raise_date = new java.util.Date((sunraise+tz)*1000L);
                    Date sun_set_date = new java.util.Date((sunset+tz)*1000L);
                    Date current_time = new java.util.Date((dt+tz)*1000L);
                    Date current_tm = new java.util.Date((tz)*1000L);

                    SimpleDateFormat sdf = new java.text.SimpleDateFormat(" hh:mm aa");
                    SimpleDateFormat stf = new java.text.SimpleDateFormat("HH");
                    SimpleDateFormat ctf = new java.text.SimpleDateFormat(" hh:mm aa");
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
                    stf.setTimeZone(TimeZone.getTimeZone(String.valueOf(tz)));
                    ctf.setTimeZone(TimeZone.getTimeZone(String.valueOf(tz)));
                    String sun_raise_time = sdf.format(sun_raise_date);
                    String sun_set_time = sdf.format(sun_set_date);
                    String place_time = ctf.format(current_time);
                    String ct = stf.format(current_time);

                    int time = Integer.parseInt(ct);

                    double windspeed = (wind/1000)*3600;

                    Locale l = new Locale("", country);

                    String upperCaseCondition = condition.substring(0, 1).toUpperCase() + condition.substring(1).toLowerCase();

                    if (!CityName.equals("azbycx")){
                        loc = CityName;
                    }

                    mTemp.setText(String.valueOf(Math.round(tm)));
                    mHumidityValue.setText(String.valueOf((int)hum)+"%");
                    mPlace.setText(loc+", "+String.valueOf(l.getDisplayCountry()));
                    mWindspeed.setText(String.valueOf((int)windspeed)+" Km/H");
                    mCondition.setText(upperCaseCondition);
                    mSunraiseValue.setText(sun_raise_time);
                    mSunsetValue.setText(sun_set_time);
                    mPlaceTime.setText("Last updated at :\n"+place_time+" (Local)");

                    if (dbCurrentPlaceList.size()==0){
                        dbCurrentPlace.setText(loc+", ");
                        dbCurrentCountry.setText(l.getDisplayCountry());
                        dbCurrentPlaceList.add(1);
                    }

                    SetBackgroundColour(time);
                    SetAnimation(time,id);

                }catch(Exception a) {
                    a.printStackTrace();
                    Snackbar sbar = Snackbar.make(mMainLayout,"Some error occured !", Snackbar.LENGTH_INDEFINITE).setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    });

                    sbar.setActionTextColor(ContextCompat.getColor(MainActivity.this, R.color.DayStart));

                    sbar.show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Snackbar sbar = Snackbar.make(mMainLayout,"Please check location", Snackbar.LENGTH_LONG).setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                sbar.setActionTextColor(ContextCompat.getColor(MainActivity.this, R.color.DayStart));

                sbar.show();
                
            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);

    }

    private void SetAnimation(int time, int id) {

        makeVisible();

        if (id>=200 && id<=210 || id >=220 && id<=232){

                mAnimationView.setAnimation(R.raw.thunder_strom);
                mAnimationView.playAnimation();
                mAnimationView.loop(true);

        }
        else if (id>=211 && id<220){

            mAnimationView.setAnimation(R.raw.thunder_cloud);
            mAnimationView.playAnimation();
            mAnimationView.loop(true);

        }
        else if (id>=300 && id<=321){
            if (time>=5 && time<=17){
                mAnimationView.setAnimation(R.raw.rain_day);
                mAnimationView.playAnimation();
                mAnimationView.loop(true);
            }
            else {
                mAnimationView.setAnimation(R.raw.rain_night);
                mAnimationView.playAnimation();
                mAnimationView.loop(true);
            }
        }
        else if (id>=500 && id<=531){
            mAnimationView.setAnimation(R.raw.rain);
            mAnimationView.playAnimation();
            mAnimationView.loop(true);
        }
        else if (id>=600 && id<=611){
            mAnimationView.setAnimation(R.raw.snow);
            mAnimationView.playAnimation();
            mAnimationView.loop(true);
        }
        else if (id>=612 && id<=622){
            if (time>=5 && time<=17){
                mAnimationView.setAnimation(R.raw.snow_day);
                mAnimationView.playAnimation();
                mAnimationView.loop(true);
            }
            else {
                mAnimationView.setAnimation(R.raw.snow_night);
                mAnimationView.playAnimation();
                mAnimationView.loop(true);
            }
        }
        else if (id>=701 && id<=730){
            if (time>=8 && time<=17){
                mAnimationView.setAnimation(R.raw.sun);
                mAnimationView.playAnimation();
                mAnimationView.loop(true);
                //mCondition.setText("Clear sky");
            }
            else if (time>=18 && time<=24 || time>=1 && time<=4 ){
                mAnimationView.setAnimation(R.raw.moon);
                mAnimationView.playAnimation();
                mAnimationView.loop(true);
                //mCondition.setText("Clear sky");

            }
            else {
                mAnimationView.setAnimation(R.raw.mist);
                mAnimationView.playAnimation();
                mAnimationView.loop(true);
            }

        }
        else if (id>=732 && id<=750){
            mAnimationView.setAnimation(R.raw.fog);
            mAnimationView.playAnimation();
            mAnimationView.loop(true);
        }
        else if (id>=751 && id<=781){
            mAnimationView.setAnimation(R.raw.strom);
            mAnimationView.playAnimation();
            mAnimationView.loop(true);
        }
        else if (id==800){
            if (time>=5 && time<=17){
                mAnimationView.setAnimation(R.raw.sun);
                mAnimationView.playAnimation();
                mAnimationView.loop(true);
            }
            else {
                mAnimationView.setAnimation(R.raw.moon);
                mAnimationView.playAnimation();
                mAnimationView.loop(true);
            }
        }
        else if (id>=801 && id<=810){
            if (time>=5 && time<=17){
                mAnimationView.setAnimation(R.raw.cloud_day);
                mAnimationView.playAnimation();
                mAnimationView.loop(true);
            }
            else {
                mAnimationView.setAnimation(R.raw.cloud_night);
                mAnimationView.playAnimation();
                mAnimationView.loop(true);
            }
        }
        else {
            mAnimationView.setAnimation(R.raw.cloud);
            mAnimationView.playAnimation();
            mAnimationView.loop(true);
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }


    private void makeVisible() {

        Animation aniFade = AnimationUtils.loadAnimation(MainActivity.this,R.anim.fade_in_500);
        mMainLayout.startAnimation(aniFade);
        mMainLayout.setVisibility(View.VISIBLE);

    }

    @SuppressLint("ResourceAsColor")
    private void SetBackgroundColour(int time) {

        if (time>=5 && time <= 7 ){
            mBackgroundColour.setBackgroundResource(R.drawable.morning_back);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.MorningStart));
            mHumidityValue.setTextColor(getResources().getColor(R.color.MorningStart));
            mWindspeed.setTextColor(getResources().getColor(R.color.MorningStart));
            mSunraiseValue.setTextColor(getResources().getColor(R.color.MorningStart));
            mSunsetValue.setTextColor(getResources().getColor(R.color.MorningStart));

        }
        else if (time>=8 && time <= 16 ){
            mBackgroundColour.setBackgroundResource(R.drawable.day_back);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.DayStart));
            mHumidityValue.setTextColor(getResources().getColor(R.color.DayStart));
            mWindspeed.setTextColor(getResources().getColor(R.color.DayStart));
            mSunraiseValue.setTextColor(getResources().getColor(R.color.DayStart));
            mSunsetValue.setTextColor(getResources().getColor(R.color.DayStart));

        }
        else if (time>=17 && time <=18 || time>=4 && time<=5) {
            mBackgroundColour.setBackgroundResource(R.drawable.evening_back);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.EveningStart));
            mHumidityValue.setTextColor(getResources().getColor(R.color.EveningStart));
            mWindspeed.setTextColor(getResources().getColor(R.color.EveningStart));
            mSunraiseValue.setTextColor(getResources().getColor(R.color.EveningStart));
            mSunsetValue.setTextColor(getResources().getColor(R.color.EveningStart));

        }
        else {
            mBackgroundColour.setBackgroundResource(R.drawable.night_back);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.NightStart));
            mHumidityValue.setTextColor(getResources().getColor(R.color.NightStart));
            mWindspeed.setTextColor(getResources().getColor(R.color.NightStart));
            mSunraiseValue.setTextColor(getResources().getColor(R.color.NightStart));
            mSunsetValue.setTextColor(getResources().getColor(R.color.NightStart));
        }

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    /*-------------------Search City---------------------------*/


    private void GetLoc(String url) {



        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {

                    JSONArray main_array = response.getJSONArray("features");

                    int size = main_array.length();

                    if (size==0){
                        mNotFoundLayout.setVisibility(View.VISIBLE);
                    }
                    else {
                        mNotFoundLayout.setVisibility(View.INVISIBLE);
                    }

                    mSearchAnimation.setVisibility(View.INVISIBLE);

                    Locale locale = new Locale("en");

                    listItems = new ArrayList<>();
                    String a1 = "", b1 = "", c1 = "", a2 = "", b2 = "", c2 = "";

                    for (int i = 0; i<size; i++){
                        JSONObject all_details = main_array.getJSONObject(i);
                        JSONObject details_obj = all_details.getJSONObject("properties");
                        JSONObject geometry = all_details.getJSONObject("geometry");
                        JSONArray latlon = geometry.getJSONArray("coordinates");

                        String lat = latlon.getString(1).toString();
                        String lon = latlon.getString(0).toString();

                        String DisplayName = details_obj.getString("display_name");

                        String[] DataList = DisplayName.split("[,]", 0);

                        PlaceList list ;

                        int siz = DataList.length;

                        if (i==0){
                            if (siz==1){
                                a1 = DataList[0].trim();
                                b1 = "";
                                c1 = "";

                            }
                            else if (siz==2){
                                a1 = DataList[0].trim();
                                b1 = "";
                                c1 = DataList[1].trim();
                            }
                            else if (siz==3){
                                a1 = DataList[0].trim();
                                b1 = DataList[1].trim();
                                c1 = DataList[2].trim();
                            }
                            else if (siz>3){
                                a1 = DataList[0].trim();
                                b1 = DataList[siz-3].trim();
                                c1 = DataList[siz-1].trim();
                            }

                            list = new PlaceList(a1,b1,c1);
                            LatLongList.add(a1);
                            LatLongList.add(lat);
                            LatLongList.add(lon);
                            listItems.add(list);
                        }
                        else {
                            if (siz==1){
                                a2 = DataList[0].trim();
                                b2 = "";
                                c2 = "";

                            }
                            else if (siz==2){
                                a2 = DataList[0].trim();
                                b2 = "";
                                c2 = DataList[1].trim();
                            }
                            else if (siz==3){
                                a2 = DataList[0].trim();
                                b2 = DataList[1].trim();
                                c2 = DataList[2].trim();
                            }
                            else if (siz>3){
                                a2 = DataList[0].trim();
                                b2 = DataList[siz-3].trim();
                                c2 = DataList[siz-1].trim();
                            }

                            if (a2.equals(a1) && b2.equals(b1) && c2.equals(c1)){

                            }
                            else {
                                a1=a2;
                                b1=b2;
                                c1=c2;
                                list = new PlaceList(a1,b1,c1);
                                LatLongList.add(a1);
                                LatLongList.add(lat);
                                LatLongList.add(lon);
                                listItems.add(list);
                            }



                        }

                    }

                    cityAdapter = new CityAdapter(listItems,getApplicationContext());

                    mSearchList.setAdapter(cityAdapter);


                }catch(Exception a) {
                    a.printStackTrace();
                    mNotFoundLayout.setVisibility(View.VISIBLE);
                    mSearchAnimation.setVisibility(View.INVISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String er = String.valueOf(error);

                mNotFoundLayout.setVisibility(View.VISIBLE);
                mSearchAnimation.setVisibility(View.INVISIBLE);

            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);

    }

    @Override
    public void onBackPressed() {

        if (visibilityList.size()==1){
            mSearchButton.finishTransform();
            visibilityList.remove(0);
        }
        else {
            super.onBackPressed();
        }
    }
}

class GpsTracker extends Service implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}