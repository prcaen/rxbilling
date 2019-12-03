package fr.prcaen.rxbilling

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import fr.prcaen.rxbilling.exception.ConsumeException
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit.MILLISECONDS

class ConsumeSingleTest {

  @Test
  fun `consumeAsync should complete when BillingResponseCode is OK`() {
    // Given
    val params = mock<ConsumeParams>()
    val client = mock<BillingClient>()
    val purchaseToken = "A_TOKEN"

    doAnswer { invocationOnMock ->
      val result = mock<BillingResult>().apply {
        doReturn(BillingResponseCode.OK)
          .whenever(this)
          .responseCode
      }
      invocationOnMock.getArgument<ConsumeResponseListener>(1)
        .onConsumeResponse(result, purchaseToken)
    }
      .whenever(client)
      .consumeAsync(eq(params), any())

    // When
    val obs = client.consumeAsync(params)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    // Then
    obs.assertValue(purchaseToken)
    obs.assertNoErrors()
  }

  @Test
  fun `consumeAsync should throw exception when BillingResponseCode is not OK`() {
    // Given
    val params = mock<ConsumeParams>()
    val client = mock<BillingClient>()
    val responseCode = BillingResponseCode.ERROR
    val debugMessage = "Error"
    val purchaseToken = "A_TOKEN"

    doAnswer { invocationOnMock ->
      val result = mock<BillingResult>().apply {
        doReturn(responseCode)
          .whenever(this)
          .responseCode

        doReturn(debugMessage)
          .whenever(this)
          .debugMessage
      }
      invocationOnMock.getArgument<ConsumeResponseListener>(1)
        .onConsumeResponse(result, purchaseToken)
    }
      .whenever(client)
      .consumeAsync(eq(params), any())

    // When
    val obs = client.consumeAsync(params)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    // Then
    obs.assertError { e ->
      e is ConsumeException && e.responseCode == responseCode && e.debugMessage == debugMessage
    }
  }
}