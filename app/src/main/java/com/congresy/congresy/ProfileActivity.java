package com.congresy.congresy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.congresy.congresy.remote.ApiUtils.getUserService;
import static com.congresy.congresy.remote.ApiUtils.useSession;

public class ProfileActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    public static String aux;

    TextView tName;
    TextView tSurname;
    TextView tEmail;
    TextView tPhone;
    TextView tPlace;
    TextView tNick;
    TextView tRole;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tName = findViewById(R.id.name);
        tSurname = findViewById(R.id.surname);
        tEmail = findViewById(R.id.email);
        tPhone = findViewById(R.id.phone);
        tNick = findViewById(R.id.nick);
        tRole = findViewById(R.id.role);
        tPlace = findViewById(R.id.place);
        image = findViewById(R.id.image);

        // Loading Profile in Background Thread
        new LoadProfile().execute();
    }

    private class LoadProfile extends AsyncTask<Void, Void, String> {
        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

            InputStream in = entity.getContent();

            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];

                n =  in.read(b);

                if (n>0) out.append(new String(b, 0, n));

            }

            return out.toString();

        }

 //       @Override
 //       protected void onPreExecute() {
 //           super.onPreExecute();//           pDialog = new ProgressDialog(ProfileActivity.this);
 //            pDialog.setMessage("Loading profile ...");
 //            pDialog.setIndeterminate(false);
 //            pDialog.setCancelable(false);
 //            pDialog.show();
 //        }

        @Override
        protected String doInBackground(Void... params) {
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet("https://congresy.herokuapp.com/actors");
            HttpGet httpGet1 = new HttpGet("https://congresy.herokuapp.com/actors/userAccount/" + LoginActivity.username);
            String text = null;
            String text1 = null;
            try {
                LoginActivity.httpClient = new DefaultHttpClient();

                useSession();

                HttpResponse response = LoginActivity.httpClient.execute(httpGet, localContext);

                HttpEntity entity = response.getEntity();

                text = getASCIIContentFromEntity(entity);

                HttpResponse response1 = LoginActivity.httpClient.execute(httpGet1, localContext);
                HttpEntity entity1 = response1.getEntity();
                text1 = getASCIIContentFromEntity(entity1);
                aux = text1;

            } catch (Exception e) {
                return e.getLocalizedMessage();

            }

            return text;

        }

        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            // dismiss the dialog after getting all products
            try
            {
                JSONArray array = new JSONArray(json);
                int pos = getPosition(array, LoginActivity.username);


                if (pos == -1){
                    Toast.makeText(ProfileActivity.this, "Your user is not found, try again", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject res = array.getJSONObject(pos);
                    String name = res.getString("name");
                    String surname = res.getString("surname");
                    String email = res.getString("email");
                    String phone = res.getString("phone");
                    String photo = res.getString("photo");
                    String nick = res.getString("nick");
                    String role = res.getString("role");
                    String place = res.getString("place");


                    // displaying all data in textview

                    tName.setText("Name: "  + name);
                    tSurname.setText("Surname: " + surname);
                    tEmail.setText("Email: " + email);
                    tPhone.setText("Phone: " + phone);
                    tNick.setText("Nick: " + nick);
                    tRole.setText("Role: " + role);
                    tPlace.setText("Place: " + place);

                    chargeImage(photo);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private int getPosition (JSONArray jsonArray, String username) throws JSONException {
        JSONObject jsonObject1 = new JSONObject(aux);
        for(int index = 0; index < jsonArray.length(); index++) {
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            if(jsonObject.getString("userAccount_").equals(jsonObject1.getString("id"))) {
                return index;
            }
        }
        return -1;
    }

    private void chargeImage(String url){
        Glide.with(getApplicationContext())
                .load(url) // Image URL
                .centerCrop() // Image scale type
                .crossFade()
                .override(800,500) // Resize image
                .into(image); // ImageView to display image
    }
}