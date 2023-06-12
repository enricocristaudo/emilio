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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class SavedRecipeAdapter extends ArrayAdapter<Recipe> {
    public SavedRecipeAdapter(Context context, int idRowCustom, List list) {
        super(context, idRowCustom, list);
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.custom_saved_recipe_item, null);
        TextView name = convertView.findViewById(R.id.saved_name);
        TextView description = convertView.findViewById(R.id.saved_description);
        ImageView cover = convertView.findViewById(R.id.saved_cover);

        Recipe r = getItem(position);
        name.setText(r.getName());
        description.setText(r.getDescription());
        Picasso.get().load("http://cristaudoenrico.altervista.org/android/emilio/res/"+r.getImg()).into(cover);
        return convertView;
    }
}
