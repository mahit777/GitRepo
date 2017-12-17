package myprivate.githubrepo.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import myprivate.githubrepo.Adapters.RecycleCustomAdapter;
import myprivate.githubrepo.Model.User;
import myprivate.githubrepo.R;
import myprivate.githubrepo.RetrofitUtils.GitHubClient;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RepoDetails extends AppCompatActivity implements View.OnClickListener {
    String repoFullName,projectLink;
    public final static String TAG="Contributors";
    RecyclerView recyclerView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar=(ProgressBar)findViewById(R.id.repo_progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.INVISIBLE);
        Bundle extras=getIntent().getExtras();
        repoFullName=extras.getString(getString(R.string.repo_contributors));
        String repoName=extras.getString(getString(R.string.repo_name));
        String repoDescription=extras.getString(getString(R.string.repo_description));
        TextView description=(TextView)findViewById(R.id.repo_description);
        description.setText(repoDescription);
        projectLink=extras.getString(getString(R.string.html_url));
        TextView projectUrl=(TextView)findViewById(R.id.repo_html_url);
        projectUrl.setTextColor(Color.BLUE);
        projectUrl.setText(Html.fromHtml(projectLink));
        projectUrl.setOnClickListener(this);
      final CollapsingToolbarLayout collapsingToolbarLayout=(CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(repoName);
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
        recyclerView=(RecyclerView) findViewById(R.id.contributors_recycler);
//        BitmapDrawable bitmapDrawable=new BitmapDrawable(getResources(),bitmap);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.repo_html_url:{
                Intent intent=new Intent(RepoDetails.this, RepoWebView.class);
                intent.putExtra(getString(R.string.html_url),projectLink);
                startActivity(intent);
            }
        }
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

        Call<List<User>> call=client.contributorsList(repoFullName);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()) {
                    if (response.body() != null && response.body().size() > 0) {
                        RecycleCustomAdapter adapter = new RecycleCustomAdapter(RepoDetails.this, new ArrayList<Object>(response.body()), TAG);
                        recyclerView.setLayoutManager(new GridLayoutManager(RepoDetails.this, 3));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);
                    } else
                        Snackbar.make(progressBar, "No contributorsfor this repo", Snackbar.LENGTH_LONG).show();
                }else {

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
            public void onFailure(Call<List<User>> call, Throwable t) {
                Snackbar.make(progressBar, t.getLocalizedMessage()+" Check Internet Connection", Snackbar.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);            }
        });


    }


}
