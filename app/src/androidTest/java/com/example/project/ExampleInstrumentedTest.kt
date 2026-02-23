package com.example.project

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import com.example.project.view.LoginActivity
import com.example.project.view.DashboardActivity
import com.example.project.view.ForgotPasswordActivity
import org.junit.After

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<LoginActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testSuccessfulLogin_navigatesToDashboard() {
        // Enter email
        composeRule.onNodeWithTag("email")
            .performTextInput("binishasht.dev@gmail.com")

        // Enter password
        composeRule.onNodeWithTag("password")
            .performTextInput("bini1234")

        // Click Login
        composeRule.onNodeWithTag("button")
            .performClick()

        Thread.sleep(3000)
        // Verify that the app navigates to the DashboardActivity
        Intents.intended(hasComponent(DashboardActivity::class.java.name))
    }
}

@RunWith(AndroidJUnit4::class)
class ForgotPasswordInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ForgotPasswordActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testSendResetLink_isWorking() {
        // Enter the email
        composeRule.onNodeWithTag("forgotEmailField")
            .performTextInput("binishasht.dev@gmail.com")

        // Click the Send Reset Link button
        composeRule.onNodeWithTag("resetButton")
            .performClick()

    }
}