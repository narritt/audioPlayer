package com.example.narritt.audioplayer.prefFragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.narritt.audioplayer.R;

import java.util.ArrayList;

public class PrefMenu extends Fragment {
    ListView menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pref_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        menu = getView().findViewById(R.id.prefmenu);

        ArrayList<String> menuItems = new ArrayList<>();
        menuItems.add(getActivity().getString(R.string.pref_equalizer));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menuItems);
        menu.setAdapter(adapter);
    }
}
