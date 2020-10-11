package com.usenergysolutions.energybroker.view.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import com.usenergysolutions.energybroker.R;
import com.usenergysolutions.energybroker.config.Constants;

public class ProgressDialog extends DialogFragment {
    private static final String TAG = "ProgressDialog";

    // UI Components
    private TextView messageTextView;

    // Static Factory
    public static ProgressDialog newInstance(String data) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.APP_DATA, data);
        ProgressDialog fragment = new ProgressDialog();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        return inflater.inflate(R.layout.dialog_progress, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind UI Components
        messageTextView = view.findViewById(R.id.messageTextView);

        // Set Data
        Bundle args = getArguments();
        String msg = "Loading...";
        if (args != null) {
            msg = args.getString(Constants.APP_DATA, "Loading...");
        }
        messageTextView.setText(msg);
    }
}
