package com.cristaudoenrico.emilio;

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

public class IngredientAdapter extends ArrayAdapter<Ingredient> {

    private Recipe recipe;

    public IngredientAdapter(Context context, int idRowCustom, List list, Recipe recipe) {
        super(context, idRowCustom, list);
        this.recipe = recipe;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.custom_ingredient_item, null);
        TextView name = convertView.findViewById(R.id.ingredient_name);
        TextView quantity = convertView.findViewById(R.id.ingredient_qty);

        name.setText(recipe.getIngredients().get(position).getName());
        quantity.setText(recipe.getIngredients().get(position).getQuantity());

        return convertView;
    }
}
