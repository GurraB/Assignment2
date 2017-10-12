package se.mah.af6589.assignment2;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Gustaf Bohlin on 30/09/2017.
 */

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spacing;

    public SpacingItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = spacing;
        outRect.right = spacing;
        outRect.bottom = spacing;

        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = spacing;
        } else {
            outRect.top = 0;
        }
    }
}
