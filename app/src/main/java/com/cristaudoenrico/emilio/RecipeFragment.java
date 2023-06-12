package com.cristaudoenrico.emilio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static Recipe recipe;
    private static String custom;
    private static int index;

    public RecipeFragment(Recipe r, int index, String custom) {
        this.recipe = r;
        this.index = index;
        this.custom = custom;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeFragment newInstance(String param1, String param2) {
        RecipeFragment fragment = new RecipeFragment(recipe, index, custom);
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

    ImageView cover;
    TextView name, description;
    Button button;
    IngredientAdapter ingredientAdapter;
    ListView list_view_ingredients;
    SharedPreferences sharedPreferences;
    RequestQueue queue;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recipe, container, false);
        queue = Volley.newRequestQueue(requireContext());
        cover = v.findViewById(R.id.cover_image);
        name = v.findViewById(R.id.name_text_view);
        description = v.findViewById(R.id.description_text_view);
        button = v.findViewById(R.id.btn_save);
        sharedPreferences = getActivity().getSharedPreferences("emilio", Context.MODE_PRIVATE);

        Picasso.get().load("http://cristaudoenrico.altervista.org/android/emilio/res/"+recipe.getImg()).into(cover);
        name.setText(recipe.getName());
        description.setText(recipe.getDescription());

        String url = MainActivity.BASE_URL+"is_saved.php?user_id="+sharedPreferences.getString("token", "-1")+"&recipe_id="+recipe.getId();
        StringRequest request = new StringRequest(Request.Method.GET, url, response ->  {
            if(response.equals("true")) {
                recipe.setSaved(true);
                button.setText(R.string.rimuovi_dai_preferiti);
                MainActivity.recipes_list.get(index).setSaved(true);
                MainActivity.saved_list.add(recipe);
                button.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_close_24), null, null, null);

            }else {
                recipe.setSaved(false);
                button.setText(R.string.aggiungi_ai_preferiti);
                MainActivity.recipes_list.get(index).setSaved(false);
                MainActivity.saved_list.remove(recipe);
                button.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_add_24), null, null, null);

            }

        }, error -> Log.i("TEST", "errore: " + error));

        queue.add(request);

        button.setOnClickListener(view -> {

            if(!recipe.isSaved()) {
                String JSON_URL = MainActivity.BASE_URL + "insert_saved.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.i("TEST", response);
                                    JSONObject obj = new JSONObject(response);
                                    if(obj.getInt("success") == 1){
                                        Toast.makeText(view.getContext(), "Ricetta salvata!", Toast.LENGTH_SHORT).show();
                                        button.setText("Rimuovi dai preferiti");
                                        MainActivity.recipes_list.get(index).setSaved(true);
                                        recipe.setSaved(true);
                                        MainActivity.saved_list.add(recipe);
                                        button.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_close_24), null, null, null);
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //displaying the error in toast if occurrs
                                Toast.makeText(view.getContext(), "Si è verificato un errore, riprova più tardi", Toast.LENGTH_SHORT).show();
                            }
                        }
                )
                {
                    @Override
                    protected HashMap<String, String> getParams()
                    {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("recipe_id", String.valueOf(recipe.getId()));
                        params.put("user_id", sharedPreferences.getString("token", "-1"));
                        return params;
                    }
                };
                queue.add(stringRequest);
            } else {
                String JSON_URL = MainActivity.BASE_URL+"remove_saved.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, JSON_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.i("TEST", response);
                                    JSONObject obj = new JSONObject(response);
                                    if(obj.getInt("success") == 1){
                                        Toast.makeText(view.getContext(), "Ricetta rimossa", Toast.LENGTH_SHORT).show();
                                        button.setText("Aggiungi ai preferiti");
                                        MainActivity.recipes_list.get(index).setSaved(false);
                                        recipe.setSaved(false);
                                        MainActivity.saved_list.remove(recipe);
                                        button.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.baseline_add_24), null, null, null);
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //displaying the error in toast if occurrs
                                Toast.makeText(view.getContext(), "Si è verificato un errore, riprova più tardi", Toast.LENGTH_SHORT).show();
                            }
                        }
                )
                {
                    @Override
                    protected HashMap<String, String> getParams()
                    {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("recipe_id", String.valueOf(recipe.getId()));
                        params.put("user_id", sharedPreferences.getString("token", "-1"));
                        return params;
                    }
                };
                queue.add(stringRequest);
            }


        });


        loadIngredients();
        return v;


    }

    private void loadIngredients() {
        String url = MainActivity.BASE_URL+"get_ingredient_per_recipe.php?recipe_id="+recipe.getId()+"&custom="+custom;
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray json_saved = obj.getJSONArray("ingredients");
                            LinkedList<Ingredient> ingredients = new LinkedList<>();
                            int cont = 0;
                            while (cont < json_saved.length()) {
                                JSONObject o = json_saved.getJSONObject(cont);
                                ingredients.add(new Ingredient(o.getString("name"),o.getString("quantity"), -1));
                                cont++;
                            }
                            MainActivity.recipes_list.get(index).setIngredients(ingredients);
                            recipe.setIngredients(ingredients);
                            ingredientAdapter = new IngredientAdapter(getContext(), R.layout.custom_ingredient_item, recipe.getIngredients(), recipe);
                            list_view_ingredients = getView().findViewById(R.id.list_view_ingredients);
                            list_view_ingredients.setAdapter(ingredientAdapter);

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
        queue.add(request);
    }
}