/*
 *  Created by Jim Eckerlein on 7/23/18 9:35 AM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/23/18 9:35 AM
 */

package io.jim.tesserapp.ui.preferences

import android.content.Context
import android.preference.PreferenceManager
import androidx.annotation.StyleRes
import io.jim.tesserapp.R
import org.joda.time.LocalTime

/**
 * Return the current value for the grid preference.
 */
fun <T : Context> T.gridPreference(): Boolean =
        PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                getString(R.string.pref_grid_key), true)

/**
 * Return the current value for the dark theme preference.
 */
fun <T : Context> T.darkThemePreference(): Boolean =
        PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                getString(R.string.pref_theme_key), false)

/**
 * Return the current theme id based on [darkThemePreference].
 *
 * The theme preference is set to one the following values:
 * - Light
 * - Dark
 * - Dark at night: Dark theme after 7pm.
 */
@StyleRes
fun <T : Context> T.preferenceThemeId(): Int =
        if (darkThemePreference()) R.style.DarkTheme
        else R.style.LightTheme
