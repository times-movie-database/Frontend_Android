package com.example.moviezzzz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.moviezzzz.Api.Urls;
import com.example.moviezzzz.Utils.CheckInternetStatus;
import com.example.moviezzzz.Adapter.MovieAdapter;
import com.example.moviezzzz.Api.ApiController;
import com.example.moviezzzz.Model.GenereData;
import com.example.moviezzzz.Model.MovieBrief;
import com.example.moviezzzz.Utils.PaginationScrollListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * SEARCH ACTIVITY ----> Second Visible Page ( Used for searching a movie either by title or by genre or by both )
 */

public class SearchActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MovieAdapter adapter;
    SearchView searchView;
    Spinner spinner;
    ArrayAdapter spinneradapter;
    List<String> genereAr;
    String genereSelected;
    String searchquery;
    ImageButton imageButton;
    CheckInternetStatus mCheckInternetStatus;
    boolean is_internet_connected;
    Dialog dialog;
    ProgressDialog progressDialog;
    Urls apiBaseUrl;
    LinearLayoutManager linearLayoutManager;

    private static final int PAGE_START = 0;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private static final int TOTAL_PAGES = 5;
    private int currentPage = PAGE_START;
    private int pagesize = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        apiBaseUrl = new Urls();

        progressDialog = new ProgressDialog(SearchActivity.this);
        progressDialog.setContentView(R.layout.progress_dialog);

        mCheckInternetStatus = new CheckInternetStatus(SearchActivity.this);
        dialog = new Dialog(this);
        spinner = findViewById(R.id.gsearchspinner);

        imageButton = findViewById(R.id.searchresults);
        imageButton.setVisibility(View.INVISIBLE);

        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchView = findViewById(R.id.searchBar);
        searchView.onActionViewExpanded();

        genereAr = new ArrayList<>();

        is_internet_connected = mCheckInternetStatus.isInternetConnected();
        if (!is_internet_connected) {
            showCustomDialog();
        } else {
            fillSpinner();

        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchquery = query;
                //Here u can get the value "query" which is entered in the search box.
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                imageButton.setVisibility(View.VISIBLE);
                searchquery = newText;
                //This is your adapter that will be filtered
                return false;
            }
        });

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
                if (!is_internet_connected)
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
     * searchmoviee( It is called to search movie based on search query and genre )
     */
    public void searchmoviee(View view) {
        is_internet_connected = mCheckInternetStatus.isInternetConnected();
        if (!is_internet_connected) {
            showCustomDialog();
        } else {
            progressDialog.show();
            if (!(genereSelected.equals("All")) && searchquery != null)
                getmovies(genereSelected, searchquery);
            else if (genereSelected.equals("All") && searchquery != null)
                getmovies("All", searchquery);
            else if (!(genereSelected.equals("All")) && searchquery == null)
                getmovies(genereSelected, "null");
        }
    }

    /**
     * API CALLS
     */
    /**
     * fillSpinner ( It is used for filling the spinner )
     */
    public void fillSpinner() {
        Call<List<GenereData>> call = ApiController.getInstance(apiBaseUrl.getBaseUrlJava()).getapi().allGeners();
        call.enqueue(new Callback<List<GenereData>>() {
            @Override
            public void onResponse(Call<List<GenereData>> call, Response<List<GenereData>> response) {
                if (response.code() != 200 || !(response.isSuccessful())) {
                    Intent i = new Intent(SearchActivity.this, ErrorHandled.class);
                    i.putExtra("activity", "Srcact");
                    i.putExtra("cd", String.valueOf(response.code()));
                    startActivity(i);
                } else {
                    List<GenereData> obj = response.body();
                    genereAr.add("All");
                    for (GenereData name : obj)
                        genereAr.add(StringUtils.capitalize(name.getName()));
                    spinneradapter = new ArrayAdapter(SearchActivity.this, android.R.layout.simple_list_item_1, genereAr);
                    spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinneradapter);
                }
            }

            @Override
            public void onFailure(Call<List<GenereData>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Sorry, something went wrong", Toast.LENGTH_LONG).show();
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genereSelected = genereAr.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Api calling for loading first movie page based on search query and genre
     */
    private void getmovies(String s1, String s2) {
        Call<List<MovieBrief>> call = ApiController.getInstance(apiBaseUrl.getBaseUrlJava()).getapi().SearchName(s2, s1, PAGE_START);
        call.enqueue(new Callback<List<MovieBrief>>() {
            @Override
            public void onResponse(Call<List<MovieBrief>> call, Response<List<MovieBrief>> response) {
                if (response.code() == 204) {
                    progressDialog.dismiss();
                    Toast.makeText(SearchActivity.this, "Sorry!!! , couldn't find the searched movie", Toast.LENGTH_LONG).show();
                } else if (response.code() != 200 || !(response.isSuccessful())) {
                    Intent i = new Intent(SearchActivity.this, ErrorHandled.class);
                    i.putExtra("activity", "Srcact");
                    i.putExtra("cd", String.valueOf(response.code()));
                    startActivity(i);
                } else {
                    List<MovieBrief> movieBriefList = response.body();
                    adapter = new MovieAdapter(getApplicationContext(), movieBriefList);
                    linearLayoutManager = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    progressDialog.dismiss();
                    recyclerView.setAdapter(adapter);

                    recyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
                        @Override
                        protected void loadMoreItems() {
                            isLoading = true;
                            currentPage += 1;
                            adapter.addLoadingFooter();
                            loadNextmoviePage(s1, s2);
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
            public void onFailure(Call<List<MovieBrief>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Sorry, something went wrong while fetching the movies", Toast.LENGTH_LONG).show();
                adapter.removeLoadingFooter();
            }
        });
    }

    /**
     * Api calling for loading Next movie page based on search query and genre
     */
    private void loadNextmoviePage(String s1, String s2) {
        Call<List<MovieBrief>> call = ApiController.getInstance(apiBaseUrl.getBaseUrlJava()).getapi().SearchName(s2, s1, currentPage);
        call.enqueue(new Callback<List<MovieBrief>>() {
            @Override
            public void onResponse(Call<List<MovieBrief>> call, Response<List<MovieBrief>> response) {
                if (response.code() == 204) {
                    adapter.removeLoadingFooter();
//                    Toast.makeText(SearchActivity.this, "Sorry!!! , couldn't find more movies", Toast.LENGTH_LONG).show();
                } else if (response.code() != 200 || !(response.isSuccessful())) {
                    Intent i = new Intent(SearchActivity.this, ErrorHandled.class);
                    i.putExtra("activity", "Srcact");
                    i.putExtra("cd", String.valueOf(response.code()));
                    startActivity(i);
                } else {
                    progressDialog.dismiss();
                    adapter.removeLoadingFooter();
                    List<MovieBrief> updatedmovielist = response.body();
                    adapter.addAll(updatedmovielist);
                    isLoading = false;
                }
            }

            @Override
            public void onFailure(Call<List<MovieBrief>> call, Throwable t) {
                adapter.removeLoadingFooter();
                Toast.makeText(SearchActivity.this, "Sorry, something went wrong while fetching the movies", Toast.LENGTH_LONG).show();
            }
        });
    }

}