package com.cristaudoenrico.emilio;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.BundleCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cristaudoenrico.emilio.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int CAMERA_PERM_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int PICK_REQUEST_CODE = 103;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Bitmap bitmap;

    public AddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    LinkedList<Ingredient> ingredients_list = new LinkedList<>();
    IngredientListAdapter adapter;
    SearchView searchView;
    ListView ingredientListView;
    ImageView photo_placeholder;
    Button btn_save_recipe;
    EditText custom_recipe_name, description, preparation;
    ChipGroup type_add_new;
    String type;
    BottomNavigationView bottomNavigationView;
    SharedPreferences sharedPreferences;

    public static LinkedList<Ingredient> chosen_ingredients = new LinkedList<>();

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                    photo_placeholder.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });
    ActivityResultLauncher<Intent> take_photo = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK) {
                Bundle bundle = result.getData().getExtras();
                bitmap = (Bitmap) bundle.get("data");
                photo_placeholder.setImageBitmap(bitmap);
            }
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        load_ingredients();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add, container, false);
        searchView = v.findViewById(R.id.ingredient_search_bar);
        ingredientListView = v.findViewById(R.id.ingredients_add_new_list);
        photo_placeholder = v.findViewById(R.id.photo_placeholder);
        btn_save_recipe = v.findViewById(R.id.btn_save_recipe);
        custom_recipe_name = v.findViewById(R.id.name_add_new);
        description = v.findViewById(R.id.desc_add_new);
        preparation = v.findViewById(R.id.prep_add_new);
        type_add_new = v.findViewById(R.id.type_add_new);
        ingredients_list.clear();
        bottomNavigationView = v.findViewById(R.id.bottomNavigationView);
        sharedPreferences = getActivity().getSharedPreferences("emilio", Context.MODE_PRIVATE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                LinkedList<Ingredient> filtered = new LinkedList<>();
                for (Ingredient i: ingredients_list) {
                    if(i.getName().toLowerCase().contains(s)) filtered.add(i);
                }
                adapter = new IngredientListAdapter(getContext(), R.layout.custom_ingredient_list_item, filtered);
                ingredientListView.setAdapter(adapter);
                return false;
            }
        });

        type_add_new.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId != -1) {
                Chip c = v.findViewById(checkedId);
                type = (String) c.getText();
            }
        });


        Button btn_take_photo = v.findViewById(R.id.btn_scatta);
        Button btn_choose_photo = v.findViewById(R.id.btn_scegli);

        btn_take_photo.setOnClickListener(view -> {
            askCameraPermission();
        });

        btn_choose_photo.setOnClickListener(view -> {
            Intent gallery = new Intent(Intent.ACTION_PICK);
            gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(gallery);
        });

        btn_save_recipe.setOnClickListener(view -> {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            if(bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                final String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);

                String URL = MainActivity.BASE_URL + "save_recipe.php";
                RequestQueue queue = Volley.newRequestQueue(requireContext());

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response.equals("success")){
                                    Toast.makeText(getContext(), "Ricetta salvata!", Toast.LENGTH_SHORT).show();
                                    replaceFragment(new HomeFragment());
                                    ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
                                    binding.bottomNavigationView.setSelectedItemId(R.id.home);
                                } else {
                                    Log.i("TEST", ""+response);
                                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), "error.", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("image", base64Image);

                        JSONArray ingredients_array = new JSONArray();
                        for(Ingredient i : chosen_ingredients) {
                            ingredients_array.put(i.getId());
                        }
                        params.put("ingredients", ingredients_array.toString());
                        params.put("name", String.valueOf(custom_recipe_name.getText()));
                        params.put("description", String.valueOf(description.getText()));
                        params.put("preparation", String.valueOf(preparation.getText()));
                        params.put("type", type);
                        params.put("user_id", sharedPreferences.getString("token", "-1"));

                        return params;
                    }
                };
                queue.add(stringRequest);
            }
        });

        return v;
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        take_photo.launch(camera);
    }

    private void load_ingredients(){
        String url = MainActivity.BASE_URL+"get_ingredients.php";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray json_saved = obj.getJSONArray("ingredients");
                            int cont = 0;
                            while (cont < json_saved.length()) {
                                JSONObject o = json_saved.getJSONObject(cont);
                                String name = o.getString("ingredient_name");
                                int id = o.getInt("ingredient_id");
                                ingredients_list.add(new Ingredient(name, "", id));
                                cont++;
                            }

                            adapter = new IngredientListAdapter(getContext(), R.layout.custom_ingredient_list_item, ingredients_list);
                            ingredientListView.setAdapter(adapter);

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
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(request);
    }


    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }else {
                Toast.makeText(getContext(), "Camera permission are required", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
