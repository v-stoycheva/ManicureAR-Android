package com.viktoriastoycheva.manicurear // Тестов пакет в Android архитектурата

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testSuccessfulLoginLayoutInteraction() {
        // 1. Симулиране на въвеждане на имейл адрес в текстовото Material поле
        onView(withId(R.id.etEmail))
            .perform(typeText("viktoria.test@gmail.com"), closeSoftKeyboard())

        // 2. Симулиране на въвеждане на парола и автоматично скриване на софтуерната клавиатура
        onView(withId(R.id.etPassword))
            .perform(typeText("RawPassword123!"), closeSoftKeyboard())

        // 3. Извършване на автоматизирано софтуерно кликване върху бутона за вход
        onView(withId(R.id.btnLogin))
            .perform(click())

        // 4. Верификация (Assertion) на интерфейсната реакция локално на екрана
        onView(withId(R.id.tvStatus))
            .check(matches(isDisplayed()))

        onView(withId(R.id.tvStatus))
            .check(matches(withText("Searching for hand...")))
    }
}