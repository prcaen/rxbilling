@file:JvmName("RxBillingClient")
@file:JvmMultifileClass

package fr.prcaen.rxbilling

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import fr.prcaen.rxbilling.exception.QuerySkuDetailsException
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.NonNull
import io.reactivex.annotations.SchedulerSupport

/**
 * Perform a network query to get SKU details.
 *
 * Note: [querySkuDetailsAsync] does not operate by default on a particular [Scheduler].
 *
 * Error handling: If Billing result is not [BillingResponseCode.OK],
 * the emitter will deliver [QuerySkuDetailsException].
 *
 * @property params Params specific to this query request [SkuDetailsParams]
 *
 * @see BillingClient.querySkuDetailsAsync
 *
 * @return a Single that emits the list of [SkuDetails].
 */
@CheckReturnValue
@NonNull
@SchedulerSupport(SchedulerSupport.NONE)
fun BillingClient.querySkuDetailsAsync(params: SkuDetailsParams): Single<List<SkuDetails>> =
  Single.create { emitter ->
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