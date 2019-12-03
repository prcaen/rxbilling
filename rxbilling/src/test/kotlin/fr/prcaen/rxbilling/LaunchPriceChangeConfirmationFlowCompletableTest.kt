package fr.prcaen.rxbilling

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PriceChangeConfirmationListener
import com.android.billingclient.api.PriceChangeFlowParams
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import fr.prcaen.rxbilling.exception.LaunchPriceChangeConfirmationFlowException
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit.MILLISECONDS

class LaunchPriceChangeConfirmationFlowCompletableTest {

  @Test
  fun `launchPriceChangeConfirmationFlowAsync should complete when BillingResponseCode is OK`() {
    // Given
    val params = mock<PriceChangeFlowParams>()
    val client = mock<BillingClient>()
    val activity = mock<Activity>()

    doAnswer { invocationOnMock ->
      val result = mock<BillingResult>().apply {
        doReturn(BillingResponseCode.OK)
          .whenever(this)
          .responseCode
      }
      invocationOnMock.getArgument<PriceChangeConfirmationListener>(2)
        .onPriceChangeConfirmationResult(result)
    }
      .whenever(client)
      .launchPriceChangeConfirmationFlow(eq(activity), eq(params), any())

    // When
    val obs = client.launchPriceChangeConfirmationFlowAsync(activity, params)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    // Then
    obs.assertComplete()
    obs.assertNoErrors()
  }

  @Test
  fun `launchPriceChangeConfirmationFlowAsync should throw when BillingResponseCode is not OK`() {
    // Given
    val params = mock<PriceChangeFlowParams>()
    val client = mock<BillingClient>()
    val activity = mock<Activity>()
    val responseCode = BillingResponseCode.ERROR
    val debugMessage = "Error"

    doAnswer { invocationOnMock ->
      val result = mock<BillingResult>().apply {
        doReturn(responseCode)
          .whenever(this)
          .responseCode

        doReturn(debugMessage)
          .whenever(this)
          .debugMessage
      }

      invocationOnMock.getArgument<PriceChangeConfirmationListener>(2)
        .onPriceChangeConfirmationResult(result)
    }
      .whenever(client)
      .launchPriceChangeConfirmationFlow(eq(activity), eq(params), any())

    // When
    val obs = client.launchPriceChangeConfirmationFlowAsync(activity, params)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    // Then
    obs.assertError { e ->
      e is LaunchPriceChangeConfirmationFlowException && e.responseCode == responseCode && e.debugMessage == debugMessage
    }
  }

}