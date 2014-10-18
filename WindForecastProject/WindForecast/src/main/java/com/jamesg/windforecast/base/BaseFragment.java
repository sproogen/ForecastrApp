package com.jamesg.windforecast.base;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BaseFragment.BaseFragmentInteractionListener} interface
 * to handle interaction events.
 *
 */
public class BaseFragment extends Fragment {

    public BaseFragmentInteractionListener mListener;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (BaseFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface BaseFragmentInteractionListener {
        public int getDateTab();
        public void setDateTab(int dateTab);
        public void loadSpot(String name, int listClick);
    }
}
