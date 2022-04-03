package com.cst2335.recipe;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_activity);
        //get bundle from previous activity
        Bundle bundle=getIntent().getBundleExtra("FavouriteRecipe");
        DetailsFragment fragment=new DetailsFragment();
        fragment.setArguments(bundle);
        //go to fragment
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.setReorderingAllowed(true);
        ft.replace(R.id.flbox1,fragment);
        ft.commit();
    }
}
