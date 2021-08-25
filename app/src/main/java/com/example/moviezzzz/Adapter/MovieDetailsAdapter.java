package com.example.moviezzzz.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulhakeem.seemoretextview.SeeMoreTextView;
import com.example.moviezzzz.AddEditMovie;
import com.example.moviezzzz.Api.ApiController;
import com.example.moviezzzz.Api.Urls;
import com.example.moviezzzz.ErrorHandled;
import com.example.moviezzzz.Model.CastList;
import com.example.moviezzzz.Model.GenereData;
import com.example.moviezzzz.Model.Moviedetail;
import com.example.moviezzzz.Model.ReviewData;
import com.example.moviezzzz.R;
import com.example.moviezzzz.Utils.ScrollTextUtil;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = "moviedetailsadapter";
    private Context context,generalcontext,reviewcontext;

    private static final int GENERAL = 0;
    private static final int REVIEW = 1;
    private static final int LOADING = 2;

    private  ScrollTextUtil scrollTextUtil;
    private Moviedetail moviedetail;
    private List<ReviewData> reviewDataList;
    private String movie_id;
    private ProgressDialog progressDialog;
    private boolean isLoadingAdded = false;
    private Urls apiBaseUrl;
    private ChipGroup genrechipgrp;

    /**
     * Parameterized Constructor that sets context , list of reviews , Movie ID and General Movie Detail Object
     */
    public MovieDetailsAdapter(String Idd, List<ReviewData> reviewDataList, Moviedetail moviedetail, Context context) {
        this.movie_id=Idd;
        this.moviedetail=moviedetail;
        this.reviewDataList=reviewDataList;
        this.context=context;
        progressDialog=new ProgressDialog(context);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.dismiss();
        apiBaseUrl=new Urls();
    }

    /**
     * Default OnCreateViewHolder ( Used for inflating the Layouts )
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case REVIEW:
                View viewItem = inflater.inflate(R.layout.movie_details_review_item_layout, parent, false);
                viewHolder = new MDreview(viewItem);
                break;
            case GENERAL:
                View viewGeneral = inflater.inflate(R.layout.movie_details_general_item_layout, parent, false);
                viewHolder = new MDgeneral(viewGeneral);
                break;
            case LOADING:
                View viewloading = inflater.inflate(R.layout.item_loading, parent, false);
                viewHolder = new MDloading(viewloading);
                break;
        }
        return viewHolder;
    }

    /**
     * Default onBindViewHolder ( Used for Binding the views of a particular Layout )
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {

            case GENERAL:
                    final MDgeneral mDgeneral = (MDgeneral) holder;
                    if(mDgeneral.isViewHandled)
                        return;
                    mDgeneral.setViewHandled(true);

                    generalcontext = holder.itemView.getContext();

                    mDgeneral.summary.setText(moviedetail.summary.toString());
                    mDgeneral.movietitle.setText(WordUtils.capitalize(moviedetail.title.toString()));
                    String stringdouble = Double.toString(moviedetail.rating);
                    mDgeneral.Noofusers.setText("( " + (Integer.toString(moviedetail.count)) + " Users )");
                    mDgeneral.movieratingtext.setText(stringdouble);
                    mDgeneral.movieratingbar.setRating((float) moviedetail.rating);
                    String[] genrelist = new String[moviedetail.getGenres().size()];
                    int j = 0;

                    for (GenereData genre : moviedetail.genres) {
                        Chip chip = new Chip(context);
                        chip.setText(StringUtils.capitalize(genre.getName()));
                        chip.setChipBackgroundColorResource(R.color.yellow_600);
                        genrelist[j++] = genre.getName();
                        genrechipgrp.addView(chip);
                    }

                    for (CastList cst : moviedetail.cast) {
                        Chip chip = new Chip(context);
                        chip.setText(cst.getName());
                        chip.setChipBackgroundColorResource(R.color.yellow_600);
                        mDgeneral.castchipgrp.addView(chip);
                    }

                    /**
                     * On clicking Edit Movie Button in Movie Details Page
                     */
                    mDgeneral.editmovie.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Chip chip = new Chip(context);
                            String[] genrestrAr = new String[genrechipgrp.getChildCount()];
                            for (int i = 0; i < genrechipgrp.getChildCount(); i++) {
                                chip = (Chip) genrechipgrp.getChildAt(i);
                                genrestrAr[i] = StringUtils.capitalize(chip.getText().toString());
                            }
                            Intent i = new Intent(generalcontext, AddEditMovie.class);
                            i.putExtra("title", moviedetail.title);
                            i.putExtra("summary", moviedetail.summary);
                            i.putExtra("activity", "movieDetail");
                            i.putExtra("id", movie_id);
                            i.putExtra("genre", genrestrAr);
                            String[] strAr = new String[moviedetail.getCast().size()];
                            int j = 0;
                            for (CastList x : moviedetail.getCast())
                                strAr[j++] = x.getName();
                            i.putExtra("casts", strAr);
                            generalcontext.startActivity(i);
//                            ((Activity)generalcontext).finish();
                        }
                    });

                    /**
                     * On clicking Rate Movie Button in Movie Details Page
                     */
                    mDgeneral.rate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showCustomDialogRate(generalcontext);
                        }
                    });

                    /**
                     * On clicking Review Movie Button in Movie Details Page
                     */
                    mDgeneral.review.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showCustomDialogReview(generalcontext);
                        }
                    });

                break;

            case REVIEW:

                final MDreview mDreview = (MDreview) holder;
                reviewcontext=holder.itemView.getContext();

                SeeMoreTextView reviewtextView =(SeeMoreTextView) mDreview.reviewtext;
                TextView reviewtextdate = mDreview.reviewdate;

                reviewtextView.setTextMaxLength(100);
                reviewtextView.setSeeMoreText("read more","read less");
                reviewtextView.setSeeMoreTextColor(R.color.blue_600);

//                Log.d(TAG, "onBindViewHolder: "+reviewDataList.size()+"\t"+position+"\t"+getItemCount());

                reviewtextView.setContent(reviewDataList.get(position-1).getReview());
                reviewtextdate.setText(reviewDataList.get(position-1).getCreatedAt());

                break;

            case LOADING:

                MDloading loadingVH = (MDloading) holder;
                loadingVH.progressBar.setVisibility(View.VISIBLE);

                break;
        }
    }

    /**
     * getItemCount ( It returns the total number of items in the data set held by the adapter )
     */
    @Override
    public int getItemCount() {
        return 1+reviewDataList.size();
    }

    /**
     * getItemViewType ( It determines what type of view we should use at a particular position )
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return GENERAL;
        } else {
            return (position == reviewDataList.size() && isLoadingAdded) ? LOADING : REVIEW;
        }
    }

    /**
     * showCustomDialogReview ( Dialog Screen for Adding review of a movie )
     */
    private void showCustomDialogReview(Context generalcontext) {
        final Dialog dialog = new Dialog(generalcontext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_review);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText et_post = (EditText) dialog.findViewById(R.id.et_post);
        ((AppCompatButton) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((AppCompatButton) dialog.findViewById(R.id.bt_review_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String review = et_post.getText().toString().trim();
                if (review.isEmpty()) {
                    Toast.makeText(generalcontext, "Please enter the review", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.show();
                    postReview(review);//Function for api calling
                    dialog.dismiss();
                    Toast.makeText(generalcontext, "Submitted", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    /**
     * showCustomDialogRate ( Dialog Screen for Rating a Movie )
     */
    private void showCustomDialogRate(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_add_rating);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        final AppCompatRatingBar rating_bar = (AppCompatRatingBar) dialog.findViewById(R.id.rating_bar);

        ((AppCompatButton) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((AppCompatButton) dialog.findViewById(R.id.bt_rate_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                postRate(rating_bar.getRating());//Function for api calling
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    /**
     * API CALLING METHODS
     */
    /**
     * POST API CALLING for posting review of a movie
     */
    private void postReview(String review) {
        Call<Void> call = ApiController.getInstance(apiBaseUrl.getBaseUrlJava()).getapi().addRevw(Integer.valueOf(movie_id),review);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==202||(response.isSuccessful())) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Submitted", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i = new Intent(context, ErrorHandled.class);
                    i.putExtra("activity", "Mvd");
                    i.putExtra("cd", String.valueOf(response.code()));
                    i.putExtra("titlee",moviedetail.title);
                    i.putExtra("id",movie_id);
                    context.startActivity(i);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Sorry, something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * POST API CALLING for posting rating of a movie
     */
    private void postRate(float rating) {
        Double dbl;
        dbl = (Double) ( rating / 1.0 );
        Call<Void> call = ApiController.getInstance(apiBaseUrl.getBaseUrlJava()).getapi().addRating(Integer.valueOf(movie_id),dbl);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==202||(response.isSuccessful())){
                    progressDialog.dismiss();
                    Toast.makeText(context, "Submitted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(context, ErrorHandled.class);
                    i.putExtra("activity", "Mvd");
                    i.putExtra("cd", String.valueOf(response.code()));
                    i.putExtra("titlee",moviedetail.title);
                    i.putExtra("id",movie_id);
                    context.startActivity(i);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * PAGINATION HELPER FUNCTIONS
     */
    /**
     * add ( It is used to append a review item at the end the list )
     */
    public void add(ReviewData r) {
        reviewDataList.add(r);
        notifyItemInserted(reviewDataList.size() -1);
    }

    /**
     * addAll ( It is used to append updated review list ( New Page List ) at the end the given list i.e.reviewDataList )
     */
    public void addAll(List<ReviewData> updatedreviewlist) {
        for (ReviewData result : updatedreviewlist) {
            add(result);
        }
    }

    /**
     * addLoadingFooter ( It is used to add loading footer at the end of the recyclerview )
     */
    public void addLoadingFooter() {
        isLoadingAdded = true;
        reviewDataList.add(new ReviewData());
        notifyItemInserted(getItemCount()-1);
    }

    /**
     * removeLoadingFooter ( It is used to remove loading footer from the end of the recyclerview )
     */
    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = reviewDataList.size() - 1;
        reviewDataList.remove(position);
        notifyDataSetChanged();
//        notifyItemRemoved(position);
    }

    /**
     *View Holders
     *_________________________________________________________________________________________________
    */
    /**
     * General Movie Details ViewHolder
     */
    protected class MDgeneral extends RecyclerView.ViewHolder {
        private Button movietitle,rate,review;
        private ImageButton editmovie;
        private ChipGroup castchipgrp;
        private RatingBar movieratingbar;
        private TextView movieratingtext,Noofusers,summary;
        private boolean isViewHandled=false;

        public boolean isViewHandled() {
            return isViewHandled;
        }

        public void setViewHandled(boolean viewHandled) {
            isViewHandled = viewHandled;
        }

        public MDgeneral(View itemView) {
            super(itemView);

            movietitle=itemView.findViewById(R.id.mdtitle);
            rate=itemView.findViewById(R.id.ratethis);
            review=itemView.findViewById(R.id.mdreview);

            editmovie=itemView.findViewById(R.id.editmoviee);

            genrechipgrp=itemView.findViewById(R.id.genrechipgrp);
            castchipgrp=itemView.findViewById(R.id.castchipgrp);

            movieratingbar=itemView.findViewById(R.id.mdratingbar);

            movieratingtext=itemView.findViewById(R.id.mdrating);
            Noofusers=itemView.findViewById(R.id.noofusers);
            summary=itemView.findViewById(R.id.mdsumtextview);

            scrollTextUtil=new ScrollTextUtil();
            scrollTextUtil.enableScroll(summary);

        }
    }

    /**
     *  Movie Review ViewHolder
     */
    protected class MDreview extends RecyclerView.ViewHolder {
        private TextView reviewtext,reviewdate;

        public MDreview(View itemView) {
            super(itemView);
            reviewtext=itemView.findViewById(R.id.mdreviewuser);
            reviewdate=itemView.findViewById(R.id.reviewdate);
        }
    }

    /**
     * Loading ViewHolder
     */
    protected class MDloading extends RecyclerView.ViewHolder{
        private ProgressBar progressBar;
        public MDloading(@NonNull View itemView) {
            super(itemView);
            progressBar=itemView.findViewById(R.id.progressbar);
        }
    }

}
