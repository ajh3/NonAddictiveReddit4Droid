package com.aaronhalbert.nosurfforreddit.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aaronhalbert.nosurfforreddit.R;

public class LoginFragment extends BaseFragment {

    private OnLoginFragmentButtonListener onLoginFragmentButtonListener;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginButton = v.findViewById(R.id.login_fragment_button);
        loginButton.setOnClickListener(v1 -> login());

        return v;
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentButtonListener) {
            onLoginFragmentButtonListener = (OnLoginFragmentButtonListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginFragmentButtonListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onLoginFragmentButtonListener = null;
    }

    public void login() {
        if (onLoginFragmentButtonListener != null) {
            onLoginFragmentButtonListener.login();
        }
    }

    public interface OnLoginFragmentButtonListener {
        public void login();
    }
}
