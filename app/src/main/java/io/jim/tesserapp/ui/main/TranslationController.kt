/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.ui.main

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R

/**
 * Controller targeting the translation.
 *
 * @receiver
 * The view model containing the targeting live data.
 * Need not to be externally synchronized, as that's done internally.
 *
 * @param context
 * App context.
 *
 * @param seekBar
 * Seek bar to control the camera distance.
 *
 * @param watch
 * Text view representing the current camera distance.
 *
 * @param liveData
 * Runs on the receiving view model.
 * Returns the live data to be controlled.
 */
fun MainViewModel.translationController(
        context: Context,
        seekBar: SeekBar,
        watch: TextView,
        liveData: MainViewModel.() -> MutableLiveDataNonNull<Double>
) = Controller(
        viewModel = this,
        liveData = liveData,
        seekBar = seekBar,
        watch = watch,
        watchFormatString = context.getString(R.string.transform_translation_watch_format),
        valueRange = -5.0..5.0
)
