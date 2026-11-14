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

/**
 * 合规底线：首次启动未做隐私选择前，统计必须处于关闭状态。
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TelemetryConsentTest {

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Test
    fun beforeConsent_telemetryDisabled() {
        assertFalse(TelemetryStore.consentGiven(context))
        assertFalse(TelemetryStore.enabled(context))
    }

    @Test
    fun localOnly_keepsTelemetryDisabled() {
        TelemetryStore.setConsent(context, telemetryAllowed = false)
        assertTrue(TelemetryStore.consentGiven(context))
        assertFalse(TelemetryStore.enabled(context))
    }

    @Test
    fun agreeWithTelemetry_enablesAndCanBeWithdrawn() {
        TelemetryStore.setConsent(context, telemetryAllowed = true)
        assertTrue(TelemetryStore.enabled(context))

        TelemetryStore.setEnabled(context, false)
        assertFalse(TelemetryStore.enabled(context))
    }
}
