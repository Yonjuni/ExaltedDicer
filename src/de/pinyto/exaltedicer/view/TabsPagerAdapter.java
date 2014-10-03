package de.pinyto.exaltedicer.view;

import de.pinyto.exalteddicer.DamageFragment;
import de.pinyto.exalteddicer.PoolFragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			return new PoolFragment();
		case 1:
			return new DamageFragment();
		}

		return null;
	}

	public int getCount() {
		return 2;
	}

}
