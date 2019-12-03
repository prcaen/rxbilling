@file:JvmName("RxBillingClient")
@file:JvmMultifileClass

package fr.prcaen.rxbilling

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import fr.prcaen.rxbilling.exception.AcknowledgePurchaseException
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.NonNull
import io.reactivex.annotations.SchedulerSupport

/**
 * Acknowledge in-app purchases.
 *
 * Note: [acknowledgePurchaseAsync] does not operate by default on a particular [Scheduler].
 *
 * Error handling: If Billing result is not [BillingResponseCode.OK],
 * the emitter will deliver [AcknowledgePurchaseException].
 *
 * @property params Params specific to this load request [AcknowledgePurchaseParams].
 *
 * @see BillingClient.acknowledgePurchase
 *
 * @return Completable.
 */
@CheckReturnValue
@NonNull
@SchedulerSupport(SchedulerSupport.NONE)
fun BillingClient.acknowledgePurchaseAsync(params: AcknowledgePurchaseParams): Completable =
  Completable.create { emitter ->
    acknowledgePurchase(params) { result ->
      if (!emitter.isDisposed) {
        if (result.responseCode == BillingResponseCode.OK) {
          emitter.onComplete()
        } else {
          emitter.tryOnError(
            AcknowledgePurchaseException(
              responseCode = result.responseCode,
              debugMessage = result.debugMessage
            )
          )
        }
      }
    }
  }