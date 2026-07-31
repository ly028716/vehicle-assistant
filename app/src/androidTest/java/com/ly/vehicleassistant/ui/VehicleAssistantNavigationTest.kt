package com.ly.vehicleassistant.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ly.vehicleassistant.MainActivity
import org.junit.Rule
import org.junit.Test

class VehicleAssistantNavigationTest {
    @get:Rule val composeRule = createAndroidComposeRule<MainActivity>()

    @Test fun homeAndRecordNavigationAreAvailable() {
        composeRule.onNodeWithText("车辆概览").assertIsDisplayed()
        composeRule.onNodeWithText("添加车辆档案").assertIsDisplayed()
        composeRule.onNodeWithText("记录").performClick()
        composeRule.onNodeWithText("保养记录").assertIsDisplayed()
        composeRule.onNodeWithText("本地用车助手").assertIsDisplayed()
    }
}
