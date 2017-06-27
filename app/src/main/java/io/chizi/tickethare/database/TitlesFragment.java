package io.chizi.tickethare.database;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import io.chizi.tickethare.R;

import static io.chizi.tickethare.database.DBProvider.KEY_DATETIME;
import static io.chizi.tickethare.database.DBProvider.KEY_LICENSE_NUM;
import static io.chizi.tickethare.database.DBProvider.KEY_ROW_ID;
import static io.chizi.tickethare.database.DBProvider.TICKET_URL;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_CURR_INDEX;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_CURR_POS;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public class TitlesFragment extends ListFragment {
    ContentResolver resolver; // Provides access to other applications Content Providers

    private TextView emptyTextView;
    ArrayAdapter<String> connectArrayToListView;
    String[] ticketTitles;

    // True or False depending on if we are in horizontal or duel pane mode
    boolean mDuelPane;

    // Currently selected item in the ListView
    int mCurCheckPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_titles, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        resolver = getActivity().getContentResolver();
        updateListView();


        // Check if the FrameLayout with the id details exists
        View detailsFrame = getActivity().findViewById(R.id.details);

        // Set mDuelPane based on whether you are in the horizontal layout
        // Check if the detailsFrame exists and if it is visible
        mDuelPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        // If the screen is rotated onSaveInstanceState() below will store the // hero most recently selected. Get the value attached to curChoice and // store it in mCurCheckPosition
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt(SAVED_INSTANCE_CURR_POS, 0);
        }

        if (!isDatabaseEmpty()) {
            // Send the item selected to showDetails so the right hero info is shown
//                showDetails(mCurCheckPosition);
            clickOnListViewItem(mCurCheckPosition);

        }
    }

    public void clickOnListViewItem(final int mPosition) {
        if (mDuelPane) {
            final ListView mListView = getListView();
            // CHOICE_MODE_SINGLE allows one item in the ListView to be selected at a time
            // CHOICE_MODE_MULTIPLE allows multiple
            // CHOICE_MODE_NONE is the default and the item won't be highlighted in this case'
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mListView.performItemClick(
                            mListView.getChildAt(mPosition),
                            mPosition,
                            mListView.getAdapter().getItemId(mPosition));
                }
            });
        }
    }

    public void updateDB() {
        // An ArrayAdapter connects the array to our ListView
        // getActivity() returns a Context so we have the resources needed
        // We pass a default list item text view to put the data in and the
        // array
//        ticketTitles = Arrays.asList(getTicketTitles());
        ticketTitles = getTicketTitles();
        connectArrayToListView = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                ticketTitles);
        // Connect the ListView to our data
        setListAdapter(connectArrayToListView);
    }

    public void setEmptyTextView() {
        emptyTextView = (TextView) getActivity().findViewById(R.id.empty_database);
        if (isDatabaseEmpty()) {
            emptyTextView.setText(getString(R.string.empty_database));
        } else {
            emptyTextView.setText("");
        }
    }

    private Boolean isDatabaseEmpty() {
        Boolean result = false;
        String[] projection = new String[]{KEY_ROW_ID};
        if (resolver == null) {
            resolver = getActivity().getContentResolver();
        }
        Cursor cursor = resolver.query(TICKET_URL, projection, null, null, null);
        if (cursor.getCount() < 1) {
            result = true;
        }
        try {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        } catch (Exception ex) {

        }
        return result;
    }


    public void updateListView() {
        if (TICKET_URL != null) {
            setEmptyTextView();
            updateDB();
        }
    }

    private String[] getTicketTitles() {
        // Projection contains the columns we want
        String[] projection = new String[]{KEY_LICENSE_NUM, KEY_DATETIME};
        // Pass the URL, projection and I'll cover the other options below
        Cursor cursor = resolver.query(TICKET_URL, projection, null, null, null);

        String[] ticketTitlesArray = new String[cursor.getCount()];
        // Cycle through and display every row of data
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                String license = cursor.getString(cursor.getColumnIndex(KEY_LICENSE_NUM));
                String datetime = cursor.getString(cursor.getColumnIndex(KEY_DATETIME));
                ticketTitlesArray[i++] = license + ", " + datetime;
            } while (cursor.moveToNext());
        }

        try {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        } catch (Exception ex) {

        }

        return ticketTitlesArray;
    }

    // Called every time the screen orientation changes or Android kills an Activity
    // to conserve resources
    // We save the last item selected in the list here and attach it to the key curChoice
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_INSTANCE_CURR_POS, mCurCheckPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }

    private void showDetails(int index) {

        mCurCheckPosition = index;

        // Check if we are in horizontal mode and if yes show the ListView and the details
        if (mDuelPane) {
            // Make the currently selected item highlighted
            getListView().setItemChecked(index, true);

            // Create an object that represents the current FrameLayout that we will put the ticket in
            DetailsFragment details = (DetailsFragment)
                    getFragmentManager().findFragmentById(R.id.details);

            // When a DetailsFragment is created by calling newInstance the index for the data
            // it is supposed to show is passed to it. If that index hasn't been assigned we must
            // assign it in the if block
            if (details == null || details.getShownIndex() != index) {
                // Make the details fragment and give it the currently selected hero index
                details = DetailsFragment.newInstance(index);

                // Start Fragment transactions
                FragmentTransaction ft = getFragmentManager().beginTransaction();

                // Replace any other Fragment with our new Details Fragment with the right data
                ft.replace(R.id.details, details);

                // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
            // Launch a new Activity to show our DetailsFragment
            Intent intent = new Intent();

            // Define the class Activity to call
            intent.setClass(getActivity(), DetailsActivity.class);

            // Pass along the currently selected index assigned to the keyword index
            intent.putExtra(SAVED_INSTANCE_CURR_INDEX, index);

            // Call for the Activity to open
            startActivity(intent);
        }
    }
}
