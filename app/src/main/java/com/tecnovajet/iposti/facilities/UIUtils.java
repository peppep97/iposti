package com.tecnovajet.iposti.facilities;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.tecnovajet.iposti.expandablelist.AnimatedExpandableListView;

public class UIUtils {

    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static int setListViewHeightBasedOnItems(AnimatedExpandableListView listView, int n, int currentHeight, boolean add) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            View item = listAdapter.getView(0, null, listView);
            item.measure(0, 0);

            totalItemsHeight = n*item.getMeasuredHeight();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            if (add)
                currentHeight += totalItemsHeight;
            else
                currentHeight -= totalItemsHeight;

            params.height = currentHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return currentHeight;

        } else {
            return -1;
        }

    }
}