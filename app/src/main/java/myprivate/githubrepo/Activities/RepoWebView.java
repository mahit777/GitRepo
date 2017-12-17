package myprivate.githubrepo.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import myprivate.githubrepo.R;

public class RepoWebView extends AppCompatActivity {
    WebView webView;
    String repoUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.project_details));
        setContentView(R.layout.activity_repo_web_view);
        repoUrl=getIntent().getExtras().getString(getString(R.string.html_url));
        webView=(WebView)findViewById(R.id.repo_webview);

    }

    @Override
    protected void onStart() {
        super.onStart();
        webView.loadUrl(repoUrl);


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
