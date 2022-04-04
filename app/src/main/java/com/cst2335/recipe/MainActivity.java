package com.cst2335.recipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "RecipeSearchActivity";//print log
    //MyOpenHelper myOpener;

    private EditText searchEditText;
    private String app_id = "d0ea21e0", app_key = "551ca2a90e34d9d00522b6af20718851";
    private ArrayList<Recipe> recipes = new ArrayList<>();

    private String jsonUrl = " https://api.edamam.com/api/recipes/v2?type=public&q=";

    //constructor Messages
    public static class Recipe {
        long id;
        private String ingredient;
        private String title;
        private String url;
        //public Boolean isFavorite;


        public Recipe(String ingredient, String title,String url, long _id) {
            this.ingredient = ingredient;
            this.title = title;
            this.url = url;
            //this.isFavorite = isFavorite;
            this.id = _id;
        }

        public Recipe(String ingredient, String title,String url) {
            this.ingredient = ingredient;
            this.title = title;
            this.url = url;
            //this.isFavorite = isFavorite;
            //this.id = _id;
        }
        public long getId() {return id;}
        public String getIngredient() {return ingredient;}
        public void setIngredient(String ingredient){this.ingredient=ingredient;};
        public String getTitle() {return title;}
        public void setTitle(String title){this.title=title;};
        public String getUrl() {return url;}
        public void setUrl(String url){this.url=url;};
        //public Boolean getIsFavorite() {return isFavorite;}
        //public void setIsFavorite(String url){this.url=url;};
    }
    //ArrayList<Recipe> recipes = new ArrayList<>();

    RecipeJsonAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_activity_search);

        ListView myList = findViewById(R.id.searchResult);

        EditText searchEditText = findViewById(R.id.searchEditTxt);
        Button searchButton = findViewById(R.id.btn_search);
        searchButton.setOnClickListener( click -> {
            String inputText = searchEditText.getText().toString();
            RecipeJsonData apiIn = new RecipeJsonData();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    recipes = apiIn.getJsonData(jsonUrl + inputText + "&app_id=" + app_id + "&app_key=" + app_key);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myList.setAdapter(myAdapter = new RecipeJsonAdapter(MainActivity.this,recipes));
                        }
                    });
                }
            }).start();

        });

            myList.setOnItemClickListener((adapterView, view, position, id) -> {

            Bundle bundle=new Bundle();
            String ingredient=recipes.get(position).ingredient;
            String title = recipes.get(position).title;
            String url = recipes.get(position).url;
            bundle.putString("Ingredient",ingredient);
            bundle.putString("title",title);
            bundle.putString("url",url);
            Intent intent=new Intent(MainActivity.this,EmptyActivity.class);
            intent.putExtra("FavouriteRecipe",bundle);
            startActivity(intent);
            });

        Toolbar recipeToolbar =  findViewById(R.id.recipe_toolbar);
        setSupportActionBar(recipeToolbar);

//For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, recipeToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * multiple menu items for switching
     *
     * @param item Menuitem
     * @return booolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.help:
                android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle(getString(R.string.dialogboxTitle));
                alertDialog.setMessage(getString(R.string.author_full) + "\n" +
                        getString(R.string.version) + "\n" + getString(R.string.instruction));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
       String message = null;
        switch(item.getItemId())
        {
            case R.id.title:
                message = "Final Project：Recipe";
                break;
            case R.id.author:
                message = "Qin Li / Jin Zhang / Meiping Chen";
                break;
            case R.id.version:
                message = "Version Number: 1";
                break;
        }
        if ( message != null ) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    //MyOpenHelper myOpener = new MyOpenHelper( this );

    public class RecipeJsonAdapter extends BaseAdapter {

        private ArrayList<Recipe> data;
        private LayoutInflater inflater;
        public String ingredientData;
        public String titleData;
        public String urlData;

        /**
         * constructor for instantiation
         *
         * @param context Context
         * @param data    List<NutritionNewBean>
         */
        public RecipeJsonAdapter(Context context, ArrayList<Recipe> data) {
            super();
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        /**
         * to get the size of the List
         *
         * @return size of the List
         */
        @Override
        public int getCount() {
            return data.size();
        }

        /**
         * to get the item of a specific position
         *
         * @param position int
         * @return the specific item
         */
        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        /**
         * to get the ID of a specific item
         *
         * @param position int
         * @return the position
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * to set up the view
         *
         * @param position    int
         * @param convertView View
         * @param parent      ViewGroup
         * @return the convertView
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.recipe_activity_listview_item, null);
                viewHolder.ingredient = (TextView) convertView.findViewById(R.id.ingredient);
                viewHolder.title = (TextView) convertView
                        .findViewById(R.id.title);
                viewHolder.url = (TextView) convertView
                        .findViewById(R.id.url);
                viewHolder.like = (Button) convertView.findViewById(R.id.favButton);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ingredientData = data.get(position).getIngredient();
            titleData = data.get(position).getTitle();
            urlData = data.get(position).getUrl();
            //String calString = Double.toString(titleData);
            //String fatString = Double.toString(urlData);
            viewHolder.ingredient.setText("Ingredient: " + ingredientData);
            viewHolder.title.setText("Title: " + titleData);
            viewHolder.url.setText("URL: " + urlData);


            //Button likeButton = convertView.findViewById(R.id.favButton);
            //likeButton.setOnClickListener(view -> savetoDatabase(position));
            return convertView;
        }



        };

        /**
         * inner class to hold the view of detailed searched result
         */
        class ViewHolder {
            public TextView ingredient, title, url;
            public Button like;

        }


    public class RecipeJsonData {

        //private String app_id = "d0ea21e0", app_key = "551ca2a90e34d9d00522b6af20718851";
        //public  String recipe;
        //private String jsonUrl = " https://api.edamam.com/api/recipes/v2?type=public&q=" + recipe + "&app_id=" + app_id + "&app_key=" + app_key;

        /**
         * to get the data from the Json Object
         *
         * @param jsonUrl the URL for the connection
         * @return the data of the Json Object
         */
        public ArrayList<Recipe> getJsonData(String jsonUrl) {
            ArrayList<Recipe> searchData = new ArrayList<>();

            try {

                URL httpUrl = new URL(jsonUrl);

                HttpURLConnection connection = (HttpURLConnection) httpUrl
                        .openConnection();

                connection.setReadTimeout(5000);
                connection.setRequestMethod("GET");

                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuffer sb = new StringBuffer();
                String ln = "";

                while ((ln = bufferedReader.readLine()) != null) {

                    sb.append(ln);
                }
                Log.e("TAG", "" + sb.toString());
                JSONObject response = new JSONObject(sb.toString());
                JSONArray hits = response.getJSONArray("hits");
                for (int i = 0; i < hits.length(); i++) {
                    JSONObject hit = hits.getJSONObject(i);
                    JSONObject recipe = hit.getJSONObject("recipe");
                    JSONArray jsonArray = recipe.getJSONArray("ingredientLines");

                    String ingredient = jsonArray.toString();
                    ingredient = ingredient.replace("[", "");
                    ingredient = ingredient.replace("]", "");
                    ingredient = ingredient.replace('"', ' ');

                    String title = recipe.getString("label");
                    String url = recipe.getString("url");
                    Recipe newRecipe = new Recipe(ingredient, title, url);
                    //newRecipe.setIngredient(ingredient);
                    //newRecipe.setTitle(title);
                    //newRecipe.setUrl(url);
                    searchData.add(newRecipe);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return searchData;
        }

    }

}