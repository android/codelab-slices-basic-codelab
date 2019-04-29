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

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

/**
 * Displays the current temperature and allows user to adjust it up and down. Any adjustments from
 * the external slice will also change this temperature value.
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var temperatureTextView: TextView
    private lateinit var sliceViewerPackageName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        temperatureTextView = findViewById(R.id.temperature)

        // Used for launching external app to preview Slice.
        sliceViewerPackageName = getString(R.string.slice_viewer_application_package_name)

        findViewById<Button>(R.id.increase_temp).setOnClickListener(this)
        findViewById<Button>(R.id.decrease_temp).setOnClickListener(this)

        findViewById<Button>(R.id.launch_slice_viewer_application).setOnClickListener(this)
    }

    public override fun onResume() {
        super.onResume()
        temperatureTextView.text = getTemperatureString(applicationContext)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.increase_temp -> updateTemperature(applicationContext, temperature + 1)
            R.id.decrease_temp -> updateTemperature(applicationContext, temperature - 1)
            R.id.launch_slice_viewer_application -> launchSliceViewerApplication()
            else -> return
        }
        temperatureTextView.text = getTemperatureString(applicationContext)
    }

    private fun launchSliceViewerApplication() {

        if (isSliceViewerApplicationInstalled() && isSliceViewerApplicationEnabled()) {
            val uri = getString(R.string.uri_specific_for_slice_viewer_application)
            val sliceViewerIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(sliceViewerIntent)
        }
    }

    private fun isSliceViewerApplicationInstalled(): Boolean {

        val packageManager = applicationContext.packageManager

        try {
            packageManager.getPackageInfo(sliceViewerPackageName, PackageManager.GET_ACTIVITIES)
            return true
        } catch (ignored: PackageManager.NameNotFoundException) {

            val notInstalledToast = Toast.makeText(
                applicationContext,
                getString(R.string.slice_viewer_application_not_installed),
                Toast.LENGTH_LONG)

            notInstalledToast.show()

            Log.e(TAG, getString(R.string.error_message_slice_viewer_APK_missing))
        }

        return false
    }

    private fun isSliceViewerApplicationEnabled(): Boolean {
        var status = false
        try {
            val applicationInfo =
                applicationContext.packageManager.getApplicationInfo(sliceViewerPackageName, 0)

            if (applicationInfo != null) {
                status = applicationInfo.enabled
            }
        } catch (ignored: PackageManager.NameNotFoundException) {

            val notEnabledToast = Toast.makeText(
                applicationContext,
                getString(R.string.slice_viewer_application_not_enabled),
                Toast.LENGTH_LONG)

            notEnabledToast.show()

            Log.e(TAG, getString(R.string.error_message_slice_viewer_APK_disabled))
        }

        return status
    }

    companion object {

        private const val TAG = "MainActivity"

        /* Temperature is in Celsius.
         *
         * NOTE: You should store your data in a more permanent way that doesn't disappear when the
         * app is killed. This drastically simplified sample is focused on learning Slices.
         */
        private var temperature = 16

        fun getTemperatureString(context: Context): String {
            return context.getString(R.string.temperature, temperature)
        }

        fun getTemperature(): Int {
            return temperature
        }

        fun updateTemperature(context: Context, newTemperature: Int) {

            if (temperature != newTemperature) {
                temperature = newTemperature

                // Notify slice via URI that the temperature has changed so they can update views.
                val uri = TemperatureSliceProvider.getUri(context, "temperature")
                context.contentResolver.notifyChange(uri, null)
            }
        }
    }
}
