@file:JvmName("RxBillingClient")
@file:JvmMultifileClass

package fr.prcaen.rxbilling3

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.Purchase
import fr.prcaen.rxbilling3.exception.RetrievePurchasesException
import io.reactivex.rxjava3.annotations.CheckReturnValue
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.annotations.SchedulerSupport
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.Scheduler

/**
 * Get purchases details for all the items bought within your app. This method uses a cache of
 * Google Play Store app without initiating a network request.
 *
 * Note: [queryPurchasesAsync] does not operate by default on a particular [Scheduler].
 *
 * Error handling: If Billing result is not [BillingResponseCode.OK],
 * the emitter will deliver [RetrievePurchasesException].
 *
 * @param skuType The type of SKU, either "inapp" or "subs" as in [SkuType]
 *
 * @see BillingClient.queryPurchases
 *
 * @return a Single that emits the list of [Purchase].
 */
@CheckReturnValue
@NonNull
@SchedulerSupport(SchedulerSupport.NONE)
fun BillingClient.queryPurchasesAsync(@SkuType skuType: String): Single<List<Purchase>> =
  Single.create { emitter ->
    if (emitter.isDisposed) return@create

    val purchasesResult = queryPurchases(skuType)

    if (!emitter.isDisposed) {
      val result = purchasesResult.billingResult

      if (result.responseCode == BillingResponseCode.OK) {
        emitter.onSuccess(purchasesResult.purchasesList)
      } else {
        emitter.tryOnError(
          RetrievePurchasesException(
            responseCode = result.responseCode,
            debugMessage = result.debugMessage
          )
        )
      }
    }
  }