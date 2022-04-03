package com.cst2335.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
public class DetailsFragment extends Fragment {
    private Bundle bundle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recipe_fragment,container, false);
        TextView ingredient = view.findViewById(R.id.ingredient2);
        TextView title = view.findViewById(R.id.title2);
        TextView url = view.findViewById(R.id.url2);
        Button hide = view.findViewById(R.id.hide);
        bundle = getArguments();
        //fetch the message, id, and boolean info back from bundle
        String ingredient1 = bundle.getString("Ingredient");
        String title1 = bundle.getString("title");
        String url1 = bundle.getString("url");
        //locate message, id
        ingredient.setText(ingredient1);
        title.setText(title1);
        url.setText(url1);

        //hide button function
        hide.setOnClickListener(view1 -> {
            getActivity().finish();
        });
        return view;
    }
}
