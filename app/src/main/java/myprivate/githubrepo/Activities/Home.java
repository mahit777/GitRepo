package myprivate.githubrepo.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import myprivate.githubrepo.Adapters.LicenceAdapter;
import myprivate.githubrepo.Adapters.RecycleCustomAdapter;
import myprivate.githubrepo.Model.License;
import myprivate.githubrepo.Model.Repository;
import myprivate.githubrepo.Model.SearchResult;
import myprivate.githubrepo.R;
import myprivate.githubrepo.RetrofitUtils.GitHubClient;
import myprivate.githubrepo.SearchUtils.SuggestionProvider;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    public final static String TAG = "Search Repos";
    String query="";
    String generatedQuerry="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = (RecyclerView) findViewById(R.id.home_repo_list);
        progressBar = (ProgressBar) findViewById(R.id.indeterminateBar);
        progressBar.setIndeterminate(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        progressBar.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(this);

//       progressBar.getProgressBackgroundTintMode()

    }

    private void doMySearch(final String query,String filters) {


        progressBar.setVisibility(View.VISIBLE);
//        HashMap<String, String> parameterMap = new HashMap<>();
//        parameterMap.put("q", query);
        String API_BASE_URL = "https://api.github.com/";

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                Request request = chain.request();
                String url = request.url().toString();
                url=url.replace("%3D", "=");
                url=url.replace("%3E", ">");
                url=url.replace("%3C", "<");
                url=url.replace("-%0A", "-");
                Request newRequest = new Request.Builder()
                        .url(url)
                        .build();

                return chain.proceed(newRequest);

            }
        });


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

        GitHubClient client = retrofit.create(GitHubClient.class);
        ArrayList<String> finList=new ArrayList<String>();
        finList.add(query+filters);
        Call<SearchResult> call = client.seaerchRepo(finList);
        call.enqueue(new Callback<SearchResult>() {

            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().getTotal_count() > 0) {
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.queryString), MODE_PRIVATE);
                        sharedPreferences.edit().putString(getString(R.string.home_search), query).apply();
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
                } else {

                    try {
                        Snackbar.make(progressBar, response.errorBody().string(), Snackbar.LENGTH_LONG).show();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
//                Snackbar.make(progressBar, t.getMessage(), Snackbar.LENGTH_LONG).show();
                Snackbar.make(progressBar, t.getLocalizedMessage() + " Check Internet Connection", Snackbar.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
//        call.execute();


    }

    public ArrayList<Repository> sort(ArrayList<Repository> repositoryArrayList) {
        Collections.sort(repositoryArrayList, new Comparator<Repository>() {
            @Override
            public int compare(Repository r1, Repository r2) {
                return Integer.compare(r1.getWatchers(), r2.getWatchers());
            }
        });
        if (repositoryArrayList.size() > 10) {
            repositoryArrayList = new ArrayList<>(repositoryArrayList.subList(repositoryArrayList.size() - 11, repositoryArrayList.size() - 1));
        }
        Collections.reverse(repositoryArrayList);

        return repositoryArrayList;
    }

    @Override
    protected void onStart() {
        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

            doMySearch(query,"");
        } else {
            getQuery();
        }

        super.onStart();
    }

    private void getQuery() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.queryString), MODE_PRIVATE);
        String userInput = sharedPreferences.getString(getString(R.string.home_search), "");
        if (userInput.isEmpty()) {
            Snackbar.make(recyclerView, "Press the search button on the top to search repositories", Snackbar.LENGTH_LONG).show();
        } else {
            query=userInput;
            doMySearch(userInput,"");
        }
    }

    @Override
    protected void onStop() {


        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.home_search_btn);

//        menuItem.
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.home_search_btn).getActionView();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButton2: {
                if(query.isEmpty()){
                    Toast.makeText(view.getContext(),"please search for repositories before you can filter",Toast.LENGTH_SHORT).show();

                }else {
                    callFilter();
                }
                break;
            }
            case R.id.filter_created_starting_date:{
                callendarDialog((TextView) view,view.getContext(),true);
                break;
            }
            case R.id.filter_created_to_date:{
                callendarDialog((TextView) view,view.getContext(),false);
                break;
            }case R.id.filter_pushed_starting_date:{
                callendarDialog((TextView) view,view.getContext(),true);
                break;
            }case R.id.filter_pushed_to_date:{
                callendarDialog((TextView) view,view.getContext(),false);
                break;
            }

        }

    }

    private void callendarDialog(final TextView view, final Context context , final Boolean isFrom) {

        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog=new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Calendar c = Calendar.getInstance();
                c.set(i,i1, i2);
                DateTime dateTime=new DateTime(c.getTime());
                if(isFrom&&dateTime.isAfter(DateTime.now())){
                    Toast.makeText(context,"please select a day before today",Toast.LENGTH_SHORT).show();

                }else {
                    String month=(i1+1)+"";
                    if(month.length()!=2){
                        month="0"+month;
                    }
                    String day=i2+"";
                    if(day.length()!=2){
                        day="0"+day;
                    }

                    view.setText(i + "-" + month + "-" +day);
                }
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));

        datePickerDialog.show();

    }

    private void callFilter() {
//        final String genratedQuery=query;


        generatedQuerry="";
        final Dialog filterDialog = new Dialog(Home.this);
        filterDialog.setContentView(R.layout.dialog_filter);
        filterDialog.getWindow().setLayout(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
        final TextView createdFromDate, createdToDate, pushedFromDate, pushedToDate;
        final EditText forkCount, userName, language;
       final CheckBox displayForks,inName, inDescription, inReadme, archived;
        final CheckBox onlyForks;
        final Button filter;
        final Spinner liciense;
        createdFromDate = (TextView) filterDialog.findViewById(R.id.filter_created_starting_date);
        createdToDate = (TextView) filterDialog.findViewById(R.id.filter_created_to_date);
        pushedFromDate = (TextView) filterDialog.findViewById(R.id.filter_pushed_starting_date);
        pushedToDate = (TextView) filterDialog.findViewById(R.id.filter_pushed_to_date);
        forkCount = (EditText) filterDialog.findViewById(R.id.filter_forks_count);
        userName = (EditText) filterDialog.findViewById(R.id.filter_user);
        language = (EditText) filterDialog.findViewById(R.id.filter_language);
        displayForks = (CheckBox) filterDialog.findViewById(R.id.filter_forks_displayForks);
        onlyForks = (CheckBox) filterDialog.findViewById(R.id.filter_forks_only);
        inName = (CheckBox) filterDialog.findViewById(R.id.filter_in_name);
        inDescription = (CheckBox) filterDialog.findViewById(R.id.filter_in_description);
        inReadme = (CheckBox) filterDialog.findViewById(R.id.filter_in_readme);
        archived = (CheckBox) filterDialog.findViewById(R.id.filter_archived_true);
        filter = (Button) filterDialog.findViewById(R.id.filter_apply);
        liciense = (Spinner) filterDialog.findViewById(R.id.filter_license);
        getLicense(liciense);
        createdFromDate.setOnClickListener(this);
        createdToDate.setOnClickListener(this);
        pushedFromDate.setOnClickListener(this);
        pushedToDate.setOnClickListener(this);

        displayForks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               onlyForks.setEnabled(b);
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                     if(!createdFromDate.getText().toString().trim().equals(getString(R.string.date_format))){
                         generatedQuerry= generatedQuerry+"+created:>="+createdFromDate.getText().toString().trim();
                     }
                if(!createdToDate.getText().toString().trim().equals(getString(R.string.date_format))){
                    generatedQuerry= generatedQuerry+"+created:<="+createdToDate.getText().toString().trim();
                }if(!pushedFromDate.getText().toString().trim().equals(getString(R.string.date_format))){
                    generatedQuerry= generatedQuerry+"+pushed:>="+pushedFromDate.getText().toString().trim();
                }if(!pushedToDate.getText().toString().trim().equals(getString(R.string.date_format))){
                    generatedQuerry= generatedQuerry+"+pushed:<="+pushedToDate.getText().toString().trim();
                }
                if(displayForks.isChecked()){
                    if(onlyForks.isChecked()){
                        generatedQuerry= generatedQuerry+"+fork:only";

                    }
                    else{
                        generatedQuerry= generatedQuerry+"+fork:true";

                    }
                }else {
                    generatedQuerry= generatedQuerry+"+fork:false";

                }
                String forks=forkCount.getText().toString().trim();
                if(!forks.isEmpty()){
                    generatedQuerry= generatedQuerry+"+forks:>"+forks;

                }
                if(!(inDescription.isChecked()&&inName.isChecked()&&inReadme.isChecked())){
                    if(inDescription.isChecked()){
                        generatedQuerry= generatedQuerry+"+in:name,description";

                    }
                    if(inName.isChecked()){
                        generatedQuerry= generatedQuerry+"+in:name";

                    }
                    if(inReadme.isChecked()){
                        generatedQuerry= generatedQuerry+"+in:readme";

                    }
                }
                String lang=language.getText().toString().trim();
                if(!lang.isEmpty()){
                    generatedQuerry= generatedQuerry+"+language:"+lang;

                }
                License license=(License)(liciense.getSelectedItem());
                if(license!=null) {
                    if (!license.getName().equals(getString(R.string.prompt_license))) {
                        generatedQuerry = generatedQuerry + "+license:" + license.getKey();

                    }
                }

                String user=userName.getText().toString().trim();
                if(!user.isEmpty()){
                    generatedQuerry= generatedQuerry+"+user:"+user;

                }
                if(archived.isChecked()){
                    generatedQuerry= generatedQuerry+"+archived:true";

                }
                filterDialog.dismiss();
               doMySearch(query,generatedQuerry);

            }
        });
        filterDialog.show();

    }

    private void getLicense(final Spinner view) {

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
        GitHubClient client = retrofit.create(GitHubClient.class);
        Call<List<License>> call = client.licenseList();

        call.enqueue(new Callback<List<License>>() {
            @Override
            public void onResponse(Call<List<License>> call, Response<List<License>> response) {
                if(response.isSuccessful()){

                    LicenceAdapter licenceAdapter=new LicenceAdapter(Home.this,android.R.layout.simple_spinner_item,new ArrayList<License>(response.body()));
                    licenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    view.setAdapter(licenceAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<License>> call, Throwable t) {

            }
        });

    }
}
