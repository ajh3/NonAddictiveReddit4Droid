package com.aaronhalbert.nosurfforreddit;

import android.arch.core.util.Function;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.aaronhalbert.nosurfforreddit.reddit.Listing;

import java.util.List;

public class NoSurfViewModel extends ViewModel {
    private NoSurfRepository repository = NoSurfRepository.getInstance();

    private final LiveData<Listing> allPostsLiveData =
            Transformations.switchMap(repository.getAllPostsLiveData(),
                    new Function<Listing, LiveData<Listing>>() {
                        @Override
                        public LiveData<Listing> apply(Listing input) {
                            final MutableLiveData<Listing> listing = new MutableLiveData<>();
                            listing.setValue(input);
                            return listing;
                        }
                    });

    private final LiveData<Listing> homePostsLiveData =
            Transformations.switchMap(repository.getHomePostsLiveData(),
                    new Function<Listing, LiveData<Listing>>() {
                        @Override
                        public LiveData<Listing> apply(Listing input) {
                            final MutableLiveData<Listing> listing = new MutableLiveData<>();
                            listing.setValue(input);
                            return listing;
                        }
                    });

    private final LiveData<List<Listing>> commentsLiveData =
            Transformations.switchMap(repository.getCommentsLiveData(),
                    new Function<List<Listing>, LiveData<List<Listing>>>() {
                        @Override
                        public LiveData<List<Listing>> apply(List<Listing> input) {
                            final MutableLiveData<List<Listing>> commentListing = new MutableLiveData<>();
                            commentListing.setValue(input);
                            return commentListing;
                        }
                    });

    public void initApp() {

        repository.requestAppOnlyOAuthToken();

    }

    public LiveData<Listing> getAllPostsLiveData() {
        return allPostsLiveData;
    }

    public LiveData<Listing> getHomePostsLiveData() {
        return homePostsLiveData;
    }

    public LiveData<List<Listing>> getCommentsLiveData() {
        return commentsLiveData;
    }

    public void requestAllSubredditsListing() {
        repository.requestAllSubredditsListing();
    }

    public void requestHomeSubredditsListing() {
        repository.requestHomeSubredditsListing();
    }

    public void requestPostCommentsListing(String id) {

        repository.requestPostCommentsListing(id);
    }

    public void requestUserOAuthToken(String code) {
        repository.requestUserOAuthToken(code);
    }



}
