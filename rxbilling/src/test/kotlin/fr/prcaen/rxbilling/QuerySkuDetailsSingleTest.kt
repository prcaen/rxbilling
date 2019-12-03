package fr.prcaen.rxbilling

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import fr.prcaen.rxbilling.exception.QuerySkuDetailsException
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit.MILLISECONDS

class QuerySkuDetailsSingleTest {

  @Test
  fun `querySkuDetailsAsync should return list when BillingResponseCode is OK`() {
    // Given
    val params = mock<SkuDetailsParams>()
    val client = mock<BillingClient>()
    val list = listOf(mock<SkuDetails>())

    doAnswer { invocationOnMock ->
      val result = mock<BillingResult>().apply {
        doReturn(BillingResponseCode.OK)
          .whenever(this)
          .responseCode
      }
      invocationOnMock.getArgument<SkuDetailsResponseListener>(1)
        .onSkuDetailsResponse(result, list)
    }
      .whenever(client)
      .querySkuDetailsAsync(eq(params), any())

    // When
    val obs = client.querySkuDetailsAsync(params)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    obs.assertValue(list)
    obs.assertNoErrors()
  }

  @Test
  fun `querySkuDetailsAsync should throw exception when BillingResponseCode is not OK`() {
    // Given
    val params = mock<SkuDetailsParams>()
    val client = mock<BillingClient>()
    val responseCode = BillingResponseCode.ERROR
    val debugMessage = "Error"
    val list = listOf(mock<SkuDetails>())

    doAnswer { invocationOnMock ->
      val result = mock<BillingResult>().apply {
        doReturn(responseCode)
          .whenever(this)
          .responseCode

        doReturn(debugMessage)
          .whenever(this)
          .debugMessage
      }
      invocationOnMock.getArgument<SkuDetailsResponseListener>(1)
        .onSkuDetailsResponse(result, list)
    }
      .whenever(client)
      .querySkuDetailsAsync(eq(params), any())

    // When
    val obs = client.querySkuDetailsAsync(params)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    // Then
    obs.assertError { e ->
      e is QuerySkuDetailsException && e.responseCode == responseCode && e.debugMessage == debugMessage
    }
  }
}