package se.mah.af6589.assignment2;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Gustaf Bohlin on 04/10/2017.
 */

public class ImageFragment extends DialogFragment {

    private View rootView;
    private ImageView ivImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_image, container, false);
        initializeComponents();
        return rootView;
    }

    private void initializeComponents() {
        ivImage = rootView.findViewById(R.id.iv_image);
    }

    @Override
    public void onResume() {
        super.onResume();
        ivImage.setImageBitmap(((MainActivity) getActivity()).getController().getDataFragment().getBitmapToDisplay());
    }
}
