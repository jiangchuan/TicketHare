package io.chizi.tickethare.database;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.chizi.tickethare.R;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public class DatabaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // Create an object that represents the current FrameLayout that we will
        // put the hero data in
        TitlesFragment titlesFragment = (TitlesFragment)
                getFragmentManager().findFragmentById(R.id.titles);

        // When a DetailsFragment is created by calling newInstance the index for the data
        // it is supposed to show is passed to it. If that index hasn't been assigned we must
        // assign it in the if block
        if (titlesFragment == null) {

            // Make the details fragment and give it the currently selected hero index
            titlesFragment = new TitlesFragment();

            // Start Fragment transactions
            FragmentTransaction ft = getFragmentManager().beginTransaction();

            // Replace any other Fragment with our new Details Fragment with the right data
            ft.replace(R.id.titles, titlesFragment);

            // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }

    }

}
