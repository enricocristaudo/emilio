package com.cristaudoenrico.emilio;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    RecipeAdapter adapter;
    ListView home_list_view;
    ChipGroup chipGroup;
    public static LinkedList<Recipe> recipes_list = new LinkedList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        chipGroup = v.findViewById(R.id.chip_group);
        home_list_view = v.findViewById(R.id.home_list);
        load();
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            LinkedList<Recipe> filtered_list_primi = new LinkedList<Recipe>();
            for (int i = 0; i < MainActivity.list.size(); i++) {
                if(MainActivity.list.get(i).getType().equals("primo")) {
                    filtered_list_primi.add(MainActivity.list.get(i));
                }
            }
            LinkedList<Recipe> filtered_list_secondi = new LinkedList<Recipe>();
            for (int i = 0; i < MainActivity.list.size(); i++) {
                if(MainActivity.list.get(i).getType().equals("secondo")) {
                    filtered_list_secondi.add(MainActivity.list.get(i));
                }
            }
            LinkedList<Recipe> filtered_list_dolci = new LinkedList<Recipe>();
            for (int i = 0; i < MainActivity.list.size(); i++) {
                if(MainActivity.list.get(i).getType().equals("dolce")) {
                    filtered_list_dolci.add(MainActivity.list.get(i));
                }
            }
            if(checkedId == -1) {
                // niente selezionato
                adapter = new RecipeAdapter(getContext(), R.layout.custom_recipe_item, MainActivity.list);
                home_list_view.setAdapter(adapter);
            }
            if(checkedId == R.id.chip5) {
                // primi piatti
                adapter = new RecipeAdapter(getContext(), R.layout.custom_recipe_item, filtered_list_primi);
                home_list_view.setAdapter(adapter);
            }
            if(checkedId == R.id.chip6) {
                // secondi piatti
                adapter = new RecipeAdapter(getContext(), R.layout.custom_recipe_item, filtered_list_secondi);
                home_list_view.setAdapter(adapter);
            }
            if(checkedId == R.id.chip7) {
                // dolci
                adapter = new RecipeAdapter(getContext(), R.layout.custom_recipe_item, filtered_list_dolci);
                home_list_view.setAdapter(adapter);
            }

        });
        return v;
    }

    private void load() {
        adapter = new RecipeAdapter(getContext(), R.layout.custom_recipe_item, MainActivity.list);
        Log.i("TEST", String.valueOf(MainActivity.list.size()));
        home_list_view.setAdapter(adapter);

        home_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe clicked_recipe = (Recipe)adapter.getItem(position);
                replaceFragment(new RecipeFragment(clicked_recipe, position, "false"));
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
