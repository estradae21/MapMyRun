package com.ernestoestrada.mapmyrun

import java.util.*

/**
 * Created by neilk on 5/5/2018.
 */
class Workout()
{
    private var mId: UUID? = null
    private var mTitle : String = ""
    private var mDate : Date = Date()
    private var mCompleted : Boolean = false

    private var mDistance : Float? = 0.0F
    private var mTime : String? = "00:00:00"

    private var mClient : String? = null

    init {
        mId = UUID.randomUUID()
        //mDate = Date()
    }

    constructor(id: UUID) : this()
    {
        mId = id
        mDate = Date()
    }

    fun getId() : UUID?
    {
        return mId
    }

    fun getTitle() : String?
    {
        return mTitle
    }

    fun setTitle(title : String)
    {
        mTitle = title
    }

    fun getDate() : Date
    {
        return mDate
    }

    fun setDate(date : Date)
    {
        mDate = date
    }

    fun isCompleted() : Boolean
    {
        return mCompleted
    }

    fun setCompleted(completed : Boolean)
    {
        mCompleted = completed
    }

    fun setTime(time : String)
    {
        mTime = time
    }

    fun getTime() : String?
    {
        return mTime
    }

    fun setDistance(distance : Float)
    {
        mDistance = distance
    }

    fun getDistance() : Float?
    {
        return mDistance
    }

    fun getClient() : String?
    {
        return mClient
    }

    fun setClient(client : String?)
    {
        mClient = client
    }

    fun getPhotoFileName():String
    {
        return "IMG_" + getId().toString() + ".jpg"
    }
}