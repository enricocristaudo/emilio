package com.cristaudoenrico.emilio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SavedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SavedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SavedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SavedFragment newInstance(String param1, String param2) {
        SavedFragment fragment = new SavedFragment();
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

    SavedRecipeAdapter adapter;
    ListView list_view;
    LinkedList<Recipe> saved = new LinkedList<Recipe>();
    SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_saved, container, false);
        list_view = v.findViewById(R.id.saved_list);
        sharedPreferences = getActivity().getSharedPreferences("emilio", Context.MODE_PRIVATE);
        getSaved();
        return v;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void getSaved() {
        String url = MainActivity.BASE_URL+"get_saved.php?user_id="+sharedPreferences.getString("token", "-1");
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        saved.clear();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray json_saved = obj.getJSONArray("recipes");
                            int cont = 0;
                            while (cont < json_saved.length()) {
                                JSONObject o = json_saved.getJSONObject(cont);
                                String name, description, type, img, preparation;
                                double rating = o.getDouble("rating");
                                int id = o.getInt("recipe_id");
                                name = o.getString("name");
                                description = o.getString("description");
                                type = o.getString("type");
                                img = o.getString("img");
                                preparation = o.getString("preparation");
                                Recipe r = new Recipe(id,name,description, preparation, type,null,rating,img);
                                r.setSaved(true);
                                saved.add(r);
                                cont++;
                            }
                            load();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }

    private void load(){

        adapter = new SavedRecipeAdapter(getContext(), R.layout.custom_saved_recipe_item, saved);
        list_view.setAdapter(adapter);

        list_view.setOnItemClickListener((adapterView, view, i, l) -> {
            Recipe clicked_recipe = (Recipe)adapter.getItem(i);
            replaceFragment(new RecipeFragment(clicked_recipe,i, "false"));
        });
    }

}

