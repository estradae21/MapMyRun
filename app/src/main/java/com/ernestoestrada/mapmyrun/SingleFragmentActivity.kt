package com.ernestoestrada.mapmyrun

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.ernestoestrada.mapmyrun.R

/**
 * Created by neilk on 5/5/2018.
 */
abstract class SingleFragmentActivity : AppCompatActivity()
{
    abstract fun createFragment() : Fragment

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        val fm = supportFragmentManager

        var fragment : Fragment? =  fm.findFragmentById(R.id.fragment_container)

        if(fragment == null)
        {
            fragment = createFragment()
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit()
        }
    }
}