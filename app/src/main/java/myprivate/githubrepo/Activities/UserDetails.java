package myprivate.githubrepo.Activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import myprivate.githubrepo.Adapters.RecycleCustomAdapter;
import myprivate.githubrepo.Model.Repository;
import myprivate.githubrepo.Model.User;
import myprivate.githubrepo.R;
import myprivate.githubrepo.RetrofitUtils.GitHubClient;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserDetails extends AppCompatActivity {
    String userLogin;
    public final static String TAG="User Details";
    ProgressBar progressBar;

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras=getIntent().getExtras();
        userLogin=extras.getString(getString(R.string.get_user_url));
        final CollapsingToolbarLayout collapsingToolbarLayout=(CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(userLogin);
        progressBar=(ProgressBar)findViewById(R.id.user_progressbar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.INVISIBLE);
        String bitmapUrl= extras.getString(getString(R.string.repo_image));
        Picasso.with(this).load(bitmapUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                BitmapDrawable bitmapDrawable=new BitmapDrawable(getResources(),bitmap);
                collapsingToolbarLayout.setBackground(bitmapDrawable);

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });


        recyclerView=(RecyclerView)findViewById(R.id.user_repo_list);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        String API_BASE_URL = "https://api.github.com/";

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        );

        Retrofit retrofit =
                builder
                        .client(
                                httpClient.build()
                        )
                        .build();

        GitHubClient client =  retrofit.create(GitHubClient.class);

        Call<List<Repository>> call=client.userRepoList(userLogin);

        call.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                if(response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(UserDetails.this));
                        recyclerView.setHasFixedSize(true);
                        RecycleCustomAdapter adapter = new RecycleCustomAdapter(UserDetails.this, new ArrayList<Object>(response.body()), TAG);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Snackbar.make(progressBar, "No Repositories for this user", Snackbar.LENGTH_LONG).show();

                    }
                }
                else {

                    try{
                        Snackbar.make(progressBar, response.errorBody().string(), Snackbar.LENGTH_LONG).show();


                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }

                }

                progressBar.setVisibility(View.INVISIBLE);


            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                Snackbar.make(progressBar, t.getLocalizedMessage()+" Check Internet Connection", Snackbar.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });


    }
}
