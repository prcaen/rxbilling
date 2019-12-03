@file:JvmName("RxBillingClient")
@file:JvmMultifileClass

package fr.prcaen.rxbilling

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.PurchaseHistoryRecord
import fr.prcaen.rxbilling.exception.QueryPurchaseHistoryException
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.NonNull
import io.reactivex.annotations.SchedulerSupport

/**
 * Returns the most recent purchase made by the user for each SKU, even if that purchase is
 * expired, canceled, or consumed.
 *
 * Note: [queryPurchaseHistoryAsync] does not operate by default on a particular [Scheduler].
 *
 * Error handling: If Billing result is not [BillingResponseCode.OK],
 * the emitter will deliver [QueryPurchaseHistoryException].
 *
 * @property skuType The type of SKU, either "inapp" or "subs" as in [SkuType]
 *
 * @see BillingClient.queryPurchaseHistoryAsync
 *
 * @return a Single that emits the list of [PurchaseHistoryRecord]
 * (even if that purchase is expired, canceled, or consumed - up to 1 per each SKU).
 */
@CheckReturnValue
@NonNull
@SchedulerSupport(SchedulerSupport.NONE)
fun BillingClient.queryPurchaseHistoryAsync(@SkuType skuType: String): Single<List<PurchaseHistoryRecord>> =
  Single.create { emitter ->
    queryPurchaseHistoryAsync(skuType) { result, list ->
      if (!emitter.isDisposed) {
        if (result.responseCode == BillingResponseCode.OK) {
          emitter.onSuccess(list)
        } else {
          emitter.tryOnError(
            QueryPurchaseHistoryException(
              responseCode = result.responseCode,
              debugMessage = result.debugMessage
            )
          )
        }
      }
    }
  }