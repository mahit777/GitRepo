package myprivate.githubrepo.Model;

/**
 * Created by mahitej on 22-12-2017.
 */

public class License {
    private String key,name;

    public License(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
