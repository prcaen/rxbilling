package fr.prcaen.rxbilling3

import com.android.billingclient.api.BillingClient
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test

class IsReadySingleTest {

  @Test
  fun `isReadyAsync should return true when BillingClient#isReady == true`() {
    // Given
    val client = mock<BillingClient>()
    val isReady = true

    doReturn(isReady)
      .whenever(client)
      .isReady

    // When
    val obs = client.isReadyAsync()
      .test()

    // Then
    obs.assertValue(isReady)
    obs.assertNoErrors()
  }

  @Test
  fun `isReadyAsync should return false when BillingClient#isReady == false`() {
    // Given
    val client = mock<BillingClient>()
    val isReady = false

    doReturn(isReady)
      .whenever(client)
      .isReady

    // When
    val obs = client.isReadyAsync()
      .test()

    // Then
    obs.assertValue(isReady)
    obs.assertNoErrors()
  }

}