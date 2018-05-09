package com.ernesto.neil.fitnessmapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ernesto.neil.fitnessmapp.database.WorkoutDbSchema.*

/**
 * Created by neilk on 5/8/2018.
 */
class WorkoutBaseHelper(context: Context, val DATABASE_NAME: String = "workoutBase.db",
                        val VERSION : Int = 1) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION)
{
    override fun onCreate(db: SQLiteDatabase?)
    {
        db?.execSQL("create table " + WorkoutTable.NAME + "(" +
                " _id integer primary key autoincrement," +
                WorkoutTable.Cols().UUID + ", " +
                WorkoutTable.Cols().TITLE + ", " +
                WorkoutTable.Cols().DATE + ", " +
                WorkoutTable.Cols().COMPLETED + ", " +
                WorkoutTable.Cols().DISTANCE + ", " +
                WorkoutTable.Cols().TIME + ", " +
                WorkoutTable.Cols().CLIENT +
                ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)
    {

    }

}