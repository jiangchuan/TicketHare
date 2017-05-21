package io.chizi.tickethare.database;

import android.app.Activity;
import android.os.Bundle;

import io.chizi.tickethare.R;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public class FragmentLayout extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for fragment_layout.xml
        setContentView(R.layout.fragment_data);
    }

}
