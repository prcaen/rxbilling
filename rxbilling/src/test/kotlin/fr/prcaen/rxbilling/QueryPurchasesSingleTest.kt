package fr.prcaen.rxbilling

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.SkuType.INAPP
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchasesResult
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import fr.prcaen.rxbilling.exception.RetrievePurchasesException
import org.junit.Test
import java.util.concurrent.TimeUnit.MILLISECONDS

class QueryPurchasesSingleTest {

  @Test
  fun `queryPurchasesAsync should return list when BillingResponseCode is OK`() {
    // Given
    val skuType = INAPP
    val client = mock<BillingClient>()
    val list = listOf(mock<Purchase>())
    val purchaseResult = mock<PurchasesResult>()
    val billingResult = mock<BillingResult>()

    doReturn(purchaseResult)
      .whenever(client)
      .queryPurchases(skuType)

    doReturn(billingResult)
      .whenever(purchaseResult)
      .billingResult

    doReturn(BillingResponseCode.OK)
      .whenever(billingResult)
      .responseCode

    doReturn(list)
      .whenever(purchaseResult)
      .purchasesList

    // When
    val obs = client.queryPurchasesAsync(skuType)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    obs.assertValue(list)
    obs.assertNoErrors()
  }

  @Test
  fun `queryPurchasesAsync should throw exception when BillingResponseCode is not OK`() {
    // Given
    val skuType = INAPP
    val client = mock<BillingClient>()
    val responseCode = BillingResponseCode.ERROR
    val debugMessage = "Error"
    val purchaseResult = mock<PurchasesResult>()
    val billingResult = mock<BillingResult>()
    val list = listOf(mock<Purchase>())

    doReturn(purchaseResult)
      .whenever(client)
      .queryPurchases(skuType)

    doReturn(billingResult)
      .whenever(purchaseResult)
      .billingResult

    doReturn(responseCode)
      .whenever(billingResult)
      .responseCode

    doReturn(debugMessage)
      .whenever(billingResult)
      .debugMessage

    doReturn(list)
      .whenever(purchaseResult)
      .purchasesList

    // When
    val obs = client.queryPurchasesAsync(skuType)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    // Then
    obs.assertError { e ->
      e is RetrievePurchasesException && e.responseCode == responseCode && e.debugMessage == debugMessage
    }
  }
}