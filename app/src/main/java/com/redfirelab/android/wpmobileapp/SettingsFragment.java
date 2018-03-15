package com.redfirelab.android.wpmobileapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import com.redfirelab.android.wpmobileapp.data.WPPreferences;

import java.util.Objects;

/**
 * Created by Pouria on 11/26/2017.
 * wpMApp project.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.pref_wordpress_app);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();

        int count = prefScreen.getPreferenceCount();

        // Go through all of the preferences, and set up their preference summary.
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            // You don't need to set up preference summaries for checkbox preferences because
            // they are already set up in xml using summaryOff and summary On
            if (!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }

        //for check the user input in this shareEditText
        Preference sizeOfTitleKey = findPreference(getString(R.string.pref_size_title_key));
        Preference sizeOfDescKey = findPreference(getString(R.string.pref_size_desc_key));
        Preference sizeOfContentKey = findPreference(getString(R.string.pref_size_content_key));

        sizeOfTitleKey.setOnPreferenceChangeListener(this);
        sizeOfDescKey.setOnPreferenceChangeListener(this);
        sizeOfContentKey.setOnPreferenceChangeListener(this);


    }

    private void setPreferenceSummary(Preference preference, String value) {

        if (preference instanceof EditTextPreference) {

            preference.setSummary(value);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Figure out which preference was changed
        Preference preference = findPreference(key);
        if (null != preference) {
            // Updates the summary for the preference
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
                Log.v("SettingFragment.java", "onSharedPreferenceChanged => " + value);
            }

            if (Objects.equals(preference.getKey(), getString(R.string.pref_site_address_key))) {

                WPPreferences.saveSiteAddressIsChange(getContext(), true);

            }

            if(Objects.equals(preference.getKey(), getString(R.string.pref_size_title_key))){

                WPPreferences.savePostTitleSizeChange(getContext(), true);

            }

            if(Objects.equals(preference.getKey(), getString(R.string.pref_size_content_key))){

                WPPreferences.savePostContentSizeChange(getContext(), true);

            }

            if(Objects.equals(preference.getKey(), getString(R.string.pref_size_desc_key))){

                WPPreferences.savePostDescriptionSizeChange(getContext(), true);

            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        /* Unregister the preference change listener */
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // register the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    //check the number, entered by user in editText shared preference
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Toast error = Toast.makeText(getContext(), R.string.error_shared_pref_edit_text_number, Toast.LENGTH_SHORT);

        // Double check that the preference is the size preference
        String titleSize = getString(R.string.pref_size_title_key);
        String descSize = getString(R.string.pref_size_desc_key);
        String contentSize = getString(R.string.pref_size_content_key);


        if (preference.getKey().equals(titleSize)) {
            String stringSize = (String) newValue;
            try {
                float size = Float.parseFloat(stringSize);
                // If the number is outside of the acceptable range, show an error.
                if (size > 30 || size <= 0) {
                    error.show();
                    return false;
                }
            } catch (NumberFormatException nfe) {
                // If whatever the user entered can't be parsed to a number, show an error
                error.show();
                return false;
            }

        } else if (preference.getKey().equals(descSize)) {

            String stringSize = (String) newValue;
            try {
                float size = Float.parseFloat(stringSize);
                // If the number is outside of the acceptable range, show an error.
                if (size > 30 || size <= 0) {
                    error.show();
                    return false;
                }
            } catch (NumberFormatException nfe) {
                // If whatever the user entered can't be parsed to a number, show an error
                error.show();
                return false;
            }

        } else if (preference.getKey().equals(contentSize)) {

            String stringSize = (String) newValue;
            try {
                float size = Float.parseFloat(stringSize);
                // If the number is outside of the acceptable range, show an error.
                if (size > 30 || size <= 0) {
                    error.show();
                    return false;
                }
            } catch (NumberFormatException nfe) {
                // If whatever the user entered can't be parsed to a number, show an error
                error.show();
                return false;
            }

        }

        return true;
    }
}
