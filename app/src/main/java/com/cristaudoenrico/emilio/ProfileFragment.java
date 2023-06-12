package com.cristaudoenrico.emilio;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    TextView stat_added, stat_saved, profile_name;
    LinkedList<Recipe> recipes_list = new LinkedList<>();
    ListView listView;
    SavedRecipeAdapter adapter;
    SharedPreferences sharedPreferences;
    ImageView settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        stat_added = v.findViewById(R.id.stat_number_added);
        stat_saved = v.findViewById(R.id.stat_number_saved);
        listView = v.findViewById(R.id.added_by_you);
        profile_name = v.findViewById(R.id.profle_name);
        settings = v.findViewById(R.id.btn_settings);
        sharedPreferences = v.getContext().getSharedPreferences("emilio", MODE_PRIVATE);

        settings.setOnClickListener(view -> {
            replaceFragment(new SettingsFragment());
        });

        profile_name.setText(sharedPreferences.getString("username", "Il tuo profile"));

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Recipe clicked_recipe = (Recipe)adapter.getItem(i);
            replaceFragment(new RecipeFragment(clicked_recipe,i, "true"));
        });

        recipes_list.clear();

        String url = MainActivity.BASE_URL+"get_profile.php?user_id="+sharedPreferences.getString("token", "-1");
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response ->  {
                        try {
                            JSONObject obj = new JSONObject(response);
                            stat_added.setText(obj.getString("num_added"));
                            stat_saved.setText(obj.getString("num_saved"));
                            JSONArray recipes = obj.getJSONArray("recipes");
                            Log.i("TEST",""+response);
                            for(int i=0; i<recipes.length(); i++) {
                                JSONObject o = recipes.getJSONObject(i);
                                recipes_list.add(new Recipe(
                                        o.getInt("id"),
                                        o.getString("name"),
                                        o.getString("description"),
                                        o.getString("preparation"),
                                        o.getString("type"),
                                        null,
                                        -1,
                                        o.getString("img")
                                ));
                            }
                            adapter = new SavedRecipeAdapter(requireContext(), R.layout.custom_recipe_item, recipes_list);
                            listView.setAdapter(adapter);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }, error -> Log.i("TEST", "errore: " + error));

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(request);

        return v;
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}