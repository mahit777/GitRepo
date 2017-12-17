package myprivate.githubrepo.SearchUtils;

import android.content.SearchRecentSuggestionsProvider;
import android.net.Uri;

/**
 * Created by mahitej on 17-12-2017.
 */

public class SuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "myprivate.githubrepo.SearchUtils.SuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return super.delete(uri, selection, selectionArgs);
    }
}
