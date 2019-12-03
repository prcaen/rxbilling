@file:JvmName("RxBillingClient")
@file:JvmMultifileClass

package fr.prcaen.rxbilling

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.RewardLoadParams
import fr.prcaen.rxbilling.exception.LoadRewardedSkuException
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.NonNull
import io.reactivex.annotations.SchedulerSupport

/**
 * Loads a rewarded sku in the background and returns the result asynchronously.
 *
 * Note: [loadRewardedSkuAsync] does not operate by default on a particular [Scheduler].
 *
 * Error handling: If Billing result is not [BillingResponseCode.OK],
 * the emitter will deliver [LoadRewardedSkuException].
 *
 * @param params Params specific to this load request [RewardLoadParams]
 *
 * @see BillingClient.loadRewardedSku
 *
 * @return Completable.
 */
@CheckReturnValue
@NonNull
@SchedulerSupport(SchedulerSupport.NONE)
fun BillingClient.loadRewardedSkuAsync(params: RewardLoadParams): Completable =
  Completable.create { emitter ->
    loadRewardedSku(params) { result ->
      if (!emitter.isDisposed) {
        if (result.responseCode == BillingResponseCode.OK) {
          emitter.onComplete()
        } else {
          emitter.tryOnError(
            LoadRewardedSkuException(
              responseCode = result.responseCode,
              debugMessage = result.debugMessage
            )
          )
        }
      }
    }
  }