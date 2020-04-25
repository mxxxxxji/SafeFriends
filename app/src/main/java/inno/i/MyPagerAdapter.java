package inno.i;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import inno.i.Fragment.AttendFragment;
import inno.i.Fragment.SeatingFragment;
import inno.i.Fragment.SecondFragment;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    private int NUM_ITEMS = 3;
    GlobalApplication myApp = new GlobalApplication();
    String next_location;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    public MyPagerAdapter(FragmentManager fm, int numOfTabs){
        super(fm);
        this.NUM_ITEMS = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        next_location=myApp.getNextlocation();
        Log.e("-------MyAdapter", "--------");
        Log.e("next_location", next_location);
        switch (position) {
            case 0:
                return new AttendFragment();
            case 1:
                return new SecondFragment().newInstance(next_location);
            case 2:
                return new SeatingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
