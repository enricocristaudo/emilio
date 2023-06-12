package com.cristaudoenrico.emilio;

import java.util.LinkedList;

public class Recipe {
    private int id;
    private String name, description, type, img, preparation;
    private LinkedList<Ingredient> ingredients;
    private double rating;
    private boolean isSaved;

    public Recipe(int id, String name, String description, String preparation, String type, LinkedList<Ingredient> ingredients, double rating, String img) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.rating = rating;
        this.type = type;
        this.img = img;
        this.preparation = preparation;
        this.isSaved = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LinkedList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(LinkedList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public String getPreparation() {
        return preparation;
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }
}
