package com.ernestoestrada.mapmyrun

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.ernestoestrada.mapmyrun.R
import java.io.File
import java.util.*

/**
 * Created by neilk on 5/8/2018.
 */
class PhotoFragment : DialogFragment()
{
    private lateinit var mWorkoutPhotoFile :File
    private lateinit var mWorkoutPhoto : ImageView
    private var mLastWorkoutPhotoHeight = 0

    private val WORKOUT_ID = "WORKOUT_ID"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        var uuid : UUID = arguments?.getSerializable(WORKOUT_ID) as UUID
        var workout : Workout = WorkoutLab.get(activity)?.getWotkout(uuid)!!
        mWorkoutPhotoFile = WorkoutLab.get(activity)!!.getPhotoFile(workout)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        var view : View = LayoutInflater.from(activity).inflate(R.layout.dialog_photo, null)
        mWorkoutPhoto = view.findViewById(R.id.workout_photo)

        var viewTreeObserver : ViewTreeObserver = mWorkoutPhoto.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout()
            {
                updatePhoto()
            }
        })

        return AlertDialog.Builder(activity!!).setView(view).create()
    }

    private fun updatePhoto()
    {
        var width = mWorkoutPhoto.width
        var height = mWorkoutPhoto.height
        if(height != mLastWorkoutPhotoHeight)
        {
            mLastWorkoutPhotoHeight = height
            var bitmap : Bitmap = PictureUtils().getScaledBitmap(mWorkoutPhotoFile.absolutePath, width, height)
            mWorkoutPhoto.setImageBitmap(bitmap)
        }
    }

    fun newInstance(uuid: UUID) : PhotoFragment
    {
        var bundle : Bundle = Bundle()
        bundle.putSerializable(WORKOUT_ID, uuid)
        var photoFragment : PhotoFragment = PhotoFragment()
        photoFragment.arguments = bundle
        return photoFragment
    }
}