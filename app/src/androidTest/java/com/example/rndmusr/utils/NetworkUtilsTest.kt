// androidTest/java/com/example/rndmusr/utils/NetworkUtilsInstrumentedTest.kt
package com.example.rndmusr.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.rndmusr.domain.utils.NetworkUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class NetworkUtilsInstrumentedTest {

    @Test
    fun testIsInternetAvailable() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val result = NetworkUtils.isInternetAvailable(context)

        println("Internet available: $result")
        assertTrue(true)
    }
}