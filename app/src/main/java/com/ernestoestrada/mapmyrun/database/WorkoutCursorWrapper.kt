package com.ernesto.neil.fitnessmapp.database

import android.database.Cursor
import android.database.CursorWrapper
import com.ernesto.neil.fitnessmapp.Workout
import com.ernesto.neil.fitnessmapp.database.WorkoutDbSchema.*
import java.util.*

/**
 * Created by neilk on 5/8/2018.
 */
class WorkoutCursorWrapper(val cursor: Cursor) : CursorWrapper(cursor)
{
    fun getWorkout(): Workout
    {
        var uuidString = getString(getColumnIndex(WorkoutTable.Cols().UUID))
        var title = getString(getColumnIndex(WorkoutTable.Cols().TITLE))
        var date : Long = getLong(getColumnIndex(WorkoutTable.Cols().DATE))
        var isCompleted = getInt(getColumnIndex(WorkoutTable.Cols().COMPLETED))
        var distance = getFloat(getColumnIndex(WorkoutTable.Cols().DISTANCE))
        var time = getString(getColumnIndex(WorkoutTable.Cols().TIME))
        var client = getString(getColumnIndex(WorkoutTable.Cols().CLIENT))

        var workout : Workout = Workout(UUID.fromString(uuidString))
        workout.setTitle(title)
        workout.setDate(Date(date))
        workout.setCompleted(isCompleted != 0)
        workout.setDistance(distance)
        workout.setTime(time)
        workout.setClient(client)

        return workout
    }
}