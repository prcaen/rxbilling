@file:JvmName("RxBillingClient")
@file:JvmMultifileClass

package fr.prcaen.rxbilling

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.PriceChangeFlowParams
import fr.prcaen.rxbilling.exception.LaunchPriceChangeConfirmationFlowException
import fr.prcaen.rxbilling.internal.checkMainThread
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.NonNull
import io.reactivex.annotations.SchedulerSupport

/**
 * Initiate a flow to confirm the change of price for an item subscribed by the user.
 *
 * When the price of a user subscribed item has changed, launch this flow to take users to
 * a screen with price change information. User can confirm the new price or cancel the flow.
 *
 * Note: [launchPriceChangeConfirmationFlowAsync] does not operate by default on a particular [Scheduler].
 *
 * Warning: This method should be executed on MainThread, it will throw [IllegalStateException] if not.
 *
 * @param activity An activity reference from which the billing flow will be launched.
 * @param params Params specific to the request [PriceChangeFlowParams].
 *
 * @see BillingClient.launchPriceChangeConfirmationFlow
 *
 * @return Completable.
 */
@CheckReturnValue
@NonNull
@SchedulerSupport(SchedulerSupport.NONE)
fun BillingClient.launchPriceChangeConfirmationFlowAsync(
  activity: Activity,
  params: PriceChangeFlowParams
): Completable =
  Completable.create { emitter ->
    if (!emitter.isDisposed) {
      if (!emitter.checkMainThread()) {
        return@create
      }
    }

    launchPriceChangeConfirmationFlow(activity, params) { result ->
      if (!emitter.isDisposed) {
        if (result.responseCode == BillingResponseCode.OK) {
          emitter.onComplete()
        } else {
          emitter.tryOnError(
            LaunchPriceChangeConfirmationFlowException(
              responseCode = result.responseCode,
              debugMessage = result.debugMessage
            )
          )
        }
      }
    }
  }