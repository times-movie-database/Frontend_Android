package com.example.moviezzzz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.moviezzzz.Api.Urls;
import com.example.moviezzzz.Utils.CheckInternetStatus;
import com.example.moviezzzz.Api.ApiController;
import com.example.moviezzzz.Model.Moviedetail;
import com.example.moviezzzz.Model.ReviewData;
import com.example.moviezzzz.Adapter.MovieDetailsAdapter;
import com.example.moviezzzz.Utils.PaginationScrollListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * MOVIE DETAILS ACTIVITY
 */
public class Movie_Details extends AppCompatActivity {
    private static final String TAG = "Movie_Details_Page";
    String mtitle,movie_id;
    MovieDetailsAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rv;
    List<ReviewData> reviewDataList;
    CheckInternetStatus mCheckInternetStatus;
    boolean is_internet_connected;
    Dialog dialog;
    Moviedetail moviedetail;
    Urls apiBaseUrl;

    private static final int PAGE_START = 0;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private static final int TOTAL_PAGES = 5;
    private int currentPage = PAGE_START;
    private int pagesize=5;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        apiBaseUrl=new Urls();
        progressDialog=new ProgressDialog(Movie_Details.this);
        progressDialog.setContentView(R.layout.progress_dialog);

        rv = findViewById(R.id.mdrecyclerview);

        reviewDataList=new ArrayList<>();
        mCheckInternetStatus = new CheckInternetStatus(Movie_Details.this);
        dialog = new Dialog(this);

        Intent intent=getIntent();
        mtitle=intent.getStringExtra("titlee");
        movie_id=intent.getStringExtra("id");

        initToolbar();

        is_internet_connected = mCheckInternetStatus.isInternetConnected();

        if(!is_internet_connected) {
            showCustomDialog();
        }
        else {
            progressDialog.show();
            loadfirstReviewsPage();
            generalmovie();
        }

    }

    /**
     * Initiating the Toolbar i.e icon, color ,Text
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_moviedetails);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setTitle(mtitle);
        toolbar.setTitleTextColor(Color.WHITE);
    }

    /**
     * onOptionsItemSelected ( It is used for handling the Menu Items )
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Custom Dialog ( It will Display when there is No Internet Connection )
     */
    private void showCustomDialog() {
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
     * Api calling for loading general movie detaiils based on Movie ID
     */
    private void generalmovie() {
    Call<Moviedetail> call = ApiController.getInstance(apiBaseUrl.getBaseUrlJava()).getapi().findDetail(Integer.valueOf(movie_id));
    call.enqueue(new Callback<Moviedetail>() {
        @Override
        public void onResponse(Call<Moviedetail> call, Response<Moviedetail> response) {

            if(response.code()!=200||!(response.isSuccessful()))
            {
                Intent i = new Intent(Movie_Details.this, ErrorHandled.class);
                i.putExtra("activity", "Mvd");
                i.putExtra("cd", String.valueOf(response.code()));
                i.putExtra("titlee",mtitle);
                i.putExtra("id",movie_id);
                startActivity(i);
            }
            else {
                moviedetail = response.body();
                adapter = new MovieDetailsAdapter(movie_id,reviewDataList, moviedetail, Movie_Details.this);
                linearLayoutManager = new LinearLayoutManager(Movie_Details.this, LinearLayoutManager.VERTICAL, false);
                rv.setLayoutManager(linearLayoutManager);
                rv.setItemAnimator(new DefaultItemAnimator());
                progressDialog.dismiss();
                rv.setAdapter(adapter);

                rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
                    @Override
                    protected void loadMoreItems() {
                        isLoading = true;
                        currentPage += 1;
                        adapter.addLoadingFooter();
                        loadNextReviewsPage();

                    }

                    @Override
                    public int getTotalPageCount() {
                        return TOTAL_PAGES;
                    }

                    @Override
                    public boolean isLastPage() {
                        return isLastPage;
                    }

                    @Override
                    public boolean isLoading() {
                        return isLoading;
                    }
                });
            }
        }

        @Override
        public void onFailure(Call<Moviedetail> call, Throwable t) {
            Toast.makeText(Movie_Details.this, "Sorry, something went wrong"+t.getMessage(), Toast.LENGTH_LONG).show();
        }
    });
}

    /**
     * Api calling for loading first movie review page based on Movie ID, Page size and Starting page
     */
    private void loadfirstReviewsPage() {
        Call<List<ReviewData>> call = ApiController.getInstance(apiBaseUrl.getBaseUrlJava()).getapi().findReviewss(Integer.valueOf(movie_id),PAGE_START,pagesize);
        call.enqueue(new Callback<List<ReviewData>>() {
            @Override
            public void onResponse(Call<List<ReviewData>> call, Response<List<ReviewData>> response) {
                if(response.code()==204) {
//                    Toast.makeText(Movie_Details.this, "Sorry, No reviews are added", Toast.LENGTH_SHORT).show();
                }
                else if(response.code()!=200||!(response.isSuccessful()))
                {
                    Intent i = new Intent(Movie_Details.this, ErrorHandled.class);
                    i.putExtra("activity", "Mvd");
                    i.putExtra("cd", String.valueOf(response.code()));
                    i.putExtra("titlee",mtitle);
                    i.putExtra("id",movie_id);
                    startActivity(i);
                }
                else {
//                    Log.d("first review", "onResponse: ");

                    progressDialog.dismiss();
                    reviewDataList = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<ReviewData>> call, Throwable t) {
                Toast.makeText(Movie_Details.this, "Sorry, couldn't fetch the reviews", Toast.LENGTH_LONG).show();
                adapter.removeLoadingFooter();
            }
        });
    }

    /**
     * Api calling for loading Next movie review page based on Movie ID, Page size and current page
     */
    private void loadNextReviewsPage() {
        Call<List<ReviewData>> call = ApiController.getInstance(apiBaseUrl.getBaseUrlJava()).getapi().findReviewss(Integer.valueOf(movie_id),currentPage,pagesize);
        call.enqueue(new Callback<List<ReviewData>>() {
            @Override
            public void onResponse(Call<List<ReviewData>> call, Response<List<ReviewData>> response) {

                if(response.code()==204) {
                    adapter.removeLoadingFooter();
                }
                else if(response.code()!=200||!(response.isSuccessful()))
                {

                    Intent i = new Intent(Movie_Details.this, ErrorHandled.class);
                    i.putExtra("activity", "Mvd");
                    i.putExtra("cd", String.valueOf(response.code()));
                    i.putExtra("titlee",mtitle);
                    i.putExtra("id",movie_id);
                    startActivity(i);

                }
                else {
//                    Log.d("Next review", "onResponse: ");
                    progressDialog.dismiss();
                    adapter.removeLoadingFooter();

                    List<ReviewData> updatedreviewlist;
                    updatedreviewlist = response.body();

                    adapter.addAll(updatedreviewlist);
                    isLoading=false;

                }
            }

            @Override
            public void onFailure(Call<List<ReviewData>> call, Throwable t) {
                Toast.makeText(Movie_Details.this, "Sorry, couldn't fetch the reviews", Toast.LENGTH_LONG).show();
                adapter.removeLoadingFooter();
            }
        });
    }

}