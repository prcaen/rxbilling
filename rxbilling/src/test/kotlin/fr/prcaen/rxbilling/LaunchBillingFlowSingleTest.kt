package fr.prcaen.rxbilling

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit.MILLISECONDS

class LaunchBillingFlowSingleTest {

  @Test
  fun `launchBillingFlowAsync should return BillingResult`() {
    // Given
    val params = mock<BillingFlowParams>()
    val client = mock<BillingClient>()
    val result = mock<BillingResult>()
    val activity = mock<Activity>()

    doReturn(result)
      .whenever(client)
      .launchBillingFlow(activity, params)

    // When
    val obs = client.launchBillingFlowAsync(activity, params)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    // Then
    obs.assertValue(result)
    obs.assertNoErrors()
  }

}