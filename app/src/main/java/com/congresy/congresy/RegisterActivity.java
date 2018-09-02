package com.congresy.congresy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.congresy.congresy.domain.Actor;
import com.congresy.congresy.domain.Place;
import com.congresy.congresy.remote.ApiUtils;
import com.congresy.congresy.remote.UserService;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.congresy.congresy.BaseActivity.checkEmail;
import static com.congresy.congresy.BaseActivity.checkPasswordsAndEmails;
import static com.congresy.congresy.BaseActivity.checkPhone;
import static com.congresy.congresy.BaseActivity.checkString;
import static com.congresy.congresy.BaseActivity.checkUrl;

public class RegisterActivity extends AppCompatActivity {

    UserService userService;

    EditText edtUsername;
    EditText edtPassword;
    EditText edtPasswordConfirm;
    EditText edtName;
    EditText edtSurname;
    EditText edtEmail;
    EditText edtEmailConfirm;
    EditText edtPhone;
    EditText edtPhoto;

    // Place attributes
    EditText edtTown;
    EditText edtCountry;
    EditText edtAddress;
    EditText edtPostalCode;
    EditText edtDetails;

    Button btnRegister;

    private Integer aux = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Sing up");

        setContentView(R.layout.activity_register);

        // set spinner values
        String[] arraySpinner = new String[] {
                "Organizator", "User"
        };

        final Spinner s = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        btnRegister = findViewById(R.id.btnRegister);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtPasswordConfirm = findViewById(R.id.edtPasswordConfirm);
        edtName = findViewById(R.id.edtName);
        edtSurname = findViewById(R.id.edtSurname);
        edtEmail = findViewById(R.id.edtEmail);
        edtEmailConfirm = findViewById(R.id.edtEmailConfirm);
        edtPhone = findViewById(R.id.edtPhone);
        edtPhoto = findViewById(R.id.edtPhoto);

        // Pace attributes
        edtTown = findViewById(R.id.edtTown);
        edtCountry = findViewById(R.id.edtCountry);
        edtAddress = findViewById(R.id.edtAddress);
        edtPostalCode = findViewById(R.id.edtPostalCode);
        edtDetails = findViewById(R.id.edtDetails);

        userService = ApiUtils.getUserService();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsername.getText().toString();
                String password = edtPassword.getText().toString();
                String passwordConfirm = edtPasswordConfirm.getText().toString();
                String name = edtName.getText().toString();
                String surname = edtSurname.getText().toString();
                String email = edtEmail.getText().toString();
                String emailConfirm = edtEmailConfirm.getText().toString();
                String phone = edtPhone.getText().toString();
                String photo = edtPhoto.getText().toString();

                // Place attributes
                String town = edtTown.getText().toString();
                String country = edtCountry.getText().toString();
                String address = edtAddress.getText().toString();
                String postalCode = edtPostalCode.getText().toString();
                String details = edtDetails.getText().toString();

                // adding properties to json for POST
                JsonObject json = new JsonObject();
                JsonObject jsonActor = new JsonObject();
                JsonObject jsonAuth = new JsonObject();

                jsonAuth.addProperty("username", username);
                jsonAuth.addProperty("password", password);

                jsonActor.addProperty("name", name);
                jsonActor.addProperty("surname", surname);
                jsonActor.addProperty("email", email);
                jsonActor.addProperty("phone", phone);

                if(!photo.equals("null")){
                    jsonActor.addProperty("photo", photo);
                }

                String role = s.getSelectedItem().toString();

                jsonActor.addProperty("role", role);

                json.add("actor", jsonActor);
                json.add("userAccount", jsonAuth);

                JsonObject jsonPlace = new JsonObject();
                jsonPlace.addProperty("town", town);
                jsonPlace.addProperty("country", country);
                jsonPlace.addProperty("address", address);
                jsonPlace.addProperty("postalCode", postalCode);
                jsonPlace.addProperty("details", details);

                if (validate(name, surname, email, emailConfirm, phone, photo, town, country, address, postalCode, details, password, passwordConfirm)) {
                    doRegister(json, jsonPlace);
                } else {
                    edtUsername.requestFocus();
                }

            }
        });
    }

    private boolean validate(String name, String surname, String email, String email2, String phone, String photo, String town, String country, String address, String postalCode, String details, String pas1, String pas2){

        if (checkString("both", name, edtName, 20))
                aux++;

        if (checkString("both", surname, edtSurname, 40))
            aux++;

        if (checkString("blank", email, edtEmail, null) || checkEmail(email, edtEmail))
            aux++;

        if (checkString("blank", email2, edtEmailConfirm, null))
            aux++;

        if (checkString("blank", phone, edtPhone, null) || checkPhone(phone, edtPhone))
            aux++;

        if (checkString("both", town, edtTown, 40))
            aux++;

        if (checkString("both", country, edtCountry, 30))
            aux++;

        if (checkString("both", address, edtAddress, 60))
            aux++;

        if (checkString("both", postalCode, edtPostalCode, 15))
            aux++;

        if (checkString("both", details, edtDetails, 70))
            aux++;


        if (checkString("blank", pas1, edtPassword, null))
            aux++;

        if (checkString("blank", pas2, edtPasswordConfirm, null))
            aux++;

        if (!photo.equals(""))
            if(checkUrl(photo, edtPhoto) && checkString("blank", photo, edtPhoto, null))
                aux++;

        if (aux == 0){
            if (checkPasswordsAndEmails(pas1, pas2, edtPassword, edtPasswordConfirm, email, email2, edtEmail, edtEmailConfirm)){
                aux++;
            }
        }

        return aux == 0;
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

    private void createPlace(final JsonObject jsonPlace, String id){
        Call<Place> call = userService.createPlace(jsonPlace, id);
        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doRegister(final JsonObject json, final JsonObject jsonPlace){
        Call<Actor> call = userService.register(json);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if(response.isSuccessful()){

                    Actor actor = response.body();

                    createPlace(jsonPlace, actor.getId());

                } else {
                    if (response.code() == 409) {
                        Toast.makeText(RegisterActivity.this, "Username is already taken", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 406){
                        Toast.makeText(RegisterActivity.this, "Email is already taken", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
