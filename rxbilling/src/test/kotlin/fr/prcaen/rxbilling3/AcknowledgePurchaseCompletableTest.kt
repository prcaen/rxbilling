package fr.prcaen.rxbilling3

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingResult
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import fr.prcaen.rxbilling3.exception.AcknowledgePurchaseException
import io.reactivex.rxjava3.observers.TestObserver
import org.junit.Test

class AcknowledgePurchaseCompletableTest {

  @Test
  fun `acknowledgePurchaseAsync should complete when BillingResponseCode is OK`() {
    // Given
    val params = mock<AcknowledgePurchaseParams>()
    val client = mock<BillingClient>()

    doAnswer { invocationOnMock ->
      val result = mock<BillingResult>().apply {
        doReturn(BillingResponseCode.OK)
          .whenever(this)
          .responseCode
      }
      invocationOnMock.getArgument<AcknowledgePurchaseResponseListener>(1)
        .onAcknowledgePurchaseResponse(result)
    }
      .whenever(client)
      .acknowledgePurchase(eq(params), any())

    // When
    val obs: TestObserver<Void> = client.acknowledgePurchaseAsync(params)
      .test()

    // Then
    obs.assertComplete()
    obs.assertNoErrors()
  }

  @Test
  fun `acknowledgePurchaseAsync should throw exception when BillingResponseCode is not OK`() {
    // Given
    val params = mock<AcknowledgePurchaseParams>()
    val client = mock<BillingClient>()
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
      invocationOnMock.getArgument<AcknowledgePurchaseResponseListener>(1)
        .onAcknowledgePurchaseResponse(result)
    }
      .whenever(client)
      .acknowledgePurchase(eq(params), any())

    // When
    val obs = client.acknowledgePurchaseAsync(params)
      .test()

    // Then
    obs.assertError { e ->
      e is AcknowledgePurchaseException && e.responseCode == responseCode && e.debugMessage == debugMessage
    }
  }

}