package com.ernesto.neil.fitnessmapp

import android.support.v4.app.Fragment

/**
 * Created by neilk on 5/5/2018.
 */
class WorkoutListActivity : SingleFragmentActivity()
{
    override fun createFragment(): Fragment
    {
        return WorkoutListFragment();
    }

}