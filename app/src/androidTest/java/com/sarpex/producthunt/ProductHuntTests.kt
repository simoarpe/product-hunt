package com.sarpex.producthunt

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToString
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductHuntTests {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        // Using targetContext as the Context of the instrumentation code
        composeTestRule.launchProductHuntApp(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun app_launches() {
        composeTestRule.onNodeWithText("Product Hunt").assertExists()
    }

    @Test
    fun app_opensPost() {

        println(composeTestRule.onRoot().printToString())
        composeTestRule.onNodeWithText(text = "Awesome Post", substring = true).performClick()

        println(composeTestRule.onRoot().printToString())
        try {
            composeTestRule.onNodeWithText("Description", substring = true).assertExists()
        } catch (e: AssertionError) {
            println(composeTestRule.onRoot().printToString())
            throw e
        }
    }
}
