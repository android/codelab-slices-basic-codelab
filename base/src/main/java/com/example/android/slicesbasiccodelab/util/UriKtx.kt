/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.slicesbasiccodelab.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri

/**
 * Extension function that builds the Uri from the package name, AKA, the authority.
 *
 * @return Uri with the provided path.
 */
fun Context.buildUriWithAuthority(path: String): Uri {
    return Uri.Builder()
        .scheme(ContentResolver.SCHEME_CONTENT)
        .authority(packageName)
        .appendPath(path)
        .build()
}
