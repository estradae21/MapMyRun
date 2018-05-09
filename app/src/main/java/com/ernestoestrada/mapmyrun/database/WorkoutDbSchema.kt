package com.ernesto.neil.fitnessmapp.database

/**
 * Created by neilk on 5/8/2018.
 */
class WorkoutDbSchema
{
    companion object WorkoutTable
    {
        val NAME = "workouts"

        class Cols
        {
            val UUID = "uuid"
            val TITLE = "title"
            val DATE = "date"
            val COMPLETED = "completed"
            val DISTANCE = "distance"
            val TIME = "time"
            val CLIENT = "client"
        }
    }
}