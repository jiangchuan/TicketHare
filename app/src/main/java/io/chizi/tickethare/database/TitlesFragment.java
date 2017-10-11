package io.chizi.tickethare.database;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
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
import static io.chizi.tickethare.database.DBProvider.KEY_TICKET_ID;
import static io.chizi.tickethare.database.DBProvider.KEY_TIME_MILIS;
import static io.chizi.tickethare.database.DBProvider.KEY_USER_ID;
import static io.chizi.tickethare.database.DBProvider.TICKET_URL;
import static io.chizi.tickethare.util.AppConstants.POLICE_USER_ID;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_CURR_INDEX;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_CURR_POS;
import static io.chizi.tickethare.util.AppConstants.SAVED_INSTANCE_USER_ID;
import static io.chizi.tickethare.util.AppConstants.TITLES_FRAGMENT_TICKET_ID;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public class TitlesFragment extends ListFragment {
    ContentResolver resolver; // Provides access to other applications Content Providers

    private String userID;

    private TextView emptyTextView;
    ArrayAdapter<String> connectArrayToListView;
    private String[] ticketTitles;
    private long[] ticketIDs;

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

        if (savedInstanceState != null) {
            userID = savedInstanceState.getString(SAVED_INSTANCE_USER_ID);
            mCurCheckPosition = savedInstanceState.getInt(SAVED_INSTANCE_CURR_POS, 0);
        }
        Intent intentFrom = getActivity().getIntent(); // Get the Intent that called for this Activity to open
        userID = intentFrom.getExtras().getString(POLICE_USER_ID); // Get the data that was sent

        resolver = getActivity().getContentResolver();
        updateListView();


        // Check if the FrameLayout with the id details exists
        View detailsFrame = getActivity().findViewById(R.id.details);

        // Set mDuelPane based on whether you are in the horizontal layout
        // Check if the detailsFrame exists and if it is visible
        mDuelPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (!isDatabaseEmpty()) {
            clickOnListViewItem(mCurCheckPosition);
        }
    }

    public void clickOnListViewItem(final int mPosition) {
        if (mDuelPane) {
            final ListView mListView = getListView();
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
        getTicketTitles();
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

    private boolean isDatabaseEmpty() {
        boolean result = false;
        String[] projection = new String[]{KEY_ROW_ID};
        if (resolver == null) {
            resolver = getActivity().getContentResolver();
        }
        Cursor cursor = resolver.query(TICKET_URL, projection, KEY_USER_ID + "=?", new String[]{userID}, null);
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

    private void getTicketTitles() {
        // Projection contains the columns we want
        String[] projection = new String[]{KEY_LICENSE_NUM, KEY_DATETIME, KEY_TICKET_ID};
        // Pass the URL, projection and I'll cover the other options below
        Cursor cursor = resolver.query(TICKET_URL, projection, KEY_USER_ID + "=?", new String[]{userID}, KEY_TIME_MILIS + " DESC");

        int numTickets = cursor.getCount();
        ticketTitles = new String[numTickets];
        ticketIDs = new long[numTickets];
        // Cycle through and display every row of data
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                String license = cursor.getString(cursor.getColumnIndex(KEY_LICENSE_NUM));
                String datetime = cursor.getString(cursor.getColumnIndex(KEY_DATETIME));
                ticketTitles[i] = license + ", " + datetime;
                ticketIDs[i] = cursor.getLong(cursor.getColumnIndex(KEY_TICKET_ID));
                i++;
            } while (cursor.moveToNext());
        }

        try {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        } catch (Exception ex) {

        }
    }

    // Called every time the screen orientation changes or Android kills an Activity
    // to conserve resources
    // We save the last item selected in the list here and attach it to the key curChoice
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_INSTANCE_USER_ID, userID);
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
//            if (details == null || details.getShownIndex() != index) {
                // Make the details fragment and give it the currently selected hero index
                details = DetailsFragment.newInstance(index, ticketIDs[index]);

                // Start Fragment transactions
                FragmentTransaction ft = getFragmentManager().beginTransaction();

                // Replace any other Fragment with our new Details Fragment with the right data
                ft.replace(R.id.details, details);

                // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
//            }

        } else {
            // Launch a new Activity to show our DetailsFragment
            Intent intent = new Intent();

            // Define the class Activity to call
            intent.setClass(getActivity(), DetailsActivity.class);

            // Pass along the currently selected index assigned to the keyword index
            intent.putExtra(SAVED_INSTANCE_CURR_INDEX, index);
            intent.putExtra(TITLES_FRAGMENT_TICKET_ID, ticketIDs[index]);

            // Call for the Activity to open
            startActivity(intent);
        }
    }

}
