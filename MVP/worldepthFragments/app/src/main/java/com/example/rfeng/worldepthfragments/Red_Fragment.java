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
public class Red_Fragment extends Fragment {
    private static final String TAG = "BlueFragment";

    private Button btnNavBlueFrag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.red_fragment, container, false);
        btnNavBlueFrag = view.findViewById(R.id.btnNavBlueFrag);
        Log.d(TAG, "onCreateView: started");

        btnNavBlueFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Going to blue fragment", Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).setmViewPager(0);
            }
        });
        return view;
    }
}
