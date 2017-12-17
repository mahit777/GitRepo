package myprivate.githubrepo.Model;

/**
 * Created by mahitej on 15-12-2017.
 */

public class User {
    public static  final int USER_TYPE=0;

    String id,name,email, avatar_url,login,url,bio,location,hireable;

    public User(String id, String name, String email, String imageUrl, String gravatar_id, String login, String url, String bio, String location, String hireable) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar_url = imageUrl;
        this.login = login;
        this.url = url;
        this.bio = bio;
        this.location = location;
        this.hireable = hireable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }



    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHireable() {
        return hireable;
    }

    public void setHireable(String hireable) {
        this.hireable = hireable;
    }
}
