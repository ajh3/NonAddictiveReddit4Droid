/*
 * Copyright (c) 2018-present, Aaron J. Halbert.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.aaronhalbert.nosurfforreddit.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.aaronhalbert.nosurfforreddit.BaseActivity
import com.aaronhalbert.nosurfforreddit.R
import com.aaronhalbert.nosurfforreddit.data.local.settings.PreferenceSettingsStore
import com.aaronhalbert.nosurfforreddit.data.remote.auth.AuthenticatorUtils
import com.aaronhalbert.nosurfforreddit.makeToast
import com.aaronhalbert.nosurfforreddit.noSurfLog
import com.aaronhalbert.nosurfforreddit.ui.utils.DayNightPicker
import com.aaronhalbert.nosurfforreddit.ui.utils.SplashScreen
import com.aaronhalbert.nosurfforreddit.utils.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

private const val LOGIN_FAILED_ERROR_MESSAGE = "Error: Login failed"
private const val NETWORK_ERROR_MESSAGE = "Network error!"
private const val NIGHT_MODE = "nightMode"
private const val AMOLED_NIGHT_MODE = "amoledNightMode"

class MainActivity : BaseActivity() {
    @Inject lateinit var preferenceSettingsStore: PreferenceSettingsStore
    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var authenticatorUtils: AuthenticatorUtils
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var navController: NavController
    private val dayNightPicker = DayNightPicker(this)
    private val sharedPrefsListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (NIGHT_MODE == key || AMOLED_NIGHT_MODE == key) {
                pickDayNightMode()
                recreate()
            }
        }
    private var nightMode: Boolean = false
    private var amoledNightMode: Boolean = false

    // region lifecycle methods --------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresentationComponent().inject(this)
        /* AppCompatDelegate.getDefaultNightMode() only works correctly when called
         * before super.onCreate(), otherwise, the activity recreates itself */
        pickDayNightMode()
        super.onCreate(savedInstanceState)
        PreferenceManager.setDefaultValues(application, R.xml.preferences, false)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, viewModelFactory)
                .get(MainActivityViewModel::class.java)

        /* only run the splash animation on fresh app launch */
        if (savedInstanceState == null) initSplash()
        initNavComponent()
        subscribeToNetworkErrors()
    }

    override fun onResume() {
        super.onResume()
        preferenceSettingsStore.registerListener(sharedPrefsListener)
    }

    override fun onPause() {
        super.onPause()
        preferenceSettingsStore.unregisterListener(sharedPrefsListener)
    }

    // endregion lifecycle methods -----------------------------------------------------------------

    // region init methods -------------------------------------------------------------------------

    private fun initSplash() {
        SplashScreen(
            logo,
            this,
            viewModel.allPostsViewStateLiveData
        )
            .setupSplashAnimation()
    }

    private fun initNavComponent() {
        // https://issuetracker.google.com/issues/142847973
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun pickDayNightMode() {
        nightMode = preferenceSettingsStore.isNightMode
        amoledNightMode = preferenceSettingsStore.isAmoledNightMode

        if (nightMode) {
            dayNightPicker.nightModeOn(amoledNightMode)
        } else {
            dayNightPicker.nightModeOff()
        }
    }

    // endregion init methods ----------------------------------------------------------------------

    // region observers/listeners ------------------------------------------------------------------

    private fun subscribeToNetworkErrors() {
        viewModel.networkErrorsLiveData.observe(this,
            Observer {
                it.contentIfNotHandled?.let { makeToast(NETWORK_ERROR_MESSAGE) }
            })
    }

    /* captures result from Reddit login page using custom redirect URI nosurfforreddit://oauth .
     * Intent filter is configured in manifest. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (Intent.ACTION_VIEW == intent.action) {
            val code = authenticatorUtils.extractCodeFromIntent(intent)

            if (code.isEmpty()) {
                noSurfLog { LOGIN_FAILED_ERROR_MESSAGE }
                makeToast(LOGIN_FAILED_ERROR_MESSAGE)
                navController.navigateUp()
            } else {
                viewModel.logUserIn(code)
                navController.navigateUp()
            }
        }
    }

    // endregion observers/listeners ---------------------------------------------------------------
}
