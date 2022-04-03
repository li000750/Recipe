package com.cst2335.recipe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "RecipeSearchActivity";//print log
    //MyOpenHelper myOpener;
    SQLiteDatabase theDatabase;
    private EditText searchEditText;
    private String app_id = "d0ea21e0", app_key = "551ca2a90e34d9d00522b6af20718851";
    private List<Recipe> recipes = new ArrayList<>();

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

    //public static boolean isTablet;
    //public static FragmentManager fragmentManager;
    //DetailsFragment tFragment = new DetailsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_activity_search);
        //isTablet = findViewById(R.id.frameLayout) != null;


        //attach listview to adapter
        ListView myList = findViewById(R.id.searchResult);
        //ListAdapter myAdapter = new Adapter();
        //myList.setAdapter(myAdapter);
        //myList.setAdapter(myAdapter = new RecipeJsonAdapter(context, data));
        EditText searchEditText = findViewById(R.id.searchEditTxt);
        Button searchButton = findViewById(R.id.btn_search);
        searchButton.setOnClickListener( click -> {
            String inputText = searchEditText.getText().toString();
            RecipeJsonData apiIn = new RecipeJsonData();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final ArrayList<Recipe> data = apiIn.getJsonData(jsonUrl + inputText + "&app_id=" + app_id + "&app_key=" + app_key);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myList.setAdapter(myAdapter = new RecipeJsonAdapter(MainActivity.this,data));
                        }
                    });
                }
            }).start();
        });
    }

    MyOpenHelper myOpener = new MyOpenHelper( this );

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
            viewHolder.title.setText("Title: " + urlData);
            viewHolder.url.setText("URL: " + titleData);


            Button likeButton = convertView.findViewById(R.id.favButton);
            likeButton.setOnClickListener(view -> {
                savetoDatabase(position);

                DetailsFragment fragment = new DetailsFragment();
                Bundle bundle = new Bundle();
                String ingredient = myAdapter.ingredientData;
                String title = myAdapter.titleData;
                String url = myAdapter.urlData;
                bundle.putString("ingredient", ingredient);
                bundle.putString("title", title);
                bundle.putString("url", url);
                Intent intent = new Intent(MainActivity.this, EmptyActivity.class);
                intent.putExtra("FavouriteRecipe", bundle);

            });
            return convertView;}


        public void savetoDatabase(int position){
            Log.d(TAG, "Writing to Database");

            //myOpener = new MyOpenHelper( this );
            theDatabase = myOpener.getWritableDatabase();
            Cursor results = theDatabase.rawQuery( "Select * from " + MyOpenHelper.TABLE_NAME + ";", null );
            int idIndex = results.getColumnIndex( MyOpenHelper.KEY_ID );
            int ingredientIndex = results.getColumnIndex( MyOpenHelper.COL_ingredient);
            int titleIndex = results.getColumnIndex( MyOpenHelper.COL_title);
            int urlIndex = results.getColumnIndex(MyOpenHelper.COL_url);

            //cursor is pointing to row -1
            while( results.moveToNext() ) //returns false if no more data
            { //pointing to row 2
                int id = results.getInt(idIndex);
                String ingredient = results.getString( ingredientIndex );
                String title = results.getString( titleIndex );
                String url = results.getString( urlIndex );
                //boolean isFavorite = results.getInt(sOrRIndex)==1;

                //add to arrayList:
                recipes.add( new Recipe( ingredient,title, url,id ));
            }
        };
        }
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