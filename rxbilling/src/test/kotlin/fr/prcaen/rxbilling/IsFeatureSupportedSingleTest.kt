package fr.prcaen.rxbilling

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.FeatureType.SUBSCRIPTIONS
import com.android.billingclient.api.BillingResult
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import fr.prcaen.rxbilling.exception.IsFeatureSupportedException
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit.MILLISECONDS

class IsFeatureSupportedSingleTest {

  @Test
  fun `isFeatureSupportedAsync should return true when BillingResponseCode is OK`() {
    // Given
    val client = mock<BillingClient>()
    val result = mock<BillingResult>()
    val feature = SUBSCRIPTIONS

    doReturn(BillingResponseCode.OK)
      .whenever(result)
      .responseCode

    doReturn(result)
      .whenever(client)
      .isFeatureSupported(feature)

    // When
    val obs = client.isFeatureSupportedAsync(feature)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    // Then
    obs.assertValue(true)
    obs.assertNoErrors()
  }

  @Test
  fun `isFeatureSupportedAsync should return false when BillingResponseCode is FEATURE_NOT_SUPPORTED`() {
    // Given
    val client = mock<BillingClient>()
    val result = mock<BillingResult>()
    val feature = SUBSCRIPTIONS

    doReturn(BillingResponseCode.FEATURE_NOT_SUPPORTED)
      .whenever(result)
      .responseCode

    doReturn(result)
      .whenever(client)
      .isFeatureSupported(feature)

    // When
    val obs = client.isFeatureSupportedAsync(feature)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    // Then
    obs.assertValue(false)
    obs.assertNoErrors()
  }

  @Test
  fun `isFeatureSupportedAsync should throw when BillingResponseCode is not OK or FEATURE_NOT_SUPPORTED`() {
    // Given
    val client = mock<BillingClient>()
    val result = mock<BillingResult>()
    val feature = SUBSCRIPTIONS
    val responseCode = BillingResponseCode.ERROR
    val debugMessage = "Error"

    doReturn(responseCode)
      .whenever(result)
      .responseCode

    doReturn(debugMessage)
      .whenever(result)
      .debugMessage

    doReturn(result)
      .whenever(client)
      .isFeatureSupported(feature)

    // When
    val obs = client.isFeatureSupportedAsync(feature)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    // Then
    obs.assertError { e ->
      e is IsFeatureSupportedException && e.responseCode == responseCode && e.debugMessage == debugMessage
    }
  }

}