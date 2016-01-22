package com.test.testproject;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comcast.freeflow.animations.DefaultLayoutAnimator;
import com.comcast.freeflow.core.FreeFlowContainer;
import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.core.Section;
import com.comcast.freeflow.core.SectionedAdapter;


import java.util.ArrayList;

public class MainActivity extends Activity {

    float lastYPosition = Float.MIN_VALUE;
    float lastDist = 0f;


    private FreeFlowContainer container = null;
    private SocialLayout sLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout r = (RelativeLayout)findViewById(R.id.headerLayout);

        Configuration configuration = getResources().getConfiguration();
        int screenHeightDp = configuration.screenHeightDp;


        final int defaultHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        final int fullScreenHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, screenHeightDp, getResources().getDisplayMetrics());

        r.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent event) {


                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
                    if (lastYPosition != Float.MIN_VALUE) {
                        //calculate the distance between last bottom layout position and the new Y (by rawY event)
                        lastDist = event.getRawY() - lastYPosition;

                        ViewGroup.LayoutParams lp = view.getLayoutParams();
                        lp.height += lastDist;
                        view.setLayoutParams(lp);

                        view.requestLayout();

                    }
                    //set the last y of the event
                    lastYPosition = event.getRawY();
                }
                else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    // to prevent when user touch only without gesture the top bar
                    if(lastYPosition != Float.MIN_VALUE) {
                        ViewGroup.LayoutParams lp = view.getLayoutParams();

                        // when lastDist is negative number , it's mean tat gesture is up
                        lp.height = lastDist < 0 ? defaultHeight : fullScreenHeight;

                        view.setLayoutParams(lp);
                        view.requestLayout();

                        // init to default values for next touch
                        lastDist = 0f;
                        lastYPosition = Float.MIN_VALUE;
                    }
                }

                return true;
            }
        });

        FreeFlowContainer container = (FreeFlowContainer) findViewById(R.id.container);

        final ImageAdapter adapter = new ImageAdapter();

        DefaultLayoutAnimator anim = (DefaultLayoutAnimator) container.getLayoutAnimator();
        anim.animateAllSetsSequentially = true;
        anim.animateIndividualCellsSequentially = true;

        container.requestFocus();

        sLayout = new SocialLayout();

        container.setAdapter(adapter);
        container.setLayout(sLayout);
    }

    static class ViewHolder {
       ImageView imageView;
    }

    /*
    This class is Implement of SectionedAdapter. the content is static images
     */
    class ImageAdapter implements SectionedAdapter {
        ArrayList<RectImage> rectsArray = new ArrayList<>();

        int [] imagesIds = {R.drawable.facebook_icon,R.drawable.github_icon,R.drawable.google_plus_icon,R.drawable.instagram_icon
        ,R.drawable.youtube_icon,R.drawable.linkedin_icon,R.drawable.facebook_icon
        ,R.drawable.github_icon,R.drawable.linkedin_icon,R.drawable.google_plus_icon
        ,R.drawable.youtube_icon,R.drawable.instagram_icon,R.drawable.facebook_icon};


        private ArrayList<Section> sections = new ArrayList<Section>();



        public ImageAdapter() {
            //init the array image  positions
            rectsArray.add(new RectImage(0,0,0));
            rectsArray.add(new RectImage(1,2,0));
            rectsArray.add(new RectImage(2,0,1));
            rectsArray.add(new RectImage(3,1,1));
            rectsArray.add(new RectImage(4,1,1));
            rectsArray.add(new RectImage(5,2,1));
            rectsArray.add(new RectImage(6,2,1));
            rectsArray.add(new RectImage(7,0,2));
            rectsArray.add(new RectImage(8,0,2));
            rectsArray.add(new RectImage(9,1,2));
            rectsArray.add(new RectImage(10,1,2));
            rectsArray.add(new RectImage(11,2,2));
            rectsArray.add(new RectImage(12,2,2));

            Section s = new Section();

            for (int index = 0; index < 13; index++) {
                s.addItem(rectsArray.get(index));
            }

            sections.add(s);
        }
        @Override
        public long getItemId(int section, int position) {
            return section * 1000 + position;
        }

        @Override
        public View getItemView(int section, int position, View convertView, ViewGroup parent) {
            ImageView imageView = null;
            if (convertView != null) {
                imageView = ((ImageView) convertView);

                //without this line - the view replace every time that top bar up the drawables - maybe because dont inflate any view (tired to figure why..)
                imageView.setImageDrawable(getDrawbleSDK(imagesIds[position]));
            } else {
                imageView = new ImageView(getApplicationContext());
                Drawable myDrawable = getDrawbleSDK(imagesIds[position]);
                imageView.setImageDrawable(myDrawable);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(5, 5, 5, 5);
            }


            return imageView;
        }

        private Drawable getDrawbleSDK(int id){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                return getResources().getDrawable(id,getTheme());
            }else{
                return getResources().getDrawable(id);
            }
        }

        @Override
        public View getHeaderViewForSection(int section, View convertView, ViewGroup parent) {
            TextView tv = null;
            if (convertView != null) {
                // Log.d(TAG, "Convert view not null");
                tv = (TextView) convertView;
            } else {
                tv = new TextView(MainActivity.this);
            }

            tv.setFocusable(false);
            tv.setText("");

            return tv;
        }

        @Override
        public int getNumberOfSections() {
            return sections.size();
        }

        @Override
        public Section getSection(int index) {
            if (index < sections.size() && index >= 0)
                return sections.get(index);

            return null;
        }

        @Override
        public Class[] getViewTypes() {
            Class[] types = { TextView.class, ImageView.class };

            return types;
        }

        @Override
        public Class getViewType(FreeFlowItem proxy) {
            return ImageView.class;
        }

        @Override
        public boolean shouldDisplaySectionHeaders() {
            return false;
        }

    }
}
