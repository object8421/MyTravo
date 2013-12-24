package com.cobra.mytravo.fragments;



import com.cobra.mytravo.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FakeFragment extends BaseFragment{
	private static final String ARG_SECTION_NUMBER_STRING = "section_number";
	private int sectionNumber;
	public FakeFragment(){
		
	}
	public static FakeFragment newInstance(int position){
		FakeFragment fragment = new FakeFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER_STRING, position);
		fragment.setArguments(args);
		return fragment;
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER_STRING);
        View rootView = inflater.inflate(R.layout.fragment_fake, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.login_register);
        textView.setText("this is fragment "+Integer.toString(sectionNumber));
        return rootView;
    }
}
