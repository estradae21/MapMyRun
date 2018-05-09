package com.ernesto.neil.fitnessmapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.FileProvider
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.format.DateFormat;
import android.widget.*
import java.io.File
import java.util.*

/**
 * Created by neilk on 5/5/2018.
 */
class WorkoutFragment : Fragment()
{
    private lateinit var mWorkout : Workout
    private lateinit var mTitleField : EditText
    private lateinit var mDateButton : Button
    private lateinit var mCompletedCheckBox : CheckBox

    private lateinit var mDistanceButton : Button
    private lateinit var mTimeButton : Button

    private lateinit var mReportButton : Button

    private lateinit var mClientButton : Button

    private lateinit var mPhotoButton : ImageButton
    private lateinit var mPhotoView : ImageView

    private lateinit var mPhotoFile : File

    private val ARG_WORKOUT_ID = "workout_id"
    private val DIALOG_DATE = "DialogDate"

    private val sDialogPhoto = "DialogPhoto"

    private val REQUEST_DATE = 0
    private val REQUEST_CONTACT = 1
    private val REQUEST_PHOTO = 2

    fun newInstance(workoutId : UUID) : WorkoutFragment
    {
        var args : Bundle = Bundle()
        args.putSerializable(ARG_WORKOUT_ID, workoutId)

        var fragment = WorkoutFragment()

        fragment.arguments = args

        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        var workoutId : UUID = arguments.getSerializable(ARG_WORKOUT_ID) as UUID

        mWorkout = WorkoutLab.get(activity)?.getWotkout(workoutId)!!
        mPhotoFile = WorkoutLab.get(activity)!!.getPhotoFile(mWorkout)
    }

    override fun onPause()
    {
        super.onPause()

        WorkoutLab.get(activity)?.updateWorkout(mWorkout)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val v = inflater?.inflate(R.layout.fragment_workout, container, false)

        mTitleField = v!!.findViewById(R.id.workout_title)
        mTitleField.setText(mWorkout.getTitle())
        mTitleField.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
            {
                //This space is intentionally left blank
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
                mWorkout.setTitle(s.toString())
            }

            override fun afterTextChanged(p0: Editable?)
            {
                //This one too
            }

        })

        mDateButton = v.findViewById(R.id.workout_date)
        updateDate()
        mDateButton.setOnClickListener()
        {
            var manager : FragmentManager = fragmentManager

            var dialog : DatePickerFragment = DatePickerFragment().newInstance(mWorkout.getDate()!!)

            dialog.setTargetFragment(this, REQUEST_DATE)

            dialog.show(manager, DIALOG_DATE)
        }

        mDistanceButton = v.findViewById(R.id.workout_distance)
        mDistanceButton.setText("Distance: " + mWorkout.getDistance() + " miles")

        mTimeButton = v.findViewById(R.id.workout_duration)
        mTimeButton.setText("Duration: " + mWorkout.getTime())

        mCompletedCheckBox = v.findViewById(R.id.workout_completed)
        mCompletedCheckBox.isChecked = mWorkout.isCompleted()
        mCompletedCheckBox.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean)
            {
                mWorkout.setCompleted(isChecked)
            }

        })

        mReportButton = v.findViewById(R.id.workout_report)
        mReportButton.setOnClickListener()
        {
            var i : Intent = Intent(Intent.ACTION_SEND)
            i.setType("text/plain")
            i.putExtra(Intent.EXTRA_TEXT, getWorkoutReport())
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.workout_report_subject))

            i = Intent.createChooser(i, getString(R.string.send_report))
            startActivity(i)
        }

        val pickContact : Intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        mClientButton = v.findViewById(R.id.workout_client)
        mClientButton.setOnClickListener()
        {
            startActivityForResult(pickContact, REQUEST_CONTACT)
        }

        if(mWorkout.getClient() != null)
        {
            mClientButton.setText(mWorkout.getClient())
        }

        var packageManager : PackageManager = activity.packageManager
        if(packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null)
        {
            mClientButton.isEnabled = false
        }

        mPhotoButton = v.findViewById(R.id.workout_camera)
        val captureImage : Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var canTakePhoto : Boolean = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null
        mPhotoButton.isEnabled = canTakePhoto

        mPhotoButton.setOnClickListener()
        {
            var uri : Uri = FileProvider.getUriForFile(activity,
                    "com.ernesto.neil.fitnessmapp.fileprovider", mPhotoFile)

            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri)

            var cameraActivities : List<ResolveInfo> =
                    activity.packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)

            for(activity : ResolveInfo in cameraActivities)
            {
                getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }

            startActivityForResult(captureImage, REQUEST_PHOTO)
        }


        mPhotoView = v.findViewById(R.id.workout_photo)
        mPhotoView.setOnClickListener()
        {
            displayPhoto()
        }

        updatePhotoView()

        return v
    }

    private fun displayPhoto()
    {
        if(mPhotoFile != null && mPhotoFile.exists())
        {
            var photoFragment : PhotoFragment = PhotoFragment().newInstance(mWorkout.getId()!!)
            var fragmentManager : FragmentManager = activity.supportFragmentManager
            photoFragment.show(fragmentManager, sDialogPhoto)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if(resultCode != Activity.RESULT_OK)
        {
            return
        }

        if(requestCode == REQUEST_DATE)
        {
            var date : Date = data!!.getSerializableExtra(DatePickerFragment().EXTRA_DATE) as Date
            mWorkout.setDate(date)
            updateDate()
        }

        else if(requestCode == REQUEST_CONTACT && data != null)
        {
            var contactUri : Uri = data.data

            //specify which fields you want your query to return values for
            var queryFields : Array<String> = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

            //Perform your query - the contactUri is like a "where" clause here
            var c : Cursor = activity.contentResolver.query(contactUri, queryFields,
                    null, null, null)

            try
            {
                //Double check that you actually got results
                if(c.count == 0)
                {
                    return
                }

                //Pull out the first column of the first row of data - that is the client's name
                c.moveToFirst()
                var client : String = c.getString(0)
                mWorkout.setClient(client)
                mClientButton.setText(client)
            }
            finally
            {
                c.close()
            }
        }
        else if(requestCode == REQUEST_PHOTO)
        {
            var uri : Uri = FileProvider.getUriForFile(activity,
                    "com.ernesto.neil.fitnessmapp.fileprovider", mPhotoFile)

            activity.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            updatePhotoView()
        }
    }

    private fun updateDate()
    {
        mDateButton.setText(mWorkout.getDate().toString())
    }

    private fun getWorkoutReport() : String
    {
        var completedString: String? = null

        if(mWorkout.isCompleted())
        {
            completedString = getString(R.string.workout_report_completed)
        }
        else
        {
            completedString = getString(R.string.workout_report_uncompleted)
        }

        var dateFormat = "EEE, MMM dd"
        var dateString : String = DateFormat.format(dateFormat, mWorkout.getDate()).toString()

        var client : String? = mWorkout.getClient()

        if(client == null)
        {
            client = getString(R.string.workout_report_no_client)
        }
        else
        {
            client = getString(R.string.workout_report_client, mWorkout.getClient())
        }

        var report : String = getString(R.string.workout_report, mWorkout.getTitle(),
                dateString, completedString, client)

        return report
    }

    private fun updatePhotoView()
    {
        if(mPhotoFile == null || !mPhotoFile.exists())
        {
            mPhotoView.setImageDrawable(null)
        }
        else
        {
            var bitmap : Bitmap = PictureUtils().getScaledBitmap(mPhotoFile.path, activity)
            mPhotoView.setImageBitmap(bitmap)
        }
    }
}