package com.example.rfeng.worldepthfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class Blue_Fragment extends Fragment {
    private static final String TAG = "BlueFragment";

    private Button btnNavRedFrag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.blue_fragment, container, false);
        btnNavRedFrag = view.findViewById(R.id.btnNavRedFrag);
        Log.d(TAG, "onCreateView: started");

        btnNavRedFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Going to red fragment", Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).setmViewPager(1);
            }
        });
        return view;
    }
}
