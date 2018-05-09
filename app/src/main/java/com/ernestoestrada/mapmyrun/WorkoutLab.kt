package com.ernesto.neil.fitnessmapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ernesto.neil.fitnessmapp.database.WorkoutBaseHelper
import com.ernesto.neil.fitnessmapp.database.WorkoutCursorWrapper
import com.ernesto.neil.fitnessmapp.database.WorkoutDbSchema
import com.ernesto.neil.fitnessmapp.database.WorkoutDbSchema.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by neilk on 5/5/2018.
 */
class WorkoutLab(context: Context?)
{
    //val workouts : ArrayList<Workout> //no longer needed

    private lateinit var mContext : Context
    private lateinit var mDatabase : SQLiteDatabase

    init
    {
        mContext = context!!.applicationContext
        mDatabase = WorkoutBaseHelper(mContext).writableDatabase
        //workouts = ArrayList()    //No longer needed
    }

    fun addWorkout(w : Workout)
    {
        //workouts.add(w)   //No longer needed
        var values = getContentValues(w)

        mDatabase.insert(WorkoutTable.NAME, null, values)
    }

    fun getWorkouts() : List<Workout>
    {
        var workouts : ArrayList<Workout> = arrayListOf()

        var cursor : WorkoutCursorWrapper = queryWorkouts(null, null)

        try
        {
            cursor.moveToFirst()
            while (!cursor.isAfterLast)
            {
                workouts.add(cursor.getWorkout())
                cursor.moveToNext()
            }
        }
        finally
        {
            cursor.close()
        }

        return workouts
    }

    fun getWotkout(id : UUID): Workout?
    {
        var cursor : WorkoutCursorWrapper = queryWorkouts(WorkoutTable.Cols().UUID + " = ?",
                arrayOf(id.toString()))

        try
        {
            if(cursor.count == 0)
            {
                return null
            }

            cursor.moveToFirst()
            return cursor.getWorkout()
        }
        finally
        {

        }
    }

    fun getPhotoFile(workout: Workout): File
    {
        var filesDir : File = mContext.filesDir
        return File(filesDir, workout.getPhotoFileName())
    }

    fun updateWorkout(workout: Workout)
    {
        var uuidString = workout.getId().toString()
        var values = getContentValues(workout)

        mDatabase.update(WorkoutTable.NAME, values,
                WorkoutTable.Cols().UUID + " = ?", arrayOf(uuidString))
    }

    private fun queryWorkouts(whereClause: String?, whereArgs: Array<String>?) : WorkoutCursorWrapper
    {
        var cursor : Cursor = mDatabase.query(
                WorkoutTable.NAME,
                null,   // null selects all columns
                whereClause,
                whereArgs,
                null,   //groupBy
                null,   //having
                null)   //orderBy

        return WorkoutCursorWrapper(cursor)
    }

    private fun getContentValues(workout : Workout) : ContentValues
    {
        var values = ContentValues()
        values.put(WorkoutTable.Cols().UUID, workout.getId().toString())
        values.put(WorkoutTable.Cols().TITLE, workout.getTitle())
        values.put(WorkoutTable.Cols().DATE, workout.getDate()!!.time)

        if(workout.isCompleted()) {
            values.put(WorkoutTable.Cols().COMPLETED, 1)
        }
        else
        {
            values.put(WorkoutTable.Cols().COMPLETED, 0)
        }

        values.put(WorkoutTable.Cols().DISTANCE, workout.getDistance())
        values.put(WorkoutTable.Cols().TIME, workout.getTime())

        values.put(WorkoutTable.Cols().CLIENT, workout.getClient())

        return values
    }

    companion object
    {
        var sWorkoutLab: WorkoutLab? = null

        fun get(context: Context?): WorkoutLab?
        {
            if (sWorkoutLab == null) {
                sWorkoutLab = WorkoutLab(context)
            }
            return sWorkoutLab
        }
    }
}