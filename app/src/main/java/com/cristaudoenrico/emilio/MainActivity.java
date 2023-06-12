package com.cristaudoenrico.emilio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cristaudoenrico.emilio.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Fragment home = new HomeFragment();
    Fragment saved = new SavedFragment();
    Fragment profile = new ProfileFragment();
    Fragment add_new = new AddFragment();

    JSONArray json_saved;
    JSONArray json_recipes;
    RequestQueue mQueue;
    RecipeAdapter adapter;
    ListView home_list_view;

    public static final String BASE_URL = "http://cristaudoenrico.altervista.org/android/emilio/api/";

    public static LinkedList<Recipe> list;
    public static LinkedList<Recipe> recipes_list = new LinkedList<>();
    public static LinkedList<Recipe> saved_list = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.home) {
                replaceFragment(home);
            }
            if(item.getItemId() == R.id.saved) {
                replaceFragment(saved);
            }
            if(item.getItemId() == R.id.profile) {
                replaceFragment(profile);
            }
            if(item.getItemId() == R.id.add_new) {
                replaceFragment(add_new);
            }
            return true;
        });
        load();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void getSaved() {
        String url = BASE_URL+"get_saved.php";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            json_saved = obj.getJSONArray("recipes");
                            int cont = 0;
                            while (cont < json_saved.length()) {
                                for (int j = 0; j < json_saved.length(); j++) {
                                    JSONObject o = json_saved.getJSONObject(j);

                                    if(recipes_list.get(cont).getId() == o.getInt("recipe_id")){
                                        recipes_list.get(cont).setSaved(true);
                                        saved_list.add(recipes_list.get(cont));
                                    }
                                }
                                cont++;
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TEST", "errore: "+error);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void load() {
        String url = BASE_URL+"get_recipes.php";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            json_recipes = obj.getJSONArray("recipes");
                            int cont = 0;

                            while(cont < json_recipes.length()) {
                                JSONObject recipe = json_recipes.getJSONObject(cont);
                                int id = recipe.getInt("recipe_id");
                                String name = recipe.getString("name");
                                String description = recipe.getString("description");
                                double rating = recipe.getDouble("rating");
                                String type = recipe.getString("type");
                                String img = recipe.getString("img");
                                String preparation = recipe.getString("preparation");
                                recipes_list.add(new Recipe(id, name, description, preparation, type, null, rating, img));
                                cont++;
                            }

                            list = new LinkedList<Recipe>();
                            home_list_view = findViewById(R.id.home_list);
                            list.addAll(recipes_list);

                            getSaved();
                            replaceFragment(home);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TEST", "errore: "+error);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}