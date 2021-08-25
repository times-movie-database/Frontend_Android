package com.example.moviezzzz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviezzzz.Model.MovieBrief;
import com.example.moviezzzz.Model.ReviewData;
import com.example.moviezzzz.Movie_Details;
import com.example.moviezzzz.R;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context,moviecontext;

    private static final int MOVIE = 0;
    private static final int LOADING = 1;

    private List<MovieBrief> movieBriefList;
    private boolean isLoadingAdded = false;

    /**
     * Parameterized Constructor that sets context and list of movies i.e. MovieBriefList
     */
    public MovieAdapter(Context applicationContext, List<MovieBrief> obj) {
        this.context=applicationContext;
        this.movieBriefList=obj;
    }

    /**
     * Default OnCreateViewHolder ( Used for inflating the Layouts i.e. movie_item Layout and item_loading layout )
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case MOVIE:
                View viewMovieItem = inflater.inflate(R.layout.movie_item, parent, false);
                viewHolder = new SAMovieViewHolder(viewMovieItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_loading, parent, false);
                viewHolder = new SALoadingViewholder(viewLoading);
                break;
        }
        return viewHolder;
    }

    /**
     * Default onBindViewHolder ( Used for Binding the views of a particular Layout )
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {

            case MOVIE:
                final SAMovieViewHolder saMovieViewHolder = (SAMovieViewHolder) holder;
                moviecontext=holder.itemView.getContext();

                saMovieViewHolder.title.setText( WordUtils.capitalize(movieBriefList.get(position).getTitle()) );
                saMovieViewHolder.ratingBar.setRating( Float.parseFloat(movieBriefList.get(position).getRating()) );
                saMovieViewHolder.rating.setText( movieBriefList.get(position).getRating()+"/5" );
                saMovieViewHolder.title.setTag( movieBriefList.get(position).getId() );

                Log.d("Movie1123", "onBindViewHolder: "+movieBriefList.size()+"\t"+position+"\t"+getItemCount());

                saMovieViewHolder.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(moviecontext, Movie_Details.class);
                        i.putExtra("titlee", saMovieViewHolder.title.getText().toString());
                        i.putExtra("id", v.getTag().toString());
                        moviecontext.startActivity(i);
                    }
                });
                break;

            case LOADING:
                SALoadingViewholder loadingVH = (SALoadingViewholder) holder;
                loadingVH.progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * getItemCount ( It returns the total number of items in the data set held by the adapter )
     */
    @Override
    public int getItemCount() {
        return movieBriefList.size();
    }

    /**
     * getItemViewType ( It determines what type of view we should use at a particular position )
     */
    @Override
    public int getItemViewType(int position) {

        return (position == movieBriefList.size()-1 && isLoadingAdded) ? LOADING : MOVIE;

    }

    /**
     * PAGINATION HELPER FUNCTIONS
     */

    /**
     * add ( It is used to append a movie item at the end the list )
     */
    public void add(MovieBrief r) {
        movieBriefList.add(r);
        notifyItemInserted(movieBriefList.size() -1);
    }

    /**
     * addAll ( It is used to append updated list ( New Page List ) at the end the given list i.e.movieBriefList )
     */
    public void addAll(List<MovieBrief> updatedmovielist) {
        for (MovieBrief result : updatedmovielist) {
            add(result);
        }
    }

    /**
     * addLoadingFooter ( It is used to add loading footer at the end of the recyclerview )
     */
    public void addLoadingFooter() {
        isLoadingAdded = true;
        movieBriefList.add(new MovieBrief());
        notifyItemInserted(getItemCount()-1);
    }

    /**
     * removeLoadingFooter ( It is used to remove loading footer from the end of the recyclerview )
     */
    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = movieBriefList.size() - 1;
        movieBriefList.remove(position);
        notifyDataSetChanged();
    }

    /**
     * VIEWHOLDERS
     */
    /**
     * SAMovieViewHolder ( Search Activity Movie View Holder )
     */
    class SAMovieViewHolder extends RecyclerView.ViewHolder {
        TextView rating,title;
        ImageView imageView;
        LinearLayout linearLayout;
        RatingBar ratingBar;

        SAMovieViewHolder(View itemView) {
            super(itemView);

            rating = itemView.findViewById(R.id.rating);
            title = itemView.findViewById(R.id.mtitle);
            ratingBar = itemView.findViewById(R.id.ratingbar);
        }
    }

    /**
     * SALoadingViewholder ( Search Activity Loading View Holder )
     */
    class SALoadingViewholder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        public SALoadingViewholder(@NonNull View itemView) {
            super(itemView);
            progressBar=itemView.findViewById(R.id.progressbar);
        }
    }

}