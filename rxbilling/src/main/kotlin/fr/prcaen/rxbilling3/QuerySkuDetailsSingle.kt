@file:JvmName("RxBillingClient")
@file:JvmMultifileClass

package fr.prcaen.rxbilling3

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import fr.prcaen.rxbilling3.exception.QuerySkuDetailsException
import io.reactivex.rxjava3.annotations.CheckReturnValue
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.annotations.SchedulerSupport
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.Scheduler

/**
 * Perform a network query to get SKUs details.
 *
 * Note: [querySkusDetailsAsync] does not operate by default on a particular [Scheduler].
 *
 * Error handling: If Billing result is not [BillingResponseCode.OK],
 * the emitter will deliver [QuerySkuDetailsException].
 *
 * @param skusList Specify the SKUs that are queried for as published in the Google Developer console.
 * @param skuType Specify the type [SkuType] of SKUs we are querying for.
 *
 * @see BillingClient.querySkusDetailsAsync
 *
 * @return a Single that emits the list of [SkuDetails].
 */
@CheckReturnValue
@NonNull
@SchedulerSupport(SchedulerSupport.NONE)
fun BillingClient.querySkusDetailsAsync(
  skusList: List<String>,
  @SkuType skuType: String
): Single<List<SkuDetails>> =
  Single.create { emitter ->
    val params: SkuDetailsParams = SkuDetailsParams.newBuilder()
      .setSkusList(skusList)
      .setType(skuType)
      .build()

    querySkuDetailsAsync(params) { result, list ->
      if (!emitter.isDisposed) {
        if (result.responseCode == BillingResponseCode.OK) {
          emitter.onSuccess(list)
        } else {
          emitter.tryOnError(
            QuerySkuDetailsException(
              responseCode = result.responseCode,
              debugMessage = result.debugMessage
            )
          )
        }
      }
    }
  }

/**
 * Perform a network query to get SKU details.
 *
 * Note: [querySkuDetailsAsync] does not operate by default on a particular [Scheduler].
 *
 * Error handling: If Billing result is not [BillingResponseCode.OK],
 * the emitter will deliver [QuerySkuDetailsException].
 *
 * @param sku Specify the SKU that is queried for as published in the Google Developer console.
 * @param skuType Specify the type [SkuType] of SKUs we are querying for.
 *
 * @see BillingClient.querySkuDetailsAsync
 *
 * @return a Single that emits a [SkuDetails].
 */
@CheckReturnValue
@NonNull
@SchedulerSupport(SchedulerSupport.NONE)
fun BillingClient.querySkuDetailsAsync(
  sku: String,
  @SkuType skuType: String
): Single<SkuDetails> = querySkusDetailsAsync(
  skusList = listOf(sku),
  skuType = skuType
).map { list ->
  list.firstOrNull() ?: throw QuerySkuDetailsException(
    responseCode = BillingResponseCode.DEVELOPER_ERROR,
    debugMessage = "Empty list of SKUs"
  )
}