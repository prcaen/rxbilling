@file:JvmName("RxBillingClient")
@file:JvmMultifileClass

package fr.prcaen.rxbilling3

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import fr.prcaen.rxbilling3.exception.AcknowledgePurchaseException
import io.reactivex.rxjava3.annotations.CheckReturnValue
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.annotations.SchedulerSupport
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler

/**
 * Acknowledge in-app purchases.
 *
 * Note: [acknowledgePurchaseAsync] does not operate by default on a particular [Scheduler].
 *
 * Error handling: If Billing result is not [BillingResponseCode.OK],
 * the emitter will deliver [AcknowledgePurchaseException].
 *
 * @param params Params specific to this load request [AcknowledgePurchaseParams].
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