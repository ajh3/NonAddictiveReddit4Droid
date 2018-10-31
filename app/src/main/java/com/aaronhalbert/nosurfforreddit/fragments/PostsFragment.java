package com.aaronhalbert.nosurfforreddit.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaronhalbert.nosurfforreddit.NoSurfViewModel;
import com.aaronhalbert.nosurfforreddit.adapters.PostsAdapter;
import com.aaronhalbert.nosurfforreddit.R;
import com.aaronhalbert.nosurfforreddit.viewstate.PostsViewState;

abstract public class PostsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private PostsAdapter postsAdapter;
    NoSurfViewModel viewModel;
    LiveData<PostsViewState> postsLiveDataViewState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(NoSurfViewModel.class);
        postsAdapter = setPostsAdapter();
        setPostsLiveDataViewState();
        observePostsLiveDataViewState();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_posts, container, false);

        setupRecyclerView(v);
        setupSwipeRefreshLayout(v);

        return v;
    }

    // region helper methods -----------------------------------------------------------------------

    private void observePostsLiveDataViewState() {
        postsLiveDataViewState.observe(this, listing -> {
            cancelRefreshingAnimation();
        });
    }

    private void cancelRefreshingAnimation() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setupRecyclerView(View v) {
        Context context = getContext();

        RecyclerView rv = v.findViewById(R.id.posts_fragment_recyclerview);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        rv.setAdapter(postsAdapter);
        rv.setHasFixedSize(true);
    }

    private void setupSwipeRefreshLayout(View v) {
        swipeRefreshLayout = v.findViewById(R.id.posts_fragment_swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    // endregion helper methods --------------------------------------------------------------------

    // region abstract methods ---------------------------------------------------------------------

    abstract PostsAdapter setPostsAdapter();
    abstract void setPostsLiveDataViewState();

    // endregion abstract methods ------------------------------------------------------------------
}
