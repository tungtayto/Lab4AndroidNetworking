package com.example.lab4androidnetworking.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lab4androidnetworking.R;
import com.example.lab4androidnetworking.api.Contants;
import com.example.lab4androidnetworking.api.RequestInterface;
import com.example.lab4androidnetworking.model.ServerRequest;
import com.example.lab4androidnetworking.model.ServerResponse;
import com.example.lab4androidnetworking.model.User;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private EditText etEmail;
    private EditText etPassword;
    private AppCompatButton btnLogin;
    private TextView tvRegister;
    private ProgressBar progress;
    private TextView tvForgot;
    private SharedPreferences pref;

    public LoginFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        pref = getActivity().getPreferences(0);
        etEmail = (EditText) view.findViewById(R.id.et_email);
        etPassword = (EditText) view.findViewById(R.id.et_password);
        btnLogin = (AppCompatButton) view.findViewById(R.id.btn_login);
        tvRegister = (TextView) view.findViewById(R.id.tv_register);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        tvForgot = (TextView) view.findViewById(R.id.tv_forgot);

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvForgot.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login){
            String email =  etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()){
                progress.setVisibility(View.VISIBLE);
                loginProcess(email, password);
            } else {
                Snackbar.make(getView(),"Fields are empty !",Snackbar.LENGTH_LONG).show();
            }
        } else if (view.getId() == R.id.tv_register){
            Snackbar.make(getView(),"Hello",Snackbar.LENGTH_LONG).show();
            goToRegister();
        } else if (view.getId() == R.id.tv_forgot){
            goToResetPassword();
        }
    }

    private void goToRegister() {
        Fragment register = new RegisterFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, register);
        ft.commit();
    }

    private void goToProfile() {
        Fragment profile = new ProfileFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, profile);
        ft.commit();
    }

    private void loginProcess(String email, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Contants.LOGIN_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call,
                                   Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                Snackbar.make(getView(),resp.getMessage(),Snackbar.LENGTH_LONG).show();
                if(resp.getResult().equals(Contants.SUCCESS)){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(Contants.IS_LOGGED_IN,true);

                    editor.putString(Contants.EMAIL,resp.getUser().getEmail());
                    editor.putString(Contants.NAME,
                            resp.getUser().getName());

                    editor.putString(Contants.UNIQUE_ID,resp.getUser().getUnique_id());
                    editor.apply();
                    goToProfile();
                }
                progress.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                Log.d(Contants.TAG,"failed");

                Snackbar.make(getView(),t.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToResetPassword(){
        Fragment reset = new PasswordResetFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame,reset);
        ft.commit();
    }

}