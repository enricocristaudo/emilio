package com.cristaudoenrico.emilio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class RecipeAdapter extends ArrayAdapter<Recipe> {
    public RecipeAdapter(Context context, int idRowCustom, List list) {
        super(context, idRowCustom, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.custom_recipe_item, null);
        TextView name = convertView.findViewById(R.id.custom_item_name);
        TextView rating = convertView.findViewById(R.id.text_view_ratings);
        TextView preparation = convertView.findViewById(R.id.recipe_preparation);
        ImageView bg = convertView.findViewById(R.id.bg);

        Recipe r = getItem(position);
        name.setText(r.getName());
        rating.setText(String.valueOf(r.getRating()));
        Picasso.get().load("http://cristaudoenrico.altervista.org/android/emilio/res/"+r.getImg()).into(bg);
        return convertView;
    }
}
