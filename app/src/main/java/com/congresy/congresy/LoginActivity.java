package com.congresy.congresy;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.congresy.congresy.BaseActivity.checkString;

public class LoginActivity extends AppCompatActivity {

    private boolean auxOk = false;
    private boolean auxOk1 = false;

    EditText edtUsername;
    EditText edtPassword;

    TextView data;

    Button btnLogin;
    Button btnRegister;
    Button btnRegisterAux;

    UserService userService;

    private Integer aux = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Sign in");

        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegisterAux = findViewById(R.id.registerAux);

        btnLogin = findViewById(R.id.btnLogin);

        userService = ApiUtils.getUserService();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();

                if(validateLogin(username, password)){
                    doLogin(username, password);
                } else {
                    edtUsername.requestFocus();
                }
            }
        });

        btnRegisterAux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentConference = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intentConference);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.index_menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.register:
                startActivity(new Intent(this, RegisterActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validateLogin(String username, String password){

        if (checkString("blank", username, edtUsername, null))
            aux++;

        if (checkString("blank", password, edtPassword, null))
            aux++;

        return aux == 0;
    }

    private void loadActor(final String username, final String password){
        Call<Actor> call = userService.getActorByUsername(username);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if(response.isSuccessful()){

                    final Actor actor = response.body();

                    if (actor.getBanned()){
                        showAlertDialogButtonClicked2();
                    } else {

                        Intent intent;

                        if (actor.getRole().equals("Administrator")){
                            intent = new Intent(LoginActivity.this, AdministrationConferencesActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, HomeActivity.class);
                        }

                        SharedPreferences sp = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("Role", actor.getRole());
                        editor.putString("Name", actor.getName() + " " + actor.getSurname());
                        editor.putString("Id", actor.getId());
                        editor.putString("UserAccountId", actor.getUserAccount());
                        editor.putInt("logged", 1);
                        editor.putString("Username", username);
                        editor.putString("Password", password);
                        editor.apply();

                        SharedPreferences sp1 = getSharedPreferences("log_prefs", Activity.MODE_PRIVATE);
                        String role_ = sp1.getString("Role", "not found");

                        try {

                            Intent intentAux = getIntent();
                            intentAux.getExtras().get("fromLogin").toString();
                            startActivity(intent);

                        } catch (NullPointerException n){

                            if(auxOk || auxOk1) {
                                startActivity(intent);

                            } else {
                                try {
                                    Intent intentN = getIntent();
                                    String id = intentN.getExtras().get("idConference").toString();

                                    if (id != null && !role_.equals("User")){
                                        showAlertDialogButtonClicked();
                                    } else {
                                        if (actor.getConferences().contains(id)){
                                            showAlertDialogButtonClicked1();
                                        } else {
                                            Intent intentConference = new Intent(LoginActivity.this, JoiningConferenceActivity.class);
                                            intentConference.putExtra("price", getIntent().getStringExtra("price"));
                                            intentConference.putExtra("idConference", id);
                                            startActivity(intentConference);
                                        }
                                    }
                                } catch (Exception e) {
                                    startActivity(intent);
                                }
                            }
                        }
                    }


                } else {
                    Toast.makeText(LoginActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showAlertDialogButtonClicked() {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention!");
        builder.setMessage("Your role in the app do not allow you to do this action. Click OK to continue to login normally");

        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auxOk = true;
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showAlertDialogButtonClicked1() {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention!");
        builder.setMessage("You are not allowed to continue because you are already in the conference of the announcement. Click OK to continue to login normally ");

        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auxOk1 = true;
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showAlertDialogButtonClicked2() {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention!");
        builder.setMessage("Your account has been banned. For more information contact congresy@gmail.com giving your account details.");

        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auxOk = true;
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doLogin(final String username,final String password){
        Call call = userService.login(username,password);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    if(!response.raw().request().url().toString().contains("error")){

                        loadActor(username, password);

                    } else {
                        Toast.makeText(LoginActivity.this, "The username or password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
