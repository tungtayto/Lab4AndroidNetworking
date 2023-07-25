package com.example.lab4androidnetworking.fragment;

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
import android.widget.Toast;

import com.example.lab4androidnetworking.R;
import com.example.lab4androidnetworking.api.Contants;
import com.example.lab4androidnetworking.api.RequestInterface;
import com.example.lab4androidnetworking.model.ServerRequest;
import com.example.lab4androidnetworking.model.ServerResponse;
import com.example.lab4androidnetworking.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterFragment extends Fragment implements View.OnClickListener {
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private AppCompatButton btnRegister;
    private TextView tvLogin;
    private ProgressBar progress;


    public RegisterFragment() {
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        etName = (EditText) view.findViewById(R.id.et_name);
        etEmail = (EditText) view.findViewById(R.id.et_email);
        etPassword = (EditText) view.findViewById(R.id.et_password);
        btnRegister = (AppCompatButton) view.findViewById(R.id.btn_register);
        tvLogin = (TextView) view.findViewById(R.id.tv_login);
        progress = (ProgressBar) view.findViewById(R.id.progress);

        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_login) {
            goToLogin();
        } else if (view.getId() == R.id.btn_register) {
            String name = etName.getText().toString().trim();
            String email = etName.getText().toString().trim();
            String password = etName.getText().toString().trim();

            if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                progress.setVisibility(View.VISIBLE);
                registerProcess(name, email, password);
            } else {
                Snackbar.make(getView(), "Vui lòng nhập thông tin đầy đủ!", Snackbar.LENGTH_LONG).show();
            }
        }
    }
    //    private void registerProcess(String name, String email, String password) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Contants.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
//        User user = new User();
//        user.setName(name);
//        user.setEmail(email);
//        user.setPassword(password);
//        ServerRequest serverRequest = new ServerRequest();
//        serverRequest.setOperation(Contants.REGISTER_OPERATION);
//        serverRequest.setUser(user);
//
//        Call<ServerResponse> call = requestInterface.operation(serverRequest);
//        call.enqueue(new Callback<ServerResponse>() {
//            @Override
//            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
//                ServerResponse resp = response.body();
//                Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
//                progress.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onFailure(Call<ServerResponse> call, Throwable t) {
//                progress.setVisibility(View.INVISIBLE);
//                Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
//            }
//        });
////        call.enqueue(new Callback<ServerResponse>() {
////            @Override
////            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
////                ServerResponse resp = response.body();
////                Snackbar.make(getView(), resp.getMessage()+"respone register", Snackbar.LENGTH_LONG).show();
////                progress.setVisibility(View.INVISIBLE);
////            }
////
////            @Override
////            public void onFailure(Call<ServerResponse> call, Throwable t) {
////                progress.setVisibility(View.INVISIBLE);
////                Log.d("In Register", "onFailure: "+t.getMessage());
////                Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
////            }
////        });
//    }
    private void registerProcess(String name, String email, String password) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        ServerRequest request = new ServerRequest();
        request.setOperation(Contants.REGISTER_OPERATION);
        request.setUser(user);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Snackbar.make(getView(), resp.getMessage()+"respone register", Snackbar.LENGTH_LONG).show();
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                Log.d(Contants.TAG, "failed");
                Snackbar.make(getView(), t.getLocalizedMessage(),
                        Snackbar.LENGTH_LONG).show();
                t.printStackTrace(); // Add this line to print the stack trace
                Log.d("TAG_Res", "onFailure: " + t.getMessage());

            }
        });
    }

    private void goToLogin() {
        Fragment login = new LoginFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, login);
        ft.commit();

    }
}