package fr.prcaen.rxbilling

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.SkuType.INAPP
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.PurchaseHistoryResponseListener
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import fr.prcaen.rxbilling.exception.QueryPurchaseHistoryException
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit.MILLISECONDS

class QueryPurchaseHistorySingleTest {

  @Test
  fun `queryPurchaseHistoryAsync should return list when BillingResponseCode is OK`() {
    // Given
    val skuType = INAPP
    val client = mock<BillingClient>()
    val list = listOf(mock<PurchaseHistoryRecord>())

    doAnswer { invocationOnMock ->
      val result = mock<BillingResult>().apply {
        doReturn(BillingResponseCode.OK)
          .whenever(this)
          .responseCode
      }
      invocationOnMock.getArgument<PurchaseHistoryResponseListener>(1)
        .onPurchaseHistoryResponse(result, list)
    }
      .whenever(client)
      .queryPurchaseHistoryAsync(eq(skuType), any())

    // When
    val obs = client.queryPurchaseHistoryAsync(skuType)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    // Then
    obs.assertValue(list)
    obs.assertNoErrors()
  }

  @Test
  fun `queryPurchaseHistoryAsync should throw exception when BillingResponseCode is not OK`() {
    // Given
    val skuType = INAPP
    val client = mock<BillingClient>()
    val responseCode = BillingResponseCode.ERROR
    val debugMessage = "Error"
    val list = listOf(mock<PurchaseHistoryRecord>())

    doAnswer { invocationOnMock ->
      val result = mock<BillingResult>().apply {
        doReturn(responseCode)
          .whenever(this)
          .responseCode

        doReturn(debugMessage)
          .whenever(this)
          .debugMessage
      }
      invocationOnMock.getArgument<PurchaseHistoryResponseListener>(1)
        .onPurchaseHistoryResponse(result, list)
    }
      .whenever(client)
      .queryPurchaseHistoryAsync(eq(skuType), any())

    // When
    val obs = client.queryPurchaseHistoryAsync(skuType)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    // Then
    obs.assertError { e ->
      e is QueryPurchaseHistoryException && e.responseCode == responseCode && e.debugMessage == debugMessage
    }
  }
}