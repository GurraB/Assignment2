package se.mah.af6589.assignment2;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by Gustaf Bohlin on 30/09/2017.
 */

public class AddGroupFragment extends DialogFragment {

    private View rootView;
    private ImageButton ibCancel, ibSave;
    private EditText etName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_group, container, false);
        initializeComponents();
        attachListeners();
        return rootView;
    }

    private void initializeComponents() {
        etName = (EditText) rootView.findViewById(R.id.et_group_name);
        ibCancel = (ImageButton) rootView.findViewById(R.id.ib_add_cancel);
        ibSave = (ImageButton) rootView.findViewById(R.id.ib_add_save);
    }

    private void attachListeners() {
        Listener listener = new Listener();
        ibCancel.setOnClickListener(listener);
        ibSave.setOnClickListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeComponents();
        attachListeners();
    }

    private class Listener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view == ibCancel) dismiss();
            if (view == ibSave) {
                try {
                    String groupName = etName.getText().toString();
                    Controller controller = ((MainActivity) getActivity()).getController();
                    controller.joinGroup(groupName);
                    dismiss();
                } catch (Exception e) {
                    Log.e("AddTransaction", "ERROR parsing data");
                }
            }
        }
    }
}
