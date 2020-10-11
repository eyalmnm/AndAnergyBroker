package com.usenergysolutions.energybroker.view.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import com.usenergysolutions.energybroker.R;
import com.usenergysolutions.energybroker.config.Dynamic;

import java.util.Objects;


public class AppExitDialog extends DialogFragment implements View.OnClickListener {

    private Button okButton;
    private Button cancelButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_app_exit, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setCancelable(false);

        okButton = view.findViewById(R.id.okButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (R.id.okButton == view.getId()) {
            Dynamic.Companion.setUuid(null);
            Objects.requireNonNull(getActivity()).finishAffinity();
            System.exit(0);
        }
        dismiss();
    }
}