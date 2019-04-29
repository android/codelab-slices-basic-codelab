/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.slicesbasiccodelab

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.SliceAction
import androidx.slice.builders.header
import androidx.slice.builders.list

import com.example.android.slicesbasiccodelab.MainActivity.Companion.getTemperature
import com.example.android.slicesbasiccodelab.MainActivity.Companion.getTemperatureString
import com.example.android.slicesbasiccodelab.TemperatureBroadcastReceiver.Companion.ACTION_CHANGE_TEMPERATURE
import com.example.android.slicesbasiccodelab.TemperatureBroadcastReceiver.Companion.EXTRA_TEMPERATURE_VALUE

/**
 * Creates a temperature control Slice that mirrors the main activity. The slice displays the
 * current temperature and allows the user to adjust the temperature up and down from the Slice
 * without launching the activity.
 *
 * NOTE: The main action still allows the user to launch the main activity if they choose.
 */
class TemperatureSliceProvider : SliceProvider() {

    private lateinit var contextNonNull: Context

    override fun onCreateSliceProvider(): Boolean {
        contextNonNull = context ?: return false
        return true
    }

    /*
     * Each Slice has an associated URI. The standard format is package name + path for each Slice.
     * In our case, we only have one slice mapped to the 'temperature' path
     * ('com.example.android.slicesbasiccodelab/temperature').
     *
     * When a surface wants to display a Slice, it sends a binding request to your app with this
     * URI via this method and you build out the slice to return. The surface can then display the
     * Slice when appropriate.
     *
     * Note: You can make your slices interactive by adding actions. (We do this for our
     * temperature up/down buttons.)
     */
    override fun onBindSlice(sliceUri: Uri): Slice? {
        Log.d(TAG, "onBindSlice(): $sliceUri")

        when (sliceUri.path) {
            "/temperature" -> return createTemperatureSlice(sliceUri)
        }
        return null
    }

    // Creates the actual Slice used in onBindSlice().
    private fun createTemperatureSlice(sliceUri: Uri): Slice {
        Log.d(TAG, "createTemperatureSlice(): $sliceUri")

        /* Slices are constructed by using a ListBuilder (it is the main building block of Slices).
         * ListBuilder allows you to add different types of rows that are displayed in a list.
         * Because we are using the Slice KTX library, we can use the DSL version of ListBuilder,
         * so we just need to use list() and include some general arguments before defining the
         * structure of the Slice.
         */
        return list(contextNonNull, sliceUri, ListBuilder.INFINITY) {
            setAccentColor(ContextCompat.getColor(contextNonNull, R.color.slice_accent_color))
            /* The first row of your slice should be a header. The header supports a title,
             * subtitle, and a tappable action (usually used to launch an Activity). A header
             * can also have a summary of the contents of the slice which can be shown when
             * the Slice may be too large to be displayed. In our case, we are also attaching
             * multiple actions to the header row (temp up/down).
             *
             * If we wanted to add additional rows, you can use the RowBuilder or the GridBuilder.
             *
             */
            header {
                title = getTemperatureString(contextNonNull)
                // Launches the main Activity associated with the Slice.
                primaryAction = SliceAction.create(
                    PendingIntent.getActivity(
                        contextNonNull,
                        sliceUri.hashCode(),
                        Intent(contextNonNull, MainActivity::class.java),
                        0
                    ),
                    IconCompat.createWithResource(contextNonNull, R.drawable.ic_home),
                    ListBuilder.ICON_IMAGE,
                    contextNonNull.getString(R.string.slice_action_primary_title)
                )
            }
            addAction(
                SliceAction.create(
                    createTemperatureChangePendingIntent(getTemperature() + 1),
                    IconCompat.createWithResource(contextNonNull, R.drawable.ic_temp_up),
                    ListBuilder.ICON_IMAGE,
                    contextNonNull.getString(R.string.increase_temperature)
                )
            )
            addAction(
                SliceAction.create(
                    createTemperatureChangePendingIntent(getTemperature() - 1),
                    IconCompat.createWithResource(contextNonNull, R.drawable.ic_temp_down),
                    ListBuilder.ICON_IMAGE,
                    contextNonNull.getString(R.string.decrease_temperature)
                )
            )
        }
    }

    // PendingIntent that triggers an increase/decrease in temperature.
    private fun createTemperatureChangePendingIntent(value: Int): PendingIntent {
        val intent = Intent(ACTION_CHANGE_TEMPERATURE)
            .setClass(contextNonNull, TemperatureBroadcastReceiver::class.java)
            .putExtra(EXTRA_TEMPERATURE_VALUE, value)

        return PendingIntent.getBroadcast(
            contextNonNull, requestCode++, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        private const val TAG = "TempSliceProvider"
        private var requestCode = 0

        fun getUri(context: Context, path: String): Uri {
            return Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(context.packageName)
                .appendPath(path)
                .build()
        }
    }
}
