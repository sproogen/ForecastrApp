package com.jamesg.windforecast;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.jamesg.windforecast.SpotFragment.SpotFragment;

public class MainContentFragment extends Fragment {

    private FavouriteSpotsFragment favouriteSpotsFragment;

    private String openSpot = "";
    private int overviewDisplay = 0;

    public MainContentFragment() {
    }

    public MainContentFragment(int overviewDisplay) {
        this.overviewDisplay = overviewDisplay;
    }

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spots_frame, container, false);

        if (savedInstanceState != null) {
            this.overviewDisplay = savedInstanceState.getInt("overviewDisplay");
            favouriteSpotsFragment = (FavouriteSpotsFragment) getActivity().getSupportFragmentManager().findFragmentByTag("favouriteSpotsFragment");
        }else{
            favouriteSpotsFragment = new FavouriteSpotsFragment(overviewDisplay);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.spots_frame, favouriteSpotsFragment,"favouriteSpotsFragment")
                    .commit();
        }

        if(overviewDisplay == 0){
            TextView today = (TextView) rootView.findViewById(R.id.todaySelector);
            today.setClickable(false);
        }else if(overviewDisplay == 1){
            TextView tomorrow = (TextView) rootView.findViewById(R.id.tomorrowSelector);
            tomorrow.setClickable(false);
            final View underline = (View) rootView.findViewById(R.id.lineUnderline);
            final ViewTreeObserver vto = underline.getViewTreeObserver();
            try{
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    public void onGlobalLayout() {
                        Log.d("WINDFINDER APP", underline.getWidth()+" Underline Width - Display 0");
                        TranslateAnimation moveTo = new TranslateAnimation(0, underline.getWidth(), 0, 0);
                        moveTo.setDuration(0);
                        moveTo.setFillAfter(true);
                        underline.startAnimation(moveTo);
                        removeOnGlobalLayoutListener(underline,this);
                    }
                });
            }catch(NullPointerException e){
                Log.e("WINDFINDER APP",e.toString());
            }
        }else{
            TextView sevenDay = (TextView) rootView.findViewById(R.id.sevenDaySelector);
            sevenDay.setClickable(false);
            final View underline = (View) rootView.findViewById(R.id.lineUnderline);
            final ViewTreeObserver vto = underline.getViewTreeObserver();
            try{
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    public void onGlobalLayout() {
                        Log.d("WINDFINDER APP", underline.getWidth()+" Underline Width - Display 0");
                        TranslateAnimation moveTo = new TranslateAnimation(0, underline.getWidth()*2, 0, 0);
                        moveTo.setDuration(0);
                        moveTo.setFillAfter(true);
                        underline.startAnimation(moveTo);
                        removeOnGlobalLayoutListener(underline,this);
                    }
                });
            }catch(NullPointerException e){
                Log.e("WINDFINDER APP",e.toString());
            }
        }

        return rootView;
    }

    public void updateOverviewDisplay(int overviewDisplay){
        View underline = (View) getView().findViewById(R.id.lineUnderline);
        final TextView today = (TextView) getView().findViewById(R.id.todaySelector);
        final TextView tomorrow = (TextView) getView().findViewById(R.id.tomorrowSelector);
        final TextView sevenDay = (TextView) getView().findViewById(R.id.sevenDaySelector);
        today.setClickable(false);
        tomorrow.setClickable(false);
        sevenDay.setClickable(false);

        if(overviewDisplay == 0){
            TranslateAnimation moveToLeft;
            if(this.overviewDisplay == 1) moveToLeft = new TranslateAnimation(underline.getWidth(), 0, 0, 0);
            else moveToLeft = new TranslateAnimation(underline.getWidth()*2, 0, 0, 0);
            moveToLeft.setDuration(400);
            moveToLeft.setFillAfter(true);
            moveToLeft.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    tomorrow.setClickable(true);
                    sevenDay.setClickable(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            underline.startAnimation(moveToLeft);

        }else if(overviewDisplay == 1){
            TranslateAnimation moveToMiddle;
            if(this.overviewDisplay == 0) moveToMiddle = new TranslateAnimation(0, underline.getWidth(), 0, 0);
            else moveToMiddle = new TranslateAnimation(underline.getWidth()*2, underline.getWidth(), 0, 0);
            moveToMiddle.setDuration(400);
            moveToMiddle.setFillAfter(true);
            moveToMiddle.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    today.setClickable(true);
                    sevenDay.setClickable(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            underline.startAnimation(moveToMiddle);
        }else if(overviewDisplay == 2){
            TranslateAnimation moveToRight;
            if(this.overviewDisplay == 0) moveToRight = new TranslateAnimation(0, underline.getWidth()*2, 0, 0);
            else moveToRight = new TranslateAnimation(underline.getWidth(), underline.getWidth()*2, 0, 0);
            moveToRight.setDuration(400);
            moveToRight.setFillAfter(true);
            moveToRight.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    today.setClickable(true);
                    tomorrow.setClickable(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            underline.startAnimation(moveToRight);
        }
        this.overviewDisplay = overviewDisplay;
        favouriteSpotsFragment.updateOverviewDisplay(overviewDisplay);
        FavouriteSpotsFragment currentFragment = (FavouriteSpotsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.spots_frame);
        currentFragment.updateOverviewDisplay(overviewDisplay);
    }

    public void closeSpot(boolean animate){
        openSpot = "";
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        if(animate)transaction.setCustomAnimations(R.anim.close_enter, R.anim.close_exit);
        transaction.replace(R.id.spots_frame, favouriteSpotsFragment,"favouriteSpotsFragment").commit();

    }

    public void removeSpot(String name){
        favouriteSpotsFragment.removeSpot(name);
    }

    public void addSpot(String name){
        favouriteSpotsFragment.addSpot(name);
    }

    public void loadSpot(String name,int animate, int type){ //0 == Favourite , 1 == Search
        SpotFragment spot = SpotFragment.newInstance(name);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        if(animate == 0){
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        }
        //transaction.setCustomAnimations(R.anim.no_anim_in, R.anim.no_anim_out, R.anim.no_anim_in, R.anim.no_anim_out);

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.spots_frame, spot);
        // Commit the transaction
        transaction.commit();
        getActivity().getSupportFragmentManager().executePendingTransactions();

        /*FavouriteSpotsFragment spot = new FavouriteSpotsFragment(overviewDisplay, name, type);*/

        if(animate == 1){
            if(getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).toggle();
            }
            /*Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    if(getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).toggle();
                    }
                }
            }, 200);*/
        }
        openSpot = name;
    }

    public void updateSpot(String name){
        favouriteSpotsFragment.updateSpot(name);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("overviewDisplay", overviewDisplay);
    }
}