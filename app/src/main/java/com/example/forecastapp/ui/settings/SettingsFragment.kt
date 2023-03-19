package com.example.forecastapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import com.example.forecastapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = super.onCreateView(inflater, container, savedInstanceState)
        view?.apply {
            //setBackgroundColor(ContextCompat.getColor( requireContext(), R.color.background_weather_color))
            backgroundTintList = ContextCompat.getColorStateList( requireContext(), R.color.background_weather_tint);
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getText(R.string.settings_fragment_actionbar_title)
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = null
    }

}