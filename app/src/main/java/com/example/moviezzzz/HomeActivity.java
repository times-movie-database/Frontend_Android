package com.example.moviezzzz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.moviezzzz.Adapter.MovieAdapter;
import com.example.moviezzzz.Api.ApiController;
import com.example.moviezzzz.Api.Urls;
import com.example.moviezzzz.Model.GenereData;
import com.example.moviezzzz.Model.MovieBrief;
import com.example.moviezzzz.Utils.CheckInternetStatus;
import com.example.moviezzzz.databinding.ActivityHomeBinding;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * HOME ACTIVITY ----> First Visible Page
 */
public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding activityHomeBinding;
    private Toolbar toolbar;
    RecyclerView recyclerView;
    MovieAdapter adapter;
    ArrayAdapter spinneradapter;
    Spinner spinner;
    String genereSelected;
    List<String> genereArr;
    CheckInternetStatus mCheckInternetStatus;
    boolean is_internet_connected;
    Dialog dialog;
    ProgressDialog progressDialog;
    Urls apiBaseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        apiBaseUrl = new Urls();
        dialog = new Dialog(this);
        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setContentView(R.layout.progress_dialog);
        spinner = activityHomeBinding.gspinner;
        spinneradapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.allgenre));
        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinneradapter);

        /**
         * Setting Toolbar icon, color ,Text
         */
        toolbar = (Toolbar) activityHomeBinding.toolbarMain;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.tmdb_logo);
        getSupportActionBar().setTitle("TMDb");
        toolbar.setTitleTextColor(Color.WHITE);

        genereArr = new ArrayList<>();

        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCheckInternetStatus = new CheckInternetStatus(HomeActivity.this);
        is_internet_connected = mCheckInternetStatus.isInternetConnected();

        if (!is_internet_connected) {
            showCustomDialog();
        } else {
            progressDialog.dismiss();
            progressDialog.show();
            fillSpinner();// To fill the genre Spinner
        }
    }

    /**
     * onCreateOptionsMenu ( It is used for inflating Menu Layout )
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * onOptionsItemSelected ( It is used for handling the Menu Items )
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.search) {

            is_internet_connected = mCheckInternetStatus.isInternetConnected();
            if (!is_internet_connected)
                showCustomDialog();
            progressDialog.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            }, 5000);

            startActivity(new Intent(this, SearchActivity.class));

        } else if (item.getItemId() == R.id.action_add) {
            is_internet_connected = mCheckInternetStatus.isInternetConnected();
            if (!is_internet_connected)
                showCustomDialog();
            Intent i = new Intent(HomeActivity.this, AddEditMovie.class);
            i.putExtra("activity", "homeact");
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Custom Dialog ( It will Display when there is No Internet Connection )
     */
    private void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.netwok_dialog);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_internet_connected = mCheckInternetStatus.isInternetConnected();
                if(!is_internet_connected)
                    showCustomDialog();
                else {
                    dialog.dismiss();
                    fillSpinner();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    /**
     * API CALLS
     */
    /**
     * fillSpinner ( It is used for filling the spinner )
     */
    private void fillSpinner() {
        Call<List<GenereData>> call = ApiController.getInstance(apiBaseUrl.getBaseUrlJava()).getapi().allGeners();
        call.enqueue(new Callback<List<GenereData>>() {
            @Override
            public void onResponse(Call<List<GenereData>> call, Response<List<GenereData>> response) {
                if (response.code() != 200 || !(response.isSuccessful()))
                {
                    Intent i = new Intent(HomeActivity.this, ErrorHandled.class);
                    i.putExtra("activity", "homeact");
                    i.putExtra("cd", String.valueOf(response.code()));
                    startActivity(i);
                }
                else {
                    List<GenereData> obj = response.body();
                    genereArr.add("All");
                    for (GenereData name : obj)
                        genereArr.add(StringUtils.capitalize(name.getName()));
                    spinneradapter = new ArrayAdapter(HomeActivity.this, android.R.layout.simple_list_item_1, genereArr);
                    spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinneradapter);

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            genereSelected = genereArr.get(position);
                            progressDialog.show();
                            if(genereSelected.equals("All"))
                                getmovies();
                            else
                                getfilteredTop10(genereSelected);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<List<GenereData>> call, Throwable t) {

                Toast.makeText(HomeActivity.this, "Sorry, something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Api calling for getting movies when No Genre is selected
     */
    private void getmovies() {

        Call<List<MovieBrief>> call = ApiController.getInstance(apiBaseUrl.getGetMoviesNode()).getapi().getTopten();
        call.enqueue(new Callback<List<MovieBrief>>() {
            @Override
            public void onResponse(Call<List<MovieBrief>> call, Response<List<MovieBrief>> response) {
                if (response.code() != 200 || !(response.isSuccessful()))
                {
                    Intent i = new Intent(HomeActivity.this, ErrorHandled.class);
                    i.putExtra("activity", "homeact");
                    i.putExtra("cd", String.valueOf(response.code()));
                    startActivity(i);
                }
                else {
                    List<MovieBrief> obj = response.body();
                    adapter = new MovieAdapter(getApplicationContext(),obj);
                    progressDialog.dismiss();
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<MovieBrief>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Sorry, something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Api calling for getting movies when Genre is selected
     */
    private void getfilteredTop10(String genereSelected) {
        Call<List<MovieBrief>> call = ApiController.getInstance(apiBaseUrl.getGetMoviesNode()).getapi().generTopten(genereSelected);
        call.enqueue(new Callback<List<MovieBrief>>() {
            @Override
            public void onResponse(Call<List<MovieBrief>> call, Response<List<MovieBrief>> response) {
                if (response.code() != 200 || !(response.isSuccessful()))
                {
                    Intent i = new Intent(HomeActivity.this, ErrorHandled.class);
                    i.putExtra("activity", "homeact");
                    i.putExtra("cd", String.valueOf(response.code()));
                    startActivity(i);
                }
                else {
                    List<MovieBrief> obj = response.body();
                    adapter = new MovieAdapter(getApplicationContext(),obj);
                    progressDialog.dismiss();
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<MovieBrief>> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "Sorry, something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

}