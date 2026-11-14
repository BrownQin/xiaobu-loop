package com.learningloop.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.learningloop.app.data.TelemetryStore
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** 匿名统计默认开启，并尊重家长在隐私政策页的调整。 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TelemetryConsentTest {

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Test
    fun default_telemetryEnabled() {
        assertTrue(TelemetryStore.enabled(context))
    }

    @Test
    fun disabled_stopsTelemetry() {
        TelemetryStore.setEnabled(context, false)
        assertFalse(TelemetryStore.enabled(context))
    }

    @Test
    fun reEnabled_restoresTelemetry() {
        TelemetryStore.setEnabled(context, false)
        TelemetryStore.setEnabled(context, true)
        assertTrue(TelemetryStore.enabled(context))
    }
}
