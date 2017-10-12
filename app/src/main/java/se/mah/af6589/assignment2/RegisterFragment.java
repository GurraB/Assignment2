package se.mah.af6589.assignment2;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Date;

/**
 * Created by Gustaf Bohlin on 29/09/2017.
 */

public class RegisterFragment extends DialogFragment {

    private EditText etUsername;
    private View rootView;
    private Button btnRegister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(false);
        setCancelable(false);
        initializeComponents();
        attachListeners();
        return rootView;
    }

    private void initializeComponents() {
        etUsername = (EditText) rootView.findViewById(R.id.et_username);
        btnRegister = (Button) rootView.findViewById(R.id.btn_register);
    }

    private void attachListeners() {
        btnRegister.setOnClickListener(new Listener());
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeComponents();
        attachListeners();
    }

    public void notifyDataReceived(MainActivity activity, boolean loggedIn) {
        Log.v("REGISTERFRAGMENT", "NOTIFIED");
        if (loggedIn)
            dismiss(activity);
        else
            Toast.makeText(activity, "Username already in use", Toast.LENGTH_LONG).show();
    }

    public void displayErrorMessage(MainActivity activity, String errorMessage) {
        Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
    }

    public void dismiss(MainActivity activity) {
        activity.getController().getDataFragment().setRegisterFragmentIsShowing(false);
        super.dismiss();
    }

    private class Listener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            try {
                String username = etUsername.getText().toString();
                ((MainActivity)getActivity()).getController().logIn(username);
            } catch (Exception e) {
                Log.e("REGISTERFRAGMENT", "EXCEPTION");
                Log.e("REGISTERFRAGMENT", e.getMessage());
            }
        }
    }
}

