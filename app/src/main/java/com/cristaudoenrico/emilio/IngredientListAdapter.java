package com.cristaudoenrico.emilio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class IngredientListAdapter extends ArrayAdapter<Ingredient> {


    public IngredientListAdapter(Context context, int idRowCustom, List list) {
        super(context, idRowCustom, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.custom_ingredient_list_item, null);
        CheckBox checkBox = convertView.findViewById(R.id.item_check_box);
        CardView cardView = convertView.findViewById(R.id.ingredient_card_view);
        Ingredient i = getItem(position);

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) {
                AddFragment.chosen_ingredients.add(i);
            } else {
                AddFragment.chosen_ingredients.remove(i);
            }
            Log.i("TEST", AddFragment.chosen_ingredients.toString());
        });

        checkBox.setText(i.getName());
        return convertView;
    }
}
