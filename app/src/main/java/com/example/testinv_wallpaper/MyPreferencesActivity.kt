package com.example.testinv_wallpaper

import android.os.Bundle
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.PreferenceActivity
import android.widget.Toast


class MyPreferencesActivity : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.prefs)

        // We want to add a validator to the number of circles so that it only
        // accepts numbers
        val circlePreference = preferenceScreen.findPreference(
            "numberOfCircles"
        )

        // Add the validator
        circlePreference.onPreferenceChangeListener = numberCheckListener
    }

    /**
     * Checks that a preference is a valid numerical value
     */
    var numberCheckListener =
        OnPreferenceChangeListener { preference, newValue -> // Check that the string is an integer
            if (newValue != null && newValue.toString().length > 0 && newValue.toString().matches("\\d*")) {
                return@OnPreferenceChangeListener true
            }
            // If now create a message to the user
            Toast.makeText(
                this@MyPreferencesActivity, "Invalid Input",
                Toast.LENGTH_SHORT
            ).show()
            false
        }
}

private fun String.matches(regex: String): Boolean {
   return true

}

