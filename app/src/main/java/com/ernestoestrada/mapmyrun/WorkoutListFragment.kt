package com.ernesto.neil.fitnessmapp

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

/**
 * Created by neilk on 5/5/2018.
 */
class WorkoutListFragment : Fragment()
{
    private lateinit var mWorkoutRecyclerView : RecyclerView
    private var mAdapter : WorkoutAdapter? = null

    private var mSubtitleVisible : Boolean = false

    private val SAVED_SUBTITLE_VISIBLE = "subtitle"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {

        var view = inflater?.inflate(R.layout.fragment_workout_list, container, false)

        mWorkoutRecyclerView = view!!.findViewById(R.id.workout_recycler_view)
        mWorkoutRecyclerView.layoutManager = LinearLayoutManager(activity)

        if(savedInstanceState != null)
        {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE)
        }
        updateUI()

        return view
    }

    override fun onResume()
    {
        super.onResume()
        updateUI()
    }

    override fun onSaveInstanceState(outState: Bundle?)
    {
        super.onSaveInstanceState(outState)
        outState!!.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?)
    {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.fragment_workout_list, menu)

        var subtitleItem : MenuItem = menu!!.findItem(R.id.show_subtitle)

        if(mSubtitleVisible)
        {
            subtitleItem.setTitle(R.string.hide_subtitle)
        }

        else
        {
            subtitleItem.setTitle(R.string.show_subtitle)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when(item?.itemId)
        {
            R.id.new_workout ->{
                var workout : Workout = Workout()
                WorkoutLab.get(activity)!!.addWorkout(workout)

                var intent : Intent = WorkoutPagerActivity().newIntent(activity, workout.getId()!!)
                startActivity(intent)
                return true
            }

            R.id.show_subtitle ->{
                mSubtitleVisible = !mSubtitleVisible
                activity.invalidateOptionsMenu()
                updateSubtitle()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateSubtitle()
    {
        val workoutLab : WorkoutLab = WorkoutLab.get(activity)!!
        var workoutCount : Int = workoutLab.getWorkouts().size
        var subtitle : String? = getString(R.string.subtitle_format, workoutCount)

        if(!mSubtitleVisible){
            subtitle = null
        }

        var activity : AppCompatActivity = activity as AppCompatActivity
        activity.supportActionBar!!.setSubtitle(subtitle)
    }

    private fun updateUI()
    {
        val workoutLab = WorkoutLab.get(activity)
        val workouts = workoutLab!!.getWorkouts()

        if(mAdapter == null) {
            mAdapter = WorkoutAdapter(workouts)
            mWorkoutRecyclerView.adapter = mAdapter
        }
        else{
            mAdapter!!.setWorkouts(workouts)
            mAdapter!!.notifyDataSetChanged()
        }

        updateSubtitle()
    }

    inner class WorkoutHolder(inflater: LayoutInflater?, parent: ViewGroup?) :
            RecyclerView.ViewHolder(inflater?.inflate(R.layout.list_item_workout, parent, false))
    {
        private var mTitleTextView : TextView = itemView.findViewById(R.id.workout_title)
        private var mDateTextView : TextView = itemView.findViewById(R.id.workout_date)
        private lateinit var mWorkout : Workout

        private var mCompletedImageView : ImageView = itemView.findViewById(R.id.workout_was_completed)

        init
        {
            itemView.setOnClickListener()
            {
                //Toast.makeText(itemView.context, mWorkout.getTitle() + " clicked!", Toast.LENGTH_SHORT).show()
                //var intent : Intent = Intent(activity, WorkoutActivity::class.java)

                var intent = WorkoutPagerActivity().newIntent(activity, mWorkout.getId()!!)
                startActivity(intent)
            }
        }

        fun bind(workout: Workout)
        {
            mWorkout = workout
            mTitleTextView.text = mWorkout.getTitle()
            mDateTextView.text = mWorkout.getDate().toString()

            if(mWorkout.isCompleted()) {
                mCompletedImageView.visibility = View.VISIBLE
            }
            else
            {
                mCompletedImageView.visibility = View.GONE
            }
        }
    }

    inner class WorkoutAdapter(workouts: List<Workout>) : RecyclerView.Adapter<WorkoutHolder>()
    {
        private var mWorkouts : List<Workout> = workouts

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutHolder
        {
            val layoutInflater = LayoutInflater.from(parent.context)
            return WorkoutHolder(layoutInflater, parent)
        }

        override fun getItemCount(): Int
        {
            return mWorkouts.size
        }

        override fun onBindViewHolder(holder: WorkoutHolder?, position: Int)
        {
            var workout : Workout = mWorkouts.get(position)
            holder?.bind(workout)
        }

        fun setWorkouts(workouts: List<Workout>)
        {
            mWorkouts = workouts
        }

    }
}