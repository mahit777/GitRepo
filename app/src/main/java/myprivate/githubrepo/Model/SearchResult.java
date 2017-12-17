package myprivate.githubrepo.Model;

import java.util.ArrayList;

/**
 * Created by mahitej on 16-12-2017.
 */

public class SearchResult {
    Integer total_count;
    boolean incomplete_results;
    ArrayList<Repository>items;

    public SearchResult(Integer total_count, boolean incomplete_results, ArrayList<Repository> items) {
        this.total_count = total_count;
        this.incomplete_results = incomplete_results;
        this.items = items;
    }

    public Integer getTotal_count() {
        return total_count;
    }

    public void setTotal_count(Integer total_count) {
        this.total_count = total_count;
    }

    public boolean isIncomplete_results() {
        return incomplete_results;
    }

    public void setIncomplete_results(boolean incomplete_results) {
        this.incomplete_results = incomplete_results;
    }

    public ArrayList<Repository> getItems() {
        return items;
    }

    public void setItems(ArrayList<Repository> items) {
        this.items = items;
    }
}
