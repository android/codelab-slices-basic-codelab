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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.android.slicesbasiccodelab.MainActivity.Companion.getTemperature
import com.example.android.slicesbasiccodelab.MainActivity.Companion.updateTemperature

/**
 * Updates temperature (triggered by SliceProvider and MainActivity).
 */
class TemperatureBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (ACTION_CHANGE_TEMPERATURE == intent.action) {
            val newValue =
                intent.extras?.getInt(EXTRA_TEMPERATURE_VALUE, getTemperature()) ?: return
            updateTemperature(context, newValue)
        }
    }

    companion object {
        private const val PACKAGE_NAME = "com.example.android.slicesbasiccodelab"

        const val ACTION_CHANGE_TEMPERATURE = "$PACKAGE_NAME.action.CHANGE_TEMPERATURE"
        const val EXTRA_TEMPERATURE_VALUE = "$PACKAGE_NAME.extra.TEMPERATURE_VALUE"
    }
}
