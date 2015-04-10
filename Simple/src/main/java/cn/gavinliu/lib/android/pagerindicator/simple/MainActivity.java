package cn.gavinliu.lib.android.pagerindicator.simple;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.gavinliu.lib.android.pagerindicator.DropsPagerIndicator;

public class MainActivity extends ActionBarActivity {

    private DropsPagerIndicator indicator;
    private ViewPager viewPager;

    private List<View> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.pager);
        indicator = (DropsPagerIndicator) findViewById(R.id.indicator);


        views = new ArrayList<>();
        views.add(getLayoutInflater().inflate(R.layout.layout_pager, null));
        views.add(getLayoutInflater().inflate(R.layout.layout_pager, null));
        views.add(getLayoutInflater().inflate(R.layout.layout_pager, null));
        views.add(getLayoutInflater().inflate(R.layout.layout_pager, null));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(0xFFFF0000);
        colors.add(0xFF00FF00);
        colors.add(0xFF0000FF);
        colors.add(0xFFFF00FF);
        indicator.setColors(colors);
        indicator.setPagerCount(views.size());

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                indicator.setPositionAndOffset(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(new PagerAdapter() {

            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(views.get(position));
            }


            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(views.get(position), 0);
                return views.get(position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
