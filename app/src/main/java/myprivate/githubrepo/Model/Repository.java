package myprivate.githubrepo.Model;

/**
 * Created by mahitej on 15-12-2017.
 */

public class Repository  {
    public static  final int REPO_TYPE=1;
    private String description;

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    private String html_url;
    private String name;
    private String id;
    private String contributors_url;

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    private String full_name;
    private Integer watchers,forks,stargazers_count;
    private User owner;

    public Repository(String description, String name, String id, String contributors_url, Integer watchers, Integer forks, Integer stargazers_count, User owner,String full_name) {
        this.description = description;
        this.name = name;
        this.id = id;
        this.full_name=full_name;
        this.contributors_url = contributors_url;
        this.watchers = watchers;
        this.forks = forks;
        this.stargazers_count = stargazers_count;
        this.owner = owner;

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContributors_url() {
        return contributors_url;
    }

    public void setContributors_url(String contributors_url) {
        this.contributors_url = contributors_url;
    }

    public Integer getWatchers() {
        return watchers;
    }

    public void setWatchers(Integer watchers) {
        this.watchers = watchers;
    }

    public Integer getForks() {
        return forks;
    }

    public void setForks(Integer forks) {
        this.forks = forks;
    }

    public Integer getStargazers_count() {
        return stargazers_count;
    }

    public void setStargazers_count(Integer stargazers_count) {
        this.stargazers_count = stargazers_count;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}

