package se.mah.af6589.assignment2;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Gustaf Bohlin on 29/09/2017.
 */

public class UserInformationFragment extends BottomSheetDialogFragment {

    private View rootView;
    private TextView tvUserName, tvLongitude, tvLatitude;
    private LinearLayout llParent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_information, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setDimAmount(0);
        return rootView;
    }

    private void initializeComponents() {
        llParent = (LinearLayout) rootView.findViewById(R.id.ll_parent_user_information);
        tvUserName = (TextView) rootView.findViewById(R.id.tv_user_information_title);
        tvLatitude = (TextView) rootView.findViewById(R.id.tv_latitude);
        tvLongitude = (TextView) rootView.findViewById(R.id.tv_longitude);
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeComponents();
        ((MainActivity) getActivity()).getController().updateUserInformationFragmentContent(this);
    }

    public void updateContent(String username, int usercolor, String latitude, String longitude) {
        llParent.setBackgroundColor(usercolor);
        tvUserName.setText(username);
        tvLatitude.setText(latitude);
        tvLongitude.setText(longitude);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.v("USERINFORMATIONFRAGMENT", "dismissed");
        super.onDismiss(dialog);
    }
}
