package com.jamesg.windforecast.SpotFragment;



import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.jamesg.windforecast.base.BaseFragment;
import com.jamesg.windforecast.R;
import com.jamesg.windforecast.base.BaseSpotFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpotWrapperFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SpotWrapperFragment extends BaseFragment {

    private String openSpot = "";

    private TextView today;
    private TextView tomorrow;
    private TextView sevenDay;
    private View underline;

    private BaseSpotFragment currentSpotFragment;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SpotWrapperFragment.
     */
    public static SpotWrapperFragment newInstance() {
        SpotWrapperFragment fragment = new SpotWrapperFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public SpotWrapperFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spots_frame, container, false);

        underline = view.findViewById(R.id.lineUnderline);
        today = (TextView) view.findViewById(R.id.todaySelector);
        tomorrow = (TextView) view.findViewById(R.id.tomorrowSelector);
        sevenDay = (TextView) view.findViewById(R.id.sevenDaySelector);

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDateTab(0);
            }
        });
        tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDateTab(1);
            }
        });
        sevenDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDateTab(2);
            }
        });

        int dateTab = mListener.getDateTab();

        if(dateTab == 0){
            today.setClickable(false);
        }else if(dateTab == 1){
            tomorrow.setClickable(false);
            final ViewTreeObserver vto = underline.getViewTreeObserver();
            try{
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    public void onGlobalLayout() {
                        Log.d("WINDFINDER APP", underline.getWidth() + " Underline Width - Display 0");
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
            sevenDay.setClickable(false);
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

        loadSpot("Aberavon");

        return view;
    }

    public void updateDateTab(int newDateTab){
        today.setClickable(false);
        tomorrow.setClickable(false);
        sevenDay.setClickable(false);

        int oldDateTab = mListener.getDateTab();

        if(newDateTab == 0){
            TranslateAnimation moveToLeft;
            if(oldDateTab == 1) moveToLeft = new TranslateAnimation(underline.getWidth(), 0, 0, 0);
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

        }else if(newDateTab == 1){
            TranslateAnimation moveToMiddle;
            if(oldDateTab == 0) moveToMiddle = new TranslateAnimation(0, underline.getWidth(), 0, 0);
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
        }else if(newDateTab == 2){
            TranslateAnimation moveToRight;
            if(oldDateTab == 0) moveToRight = new TranslateAnimation(0, underline.getWidth()*2, 0, 0);
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
        mListener.setDateTab(newDateTab);
        if(currentSpotFragment != null){
            currentSpotFragment.updateDateTab(newDateTab);
        }
    }

    public void loadSpot(String name){
        SpotFragment spot = SpotFragment.newInstance(name);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        //transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        //transaction.setCustomAnimations(R.anim.no_anim_in, R.anim.no_anim_out, R.anim.no_anim_in, R.anim.no_anim_out);

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.spots_frame, spot);
        // Commit the transaction
        transaction.commit();

        currentSpotFragment = spot;
        openSpot = name;

        mListener.loadSpot(name,0);
    }

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }
}
