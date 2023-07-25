package com.example.lab4androidnetworking.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
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

public class PasswordResetFragment extends Fragment implements View.OnClickListener {
    private TextView timer;
    private EditText etEmail;
    private EditText etCode;
    private EditText etPassword;
    private AppCompatButton btnReset;
    private ProgressBar progress;
    private boolean isResetInitiated = false;

    private CountDownTimer countDownTimer;
    private String email;


    public PasswordResetFragment() {
        // Required empty public constructor
    }

    public static PasswordResetFragment newInstance(String param1, String param2) {
        PasswordResetFragment fragment = new PasswordResetFragment();
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
        View view = inflater.inflate(R.layout.fragment_password_reset, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initViews(View view) {
        timer = (TextView) view.findViewById(R.id.timer);
        etEmail = (EditText) view.findViewById(R.id.et_email);
        etCode = (EditText) view.findViewById(R.id.et_code);
        etPassword = (EditText) view.findViewById(R.id.et_password);
        btnReset = (AppCompatButton) view.findViewById(R.id.btn_reset);
        progress = (ProgressBar) view.findViewById(R.id.progress);

        etCode.setVisibility(View.GONE);
        etPassword.setVisibility(View.GONE);
        timer.setVisibility(View.GONE);
        btnReset.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_reset) {
            if (!isResetInitiated) {
                email = etEmail.getText().toString();
                if (!email.isEmpty()) {
                    progress.setVisibility(View.VISIBLE);
                    initiateResetPasswordProcess(email);
                } else {
                    Snackbar.make(getView(), "Fields are empty !",
                            Snackbar.LENGTH_LONG).show();
                }
            } else {
                String code = etCode.getText().toString();
                String password = etPassword.getText().toString();
                if (!code.isEmpty() && !password.isEmpty()) {
                    finishResetPasswordProcess(email, code, password);
                } else {
                    Snackbar.make(getView(), "Fields are empty !",
                            Snackbar.LENGTH_LONG).show();
                }
            }

        }
    }

    private void initiateResetPasswordProcess(String email) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface requestInterface =
                retrofit.create(RequestInterface.class);
        User user = new User();
        user.setEmail(email);
        ServerRequest request = new ServerRequest();
        request.setOperation(Contants.RESET_PASSWORD_INITIATE);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call,
                                   Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Snackbar.make(getView(), resp.getMessage(),
                        Snackbar.LENGTH_LONG).show();
                if (resp.getResult().equals(Contants.SUCCESS)) {
                    Snackbar.make(getView(), resp.getMessage(),
                            Snackbar.LENGTH_LONG).show();
                    etEmail.setVisibility(View.GONE);
                    etCode.setVisibility(View.VISIBLE);
                    etPassword.setVisibility(View.VISIBLE);
                    timer.setVisibility(View.VISIBLE);
                    btnReset.setText("Change Password");
                    isResetInitiated = true;
                    startCountdownTimer();
                } else {
                    Snackbar.make(getView(), resp.getMessage(),
                            Snackbar.LENGTH_LONG).show();
                }
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                Log.d(Contants.TAG, "failed");
                Snackbar.make(getView(), t.getLocalizedMessage(),
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void finishResetPasswordProcess(String email, String code, String
            password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Contants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface requestInterface =
                retrofit.create(RequestInterface.class);
        User user = new User();
        user.setEmail(email);
        user.setCode(code);
        user.setPassword(password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Contants.RESET_PASSWORD_FINISH);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call,
                                   Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Snackbar.make(getView(), resp.getMessage(),
                        Snackbar.LENGTH_LONG).show();
                if (resp.getResult().equals(Contants.SUCCESS)) {
                    Snackbar.make(getView(), resp.getMessage(),
                            Snackbar.LENGTH_LONG).show();
                    countDownTimer.cancel();
                    isResetInitiated = false;
                    goToLogin();
                } else {
                    Snackbar.make(getView(), resp.getMessage(),
                            Snackbar.LENGTH_LONG).show();
                }
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                Log.d(Contants.TAG, "failed");
                Snackbar.make(getView(), t.getLocalizedMessage(),
                        Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                timer.setText("Time remaining : " + millisUntilFinished /
                        1000);
            }

            public void onFinish() {
                Snackbar.make(getView(), "Time Out ! Request again to reset password.", Snackbar.LENGTH_LONG).show();
                goToLogin();
            }
        }.start();
    }

    private void goToLogin() {
        Fragment login = new LoginFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, login);
        ft.commit();
    }
}