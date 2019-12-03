@file:JvmName("RxBillingClient")
@file:JvmMultifileClass

package fr.prcaen.rxbilling

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.ConsumeParams
import fr.prcaen.rxbilling.exception.ConsumeException
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.NonNull
import io.reactivex.annotations.SchedulerSupport

/**
 * Consumes a given in-app product. Consuming can only be done on an item that's owned, and as a
 * result of consumption, the user will no longer own it.
 *
 * Note: [consumeAsync] does not operate by default on a particular [Scheduler].
 *
 * * Error handling: If Billing result is not [BillingResponseCode.OK],
 * the emitter will deliver [ConsumeException].
 *
 * @property consumeParams Params specific to consume purchase.
 *
 * @throws ConsumeException
 * @see BillingClient.consumeAsync
 *
 * @return a Single that emits the purchase token that was (or was to be) consumed.
 */
@CheckReturnValue
@NonNull
@SchedulerSupport(SchedulerSupport.NONE)
fun BillingClient.consumeAsync(consumeParams: ConsumeParams): Single<String> =
  Single.create { emitter ->
    consumeAsync(consumeParams) { result, purchaseToken ->
      if (!emitter.isDisposed) {
        if (result.responseCode == BillingResponseCode.OK) {
          emitter.onSuccess(purchaseToken)
        } else {
          emitter.tryOnError(
            ConsumeException(
              responseCode = result.responseCode,
              debugMessage = result.debugMessage
            )
          )
        }
      }
    }
  }