package fr.prcaen.rxbilling3

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.SkuType.INAPP
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsResponseListener
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import fr.prcaen.rxbilling3.exception.QuerySkuDetailsException
import org.junit.Test

class QuerySkuDetailsSingleTest {

  @Test
  fun `querySkusDetailsAsync should return list when BillingResponseCode is OK`() {
    // Given
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
      .querySkuDetailsAsync(any(), any())

    // When
    val obs = client.querySkusDetailsAsync(
      skusList = listOf(),
      skuType = INAPP
    )
      .test()

    obs.assertValue(list)
    obs.assertNoErrors()
  }

  @Test
  fun `querySkusDetailsAsync should throw exception when BillingResponseCode is not OK`() {
    // Given
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
      .querySkuDetailsAsync(any(), any())

    // When
    val obs = client.querySkusDetailsAsync(
      skusList = listOf(),
      skuType = INAPP
    )
      .test()

    // Then
    obs.assertError { e ->
      e is QuerySkuDetailsException && e.responseCode == responseCode && e.debugMessage == debugMessage
    }
  }

  @Test
  fun `querySkuDetailsAsync should return list when BillingResponseCode is OK`() {
    // Given
    val client = mock<BillingClient>()
    val skuDetails = mock<SkuDetails>()
    val name = "SKU"
    val list = listOf(skuDetails)

    given(skuDetails.sku).willReturn(name)

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
      .querySkuDetailsAsync(any(), any())

    // When
    val obs = client.querySkuDetailsAsync(
      sku = name,
      skuType = INAPP
    )
      .test()

    obs.assertValue(skuDetails)
    obs.assertNoErrors()
  }

  @Test
  fun `querySkuDetailsAsync should throw exception when BillingResponseCode is not OK`() {
    // Given
    val client = mock<BillingClient>()
    val responseCode = BillingResponseCode.ERROR
    val debugMessage = "Error"
    val name = "SKU"
    val skuDetails = mock<SkuDetails>()
    val list = listOf(skuDetails)

    given(skuDetails.sku).willReturn(name)

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
      .querySkuDetailsAsync(any(), any())

    // When
    val obs = client.querySkuDetailsAsync(
      sku = name,
      skuType = INAPP
    )
      .test()

    // Then
    obs.assertError { e ->
      e is QuerySkuDetailsException && e.responseCode == responseCode && e.debugMessage == debugMessage
    }
  }

}