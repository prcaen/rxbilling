package fr.prcaen.rxbilling

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.RewardLoadParams
import com.android.billingclient.api.RewardResponseListener
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import fr.prcaen.rxbilling.exception.LoadRewardedSkuException
import io.reactivex.observers.TestObserver
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit.MILLISECONDS

class LoadRewardedSkuCompletableTest {

  @Test
  fun `loadRewardedSkuAsync should complete when BillingResponseCode is OK`() {
    // Given
    val params = mock<RewardLoadParams>()
    val client = mock<BillingClient>()

    doAnswer { invocationOnMock ->
      val result = mock<BillingResult>().apply {
        doReturn(BillingResponseCode.OK)
          .whenever(this)
          .responseCode
      }
      invocationOnMock.getArgument<RewardResponseListener>(1)
        .onRewardResponse(result)
    }
      .whenever(client)
      .loadRewardedSku(eq(params), any())

    // When
    val obs: TestObserver<Void> = client.loadRewardedSkuAsync(params)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    // Then
    obs.assertComplete()
    obs.assertNoErrors()
  }

  @Test
  fun `loadRewardedSkuAsync should throw exception when BillingResponseCode is not OK`() {
    // Given
    val params = mock<RewardLoadParams>()
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
      invocationOnMock.getArgument<RewardResponseListener>(1)
        .onRewardResponse(result)
    }
      .whenever(client)
      .loadRewardedSku(eq(params), any())

    // When
    val obs = client.loadRewardedSkuAsync(params)
      .test()
      .apply {
        awaitTerminalEvent(50, MILLISECONDS)
      }

    // Then
    obs.assertError { e ->
      e is LoadRewardedSkuException && e.responseCode == responseCode && e.debugMessage == debugMessage
    }
  }

}