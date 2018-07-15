/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.util

import android.content.Context
import android.preference.PreferenceManager
import io.jim.tesserapp.R

/**
 * Return the current value for the grid preference.
 */
fun <T : Context> T.gridPreference() =
        PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                getString(R.string.pref_grid_key), true)

/**
 * Return the current value for the dark theme preference.
 */
fun <T : Context> T.darkThemePreference() =
        PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                getString(R.string.pref_dark_theme_key), false)

/**
 * Return the current theme id based on [darkThemePreference].
 */
fun <T : Context> T.preferenceThemeId() =
        if (darkThemePreference()) {
            R.style.DarkTheme
        } else {
            R.style.LightTheme
        }
