package myprivate.githubrepo.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import myprivate.githubrepo.Adapters.RecycleCustomAdapter;
import myprivate.githubrepo.Model.Repository;
import myprivate.githubrepo.Model.SearchResult;
import myprivate.githubrepo.R;
import myprivate.githubrepo.RetrofitUtils.GitHubClient;
import myprivate.githubrepo.SearchUtils.SuggestionProvider;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    public final static String TAG="Search Repos";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
       recyclerView =(RecyclerView)findViewById(R.id.home_repo_list);
       progressBar =(ProgressBar)findViewById(R.id.indeterminateBar);
       progressBar.setIndeterminate(true);
       progressBar.setVisibility(View.INVISIBLE);

//       progressBar.getProgressBackgroundTintMode()

    }

    private void doMySearch(final String query) {


        progressBar.setVisibility(View.VISIBLE);
        HashMap<String,String> parameterMap=new HashMap<>();
        parameterMap.put("q",query);
        String API_BASE_URL = "https://api.github.com/";
        SharedPreferences sharedPreferences=getSharedPreferences(getString(R.string.queryString),MODE_PRIVATE);
        sharedPreferences.edit().putString(getString(R.string.home_search),query).apply();
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

        Call<SearchResult> call=client.seaerchRepo(parameterMap);
        call.enqueue(new Callback<SearchResult>() {

            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if(response.isSuccessful()) {
                    if (response.body().getTotal_count() > 0) {
                        ArrayList<Repository> sortedList = sort(response.body().getItems());
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Home.this);
                        recyclerView.setLayoutManager(layoutManager);

                        RecycleCustomAdapter adapter = new RecycleCustomAdapter(Home.this, new ArrayList<Object>(sortedList), TAG);

                        recyclerView.setAdapter(adapter);
                        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(Home.this, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
                        suggestions.saveRecentQuery(query, null);

                    } else {
                        Snackbar.make(progressBar, "No repos found with name " + query, Snackbar.LENGTH_LONG).show();
                    }
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
            public void onFailure(Call<SearchResult> call, Throwable t) {
//                Snackbar.make(progressBar, t.getMessage(), Snackbar.LENGTH_LONG).show();
                Snackbar.make(progressBar, t.getLocalizedMessage()+" Check Internet Connection", Snackbar.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
//        call.execute();




    }
    public ArrayList<Repository> sort(ArrayList<Repository> repositoryArrayList){
        Collections.sort(repositoryArrayList, new Comparator<Repository>() {
            @Override public int compare(Repository r1, Repository r2) {
                return Integer.compare(r1.getWatchers(),r2.getWatchers());
            }
        });
        if(repositoryArrayList.size()>10){
         repositoryArrayList=new ArrayList<>(repositoryArrayList.subList(repositoryArrayList.size()-10,repositoryArrayList.size()-1));
        }
         Collections.reverse(repositoryArrayList);

         return repositoryArrayList;
    }

    @Override
    protected void onStart() {
        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            doMySearch(query);
        }
        else{
            getQuery();
        }

        super.onStart();
    }

    private void getQuery() {
        SharedPreferences sharedPreferences=getSharedPreferences(getString(R.string.queryString),MODE_PRIVATE);
        String userInput=sharedPreferences.getString(getString(R.string.home_search),"");
        if(userInput.isEmpty()){
            Snackbar.make(recyclerView,"Press the search button on the top to search repositories",Snackbar.LENGTH_LONG).show();
        }else{
            doMySearch(userInput);
        }
    }

    @Override
    protected void onStop() {


        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.home_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.home_search_btn);

//        menuItem.
        SearchManager searchManager =(SearchManager) getSystemService(Context.SEARCH_SERVICE);
       final  SearchView searchView =(SearchView) menu.findItem(R.id.home_search_btn).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
         menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                searchView.setIconifiedByDefault(true);
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }
}
