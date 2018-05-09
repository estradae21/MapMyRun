package com.ernestoestrada.mapmyrun


import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import com.ernestoestrada.mapmyrun.R
import java.util.*

/**
 * Created by neilk on 5/8/2018.
 */
class DatePickerFragment : DialogFragment()
{
    private val ARG_DATE = "date"
    private lateinit var mDatePicker : DatePicker

    val EXTRA_DATE = "com.ernesto.neil.fitnessmapp.date"

    fun newInstance(date : Date) : DatePickerFragment
    {
        var args : Bundle = Bundle()
        args.putSerializable(ARG_DATE, date)

        var fragment : DatePickerFragment = DatePickerFragment()

        fragment.arguments = args

        return fragment
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        var date : Date = arguments?.getSerializable(ARG_DATE) as Date

        var calendar : Calendar = Calendar.getInstance()
        calendar.time = date

        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)


        var v : View = LayoutInflater.from(activity).inflate(R.layout.dialog_date, null)

        mDatePicker = v.findViewById(R.id.dialog_date_picker)
        mDatePicker.init(year, month, day, null)

        return AlertDialog.Builder(activity!!).setView(v).setTitle(R.string.date_picker_title).
                setPositiveButton(android.R.string.ok, object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int)
                    {
                        var year = mDatePicker.year
                        var month = mDatePicker.month
                        var day = mDatePicker.dayOfMonth

                        var date : Date = GregorianCalendar(year, month, day).time

                        sendResult(Activity.RESULT_OK, date)
                    }
                }).create()
    }

    private fun sendResult(resultCode : Int, date: Date)
    {
        if(targetFragment == null)
        {
            return
        }

        var intent : Intent = Intent()
        intent.putExtra(EXTRA_DATE, date)

        targetFragment!!.onActivityResult(targetRequestCode, resultCode, intent)
    }
}