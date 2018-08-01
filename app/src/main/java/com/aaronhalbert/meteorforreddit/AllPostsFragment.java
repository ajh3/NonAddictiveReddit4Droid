package com.aaronhalbert.meteorforreddit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AllPostsFragment extends Fragment implements Callback<AppOnlyOAuthToken> {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client";
    private static final String DEVICE_ID = "DO_NOT_TRACK_THIS_DEVICE";
    private static final String BASE_URL = "https://www.reddit.com";
    private static final String CLIENT_ID = "jPF59UF5MbMkWg";


    private String mParam1;
    private String mParam2;
    private RecyclerView rv = null;

    public AllPostsFragment() {
        // Required empty public constructor
    }

    public static AllPostsFragment newInstance(String param1, String param2) {
        AllPostsFragment fragment = new AllPostsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(com.aaronhalbert.meteorforreddit.R.layout.fragment_all_posts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rv = Objects.requireNonNull(getView()).findViewById(com.aaronhalbert.meteorforreddit.R.id.all_posts_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new PostsAdapter());
        rv.setHasFixedSize(true);

        // TODO Move Retrofit builder to a separate method

        Log.d(getClass().getSimpleName(), DEVICE_ID);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RedditInterface ri = retrofit.create(RedditInterface.class);

        ri.requestAppOnlyOAuthToken(GRANT_TYPE, DEVICE_ID).enqueue(this);
    }

    @Override
    public void onResponse(Call<AppOnlyOAuthToken> call, Response<AppOnlyOAuthToken> response) {
        Log.d(getClass().getSimpleName(),"Call succeeded with code=" + response.code() + " and has body = " + response.body());
        Log.d(getClass().getSimpleName(), new Gson().toJson(response.body()));

    }

    @Override
    public void onFailure(Call<AppOnlyOAuthToken> call, Throwable t) {
        Log.d(getClass().getSimpleName(), "Call failed");
    }

}
