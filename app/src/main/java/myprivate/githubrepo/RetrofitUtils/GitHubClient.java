package myprivate.githubrepo.RetrofitUtils;

import java.util.List;
import java.util.Map;

import myprivate.githubrepo.Model.License;
import myprivate.githubrepo.Model.Repository;
import myprivate.githubrepo.Model.SearchResult;
import myprivate.githubrepo.Model.User;
import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by mahitej on 16-12-2017.
 */

public interface GitHubClient {
    @GET("users/{user}/repos")
    Call<List<Repository>> userRepoList(
            @Path("user") String username
    );

    @GET("search/repositories")
    Call<SearchResult> seaerchRepo(@QueryMap Map<String,String> querryMap);
    @GET("repos/{full-name}/contributors")
    Call<List<User>> contributorsList(@Path(value = "full-name",encoded = true) String fullName);
    @GET("licenses")
    Call<List<License>> licenseList();


}
