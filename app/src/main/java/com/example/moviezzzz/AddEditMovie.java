package com.example.moviezzzz;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviezzzz.Api.ApiController;
import com.example.moviezzzz.Api.Urls;
import com.example.moviezzzz.Model.AddMovie;
import com.example.moviezzzz.Model.CastList;
import com.example.moviezzzz.Model.Editmovie;
import com.example.moviezzzz.Model.GenereData;
import com.example.moviezzzz.Utils.CheckInternetStatus;
import com.example.moviezzzz.Utils.ScrollTextUtil;
import com.example.moviezzzz.databinding.ActivityAddEditMovieBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * ADDEDIT ACTIVITY ----> New movie can be added and existing movie detail can be edited
 */
public class AddEditMovie extends AppCompatActivity {
    private ScrollTextUtil scrollTextUtil;
    private ActivityAddEditMovieBinding activityAddEditMovieBinding;
    private ArrayAdapter spinneradapter;
    private Spinner spinner;
    private ChipGroup genrechipgrp;
    private TextInputLayout textInputLayouttitle,textInputLayoutsummary,textInputLayoutcast;
    private TextInputEditText textInputEditTexttitle,textInputEditTextsummary,textInputEditTextcast;
    private TextView genreerrorview;
    private List<String>genreArr;
    private List<String>genreArrFinal;
    private HashMap<String,String>genreId;
    private String activityName;
    private AddMovie admobj;
    private String movie_id;
    private CheckInternetStatus mCheckInternetStatus;
    boolean is_internet_connected;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private Urls url;

    private boolean validGenre,validTitle,validSummary,validCast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddEditMovieBinding= DataBindingUtil.setContentView(this, R.layout.activity_add_edit_movie);
        genreArr=new ArrayList<>();
        url=new Urls();
        genreId=new HashMap<>();
        genreArrFinal=new ArrayList<>();
        mCheckInternetStatus = new CheckInternetStatus(AddEditMovie.this);
        dialog = new Dialog(this);
        progressDialog=new ProgressDialog(AddEditMovie.this);
        progressDialog.setContentView(R.layout.progress_dialog);

        genrechipgrp=activityAddEditMovieBinding.chipGroupGenre;

        textInputLayouttitle=activityAddEditMovieBinding.inputLayoutTitle;
        textInputEditTexttitle=activityAddEditMovieBinding.inputTextTitle;
        textInputLayoutsummary=activityAddEditMovieBinding.inputLayoutSummary;
        textInputEditTextsummary=activityAddEditMovieBinding.inputTextSummary;
        textInputLayoutcast=activityAddEditMovieBinding.inputLayoutCast;
        textInputEditTextcast=activityAddEditMovieBinding.inputTextCast;
        genreerrorview=activityAddEditMovieBinding.genreerrorview;

        scrollTextUtil=new ScrollTextUtil();
        scrollTextUtil.enableScroll((TextView)textInputEditTextsummary);
        scrollTextUtil.enableScroll((TextView)textInputEditTextcast);

        textInputLayouttitle.setError("Movie title cannot be empty");
        textInputLayoutcast.setError("Movie cast cannot be empty");
        textInputLayoutsummary.setError("Movie title cannot be empty");

        is_internet_connected = mCheckInternetStatus.isInternetConnected();
        if(!is_internet_connected)
        {
            showCustomDialog();
        }
        else {
            progressDialog.show();
            initgenrecomponent();
        }

        validatetitle();
        validatesummary();
        validatecast();
        validategenre();
        initToolbar();

    }

    /**
     * onCreateOptionsMenu ( It is used for inflating Menu Layout )
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    /**
     * onOptionsItemSelected ( It is used for handling the Menu Items )
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        else if(item.getItemId() == R.id.action_done) {
            is_internet_connected = mCheckInternetStatus.isInternetConnected();
            if(!is_internet_connected)
            {
                showCustomDialog();
            }
            else {
                progressDialog.show();
                editAddToDb();
                //Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initiating the Toolbar i.e icon, color ,Text
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.addmovietoolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setTitle("TMDb");
        toolbar.setTitleTextColor(Color.WHITE);
    }

    /**
     * Initiating the chips and adding into a chipgroup
     */
    private void addChip(String genre,String Id) {
        if(!genre.equals("All")) {
            Chip chip = new Chip(this, null, R.style.Widget_MaterialComponents_Chip_Entry);
            chip.setChipDrawable(ChipDrawable.createFromAttributes(this, null, 0, R.style.Widget_MaterialComponents_Chip_Entry));
            chip.setText(genre);
            chip.setTag(Id);
            chip.getTag();
            chip.setClickable(true);
            chip.setCheckable(false);
            chip.setLayoutParams(new ViewGroup.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.spacing_xxxlarge)));
            chip.setTextColor(getResources().getColor(R.color.grey_60));
            chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.grey_10)));
            chip.setChipIconTint(ColorStateList.valueOf(getResources().getColor(R.color.grey_60)));
            chip.setIconStartPaddingResource(R.dimen.spacing_medium);
            chip.setCloseIconTint(ColorStateList.valueOf(getResources().getColor(R.color.grey_40)));
            chip.setCloseIconEndPaddingResource(R.dimen.spacing_medium);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    genrechipgrp.removeView(chip);
                    validategenre();
                }
            });
            genrechipgrp.addView(chip, 0);
            validategenre();
        }
    }

    /**
     * Taking the data out if details need to edit
     */
    private void receiveDataFromDetails()
    {
        Intent intent = getIntent();
        activityName = intent.getStringExtra("activity");
        if(activityName.equals("movieDetail")) {
            TextView editScreenTitle=findViewById(R.id.addeditmovietextview);
            movie_id = intent.getStringExtra("id");
            textInputEditTexttitle.setText(WordUtils.capitalize(intent.getStringExtra("title")));
            editScreenTitle.setText("Edit "+WordUtils.capitalize(intent.getStringExtra("title")));
            textInputEditTextsummary.setText(intent.getStringExtra("summary"));
            String[] myStrings = intent.getStringArrayExtra("casts");
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i<= myStrings.length-1; i++) {
                if(i == myStrings.length-1) {
                    builder.append(myStrings[i]);
                }else {
                    builder.append(myStrings[i]+",");
                }
            }
            textInputEditTextcast.setText(builder.toString());
            String[] myStringgenre=intent.getStringArrayExtra("genre");
            for(int i=0;i<myStringgenre.length;i++)
            {
                String selected_Id=genreId.get(myStringgenre[i]);
                Chip chip = new Chip(this, null, R.style.Widget_MaterialComponents_Chip_Entry);
                chip.setChipDrawable(ChipDrawable.createFromAttributes(this, null, 0, R.style.Widget_MaterialComponents_Chip_Entry));
                chip.setText(myStringgenre[i]);
                chip.setTag(selected_Id);
                chip.setClickable(true);
                chip.setCheckable(false);
                chip.setLayoutParams(new ViewGroup.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.spacing_xxxlarge)));
                chip.setTextColor(getResources().getColor(R.color.grey_60));
                chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.grey_10)));
                chip.setChipIconTint(ColorStateList.valueOf(getResources().getColor(R.color.grey_60)));
                chip.setIconStartPaddingResource(R.dimen.spacing_medium);
                chip.setCloseIconTint(ColorStateList.valueOf(getResources().getColor(R.color.grey_40)));
                chip.setCloseIconEndPaddingResource(R.dimen.spacing_medium);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            genrechipgrp.removeView(chip);
                            validategenre();
                    }
                });

                genrechipgrp.addView(chip, i);
                validategenre();
            }
        }
    }

    /**
     * Taking the contents into an object and calling the a method to add or edit the movie
     */
    private void editAddToDb() {
        validategenre();
        validatesummary();
        validatetitle();
        validatecast();
        if(validGenre&&validTitle&&validSummary&&validCast) {
            String s1 = textInputEditTexttitle.getText().toString();
            String s2 = textInputEditTextsummary.getText().toString();
            List<CastList> cstList = new ArrayList<>();
            String CastString = textInputEditTextcast.getText().toString();
            ArrayList<String> CastsList = new ArrayList<>(Arrays.asList(CastString.split(",")));
            for (String x : CastsList) {
                CastList cstobj = new CastList(x);
                cstList.add(cstobj);
            }
            List<Integer> genList = new ArrayList<>();

            for (int i = 0; i < genrechipgrp.getChildCount(); i++) {
                Chip chip = (Chip) genrechipgrp.getChildAt(i);
                String id = chip.getTag().toString();
                Integer IdInt = Integer.valueOf(id);
                genList.add(IdInt);
            }
            admobj = new AddMovie(cstList, genList, s2, s1);
            if (activityName.equals("homeact"))
                postToDB(admobj);
            if (activityName.equals("movieDetail"))
                editToDB(admobj);
        }
        else {
            progressDialog.dismiss();
            Toast.makeText(AddEditMovie.this, "Please enter the credentials right!!!!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Post or edit the movie on Button press
     */
    public void savemoviee(View view) {
        is_internet_connected = mCheckInternetStatus.isInternetConnected();
        if(!is_internet_connected)
        {
            showCustomDialog();
        }
        else {
            progressDialog.show();
            editAddToDb();
        }
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
     * Validations
     */
    /**
     * Checking for valid genres
     */
    private void validategenre() {

        if(genrechipgrp.getChildCount()>0)
        {
            genreerrorview.setVisibility(View.INVISIBLE);
            genrechipgrp.setVisibility(View.VISIBLE);
            validGenre=true;
        }
        else {
            genreerrorview.setVisibility(View.VISIBLE);
            genrechipgrp.setVisibility(View.INVISIBLE);
            validGenre=false;
        }

    }

    /**
     * Checking for valid Cast name
     */
    private void validatecast() {
        textInputEditTextcast.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String tmp=s.toString().trim();
                validCast=true;

                if(tmp.length()==0) {
                    textInputLayoutcast.setErrorEnabled(true);
                    textInputLayoutcast.setError("Movie cast cannot be empty");
                    validCast=false;
                }
                else {
                    List<String> castList = Arrays.asList(tmp.split(","));

                    for (int i = 0; i < castList.size(); i++) {
                        String castname = castList.get(i);
                        if (castname.length() > 50) {
                            validCast=false;
                            textInputLayoutcast.setErrorEnabled(true);
                            textInputLayoutcast.setError("Cast name should not be more than 50 characters");
                        }
                    }
                    if (validCast) {
                        textInputLayoutcast.setErrorEnabled(false);
                        textInputLayoutcast.setError("");
                    }
                }
            }
        });
    }

    /**
     * Checking for valid Summary
     */
    private void validatesummary() {
        textInputEditTextsummary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validSummary=true;
                if(s.toString().trim().length()==0)
                {
                    textInputLayoutsummary.setErrorEnabled(true);
                    textInputLayoutsummary.setError("Movie title cannot be empty");
                    validSummary=false;
                }
                else if(s.toString().trim().length()>500)
                {
                    textInputLayoutsummary.setErrorEnabled(true);
                    textInputLayoutsummary.setError("Try to keep summary crisp. Can't be more than 500 characters long.");
                    validSummary=false;
                }
                else
                {
                    validSummary=true;
                    textInputLayoutsummary.setErrorEnabled(false);
                    textInputLayoutsummary.setError("");
                }
            }
        });
    }

    /**
     * Checking for valid Title
     */
    private void validatetitle() {
        textInputEditTexttitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().length()==0)
                {
                    textInputLayouttitle.setErrorEnabled(true);
                    textInputLayouttitle.setError("Movie title cannot be empty");
                    validTitle=false;
                }
                else if(s.toString().trim().length()>100)
                {
                    textInputLayouttitle.setErrorEnabled(true);
                    textInputLayouttitle.setError("Movie title should not be more than 100 characters");
                    validTitle=false;
                }
                else
                {
                    textInputLayouttitle.setErrorEnabled(false);
                    textInputLayouttitle.setError("");
                    validTitle=true;
                }
            }
        });
    }

    /**
     * API CALLS
     */
    /**
     * initgenrecomponent ( It is used for taking the genres from API and filling it into the spinner)
     */
    private void initgenrecomponent() {
        Call<List<GenereData>> call = ApiController.getInstance(url.getBaseUrlJava()).getapi().allGeners();
        call.enqueue(new Callback<List<GenereData>>() {
            @Override
            public void onResponse(Call<List<GenereData>> call, Response<List<GenereData>> response) {
                if(response.code() != 200 || !(response.isSuccessful()))
                {
                    Intent i = new Intent(AddEditMovie.this, ErrorHandled.class);
                    i.putExtra("activity", "addm");
                    i.putExtra("cd", String.valueOf(response.code()));
                    startActivity(i);
                }
                else {
                    List<GenereData> obj = response.body();
                    genreArr = new ArrayList<>();
                    genreArr.add("All");
                    for (GenereData name : obj) {
                        String genreName=StringUtils.capitalize(name.getName());
                        genreArr.add(genreName);
                        genreId.put(genreName,name.getId());
                    }
                    receiveDataFromDetails();
                    spinner=activityAddEditMovieBinding.addEditGenreSpinner;
                    spinneradapter= new ArrayAdapter(AddEditMovie.this, android.R.layout.simple_list_item_1,genreArr);
                    spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    progressDialog.dismiss();
                    spinner.setAdapter(spinneradapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selected_name=spinner.getSelectedItem().toString();
                            String selected_Id=genreId.get(selected_name);
                            addChip(selected_name,selected_Id);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<GenereData>> call, Throwable t) {
                Toast.makeText(AddEditMovie.this, "Somethig not good", Toast.LENGTH_LONG).show();
            }
        });


    }

    /**
     * Api calling for edit the movie details
     */
    private void editToDB(AddMovie admobj) {
        Call<Editmovie> call = ApiController.getInstance(url.getBaseUrlJava()).getapi().editMoviee(Integer.valueOf(movie_id),admobj);
        call.enqueue(new Callback<Editmovie>() {
            @Override
            public void onResponse(Call<Editmovie> call, Response<Editmovie> response) {
                if(response.code()==202||(response.isSuccessful())) {
                    Toast.makeText(AddEditMovie.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(AddEditMovie.this,Movie_Details.class);
                    intent.putExtra("titlee",textInputEditTexttitle.getText().toString());
                    intent.putExtra("id",movie_id);
                    finish();
                    progressDialog.dismiss();
                }
                else
                {
                    Intent i = new Intent(AddEditMovie.this, ErrorHandled.class);
                    i.putExtra("activity", "addm");
                    i.putExtra("cd", String.valueOf(response.code()));
                    startActivity(i);
//                    finish();
                }
            }
            @Override
            public void onFailure(Call<Editmovie> call, Throwable t) {
                Toast.makeText(AddEditMovie.this, "Updated Successfully", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(AddEditMovie.this,Movie_Details.class);
                intent.putExtra("titlee",textInputEditTexttitle.getText().toString());
                intent.putExtra("id",movie_id);
                finish();
                progressDialog.dismiss();
            }
        });
    }

    /**
     * Api calling for Adding a new movie
     */
    private void postToDB(AddMovie admobj) {
        Call<Integer> call = ApiController.getInstance(url.getBaseUrlJava()).getapi().addMoviee(admobj);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.code()==201||(response.isSuccessful())) {
                    progressDialog.dismiss();
                    Toast.makeText(AddEditMovie.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddEditMovie.this,HomeActivity.class));
                    finish();
                }
                else {
                    Intent i = new Intent(AddEditMovie.this, ErrorHandled.class);
                    i.putExtra("activity", "addm");
                    i.putExtra("cd", String.valueOf(response.code()));
                    startActivity(i);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(AddEditMovie.this, "Something went wrong", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

}