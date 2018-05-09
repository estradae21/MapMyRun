package com.ernesto.neil.fitnessmapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.ernestoestrada.mapmyrun.R
import java.util.*

/**
 * Created by neilk on 5/8/2018.
 */
class WorkoutPagerActivity : AppCompatActivity()
{
    private lateinit var mViewPager : ViewPager
    private lateinit var mWorkouts : List<Workout>

    private val EXTRA_WORKOUT_ID = "com.ernesto.neil.fitnessmapp.workout_id"

    fun newIntent(packageContext: Context, workoutId : UUID) : Intent
    {
        var intent = Intent(packageContext, WorkoutPagerActivity::class.java)
        intent.putExtra(EXTRA_WORKOUT_ID, workoutId)
        return intent
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_pager)

        var workoutId : UUID = intent.getSerializableExtra(EXTRA_WORKOUT_ID) as UUID

        mViewPager = findViewById(R.id.workout_view_pager)

        mWorkouts = WorkoutLab.get(this)!!.getWorkouts()

        var fragmentManager : FragmentManager = supportFragmentManager

        mViewPager.adapter = object : FragmentStatePagerAdapter(fragmentManager){
            override fun getItem(position: Int): Fragment
            {
                var workout : Workout = mWorkouts.get(position)
                return WorkoutFragment().newInstance(workout.getId()!!)
            }

            override fun getCount(): Int
            {
                return mWorkouts.size
            }

        }

        for(i in 0..(mWorkouts.size - 1))
        {
            if(mWorkouts.get(i).getId()!!.equals(workoutId))
            {
                mViewPager.setCurrentItem(i)
                break
            }
        }
    }
}