@file:JvmName("RxBillingClient")
@file:JvmMultifileClass

package fr.prcaen.rxbilling

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.FeatureType
import com.android.billingclient.api.BillingResult
import fr.prcaen.rxbilling.exception.IsFeatureSupportedException
import fr.prcaen.rxbilling.internal.checkMainThread
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.NonNull
import io.reactivex.annotations.SchedulerSupport

/**
 * Check if specified feature or capability is supported by the Play Store.
 *
 * Note: [isFeatureSupportedAsync] does not operate by default on a particular [Scheduler].
 *
 * Warning: This method should be executed on MainThread, it will throw [IllegalStateException] if not.
 *
 * Error handling: If Billing result is not [BillingResponseCode.OK] or [BillingResponseCode.FEATURE_NOT_SUPPORTED],
 * the emitter will deliver [IsFeatureSupportedException].
 *
 * @property feature Params specific to this load request [FeatureType].
 *
 * @see BillingClient.isFeatureSupported
 *
 * @return a Single that emits true if feature is not supported, false otherwise..
 */
@CheckReturnValue
@NonNull
@SchedulerSupport(SchedulerSupport.NONE)
fun BillingClient.isFeatureSupportedAsync(@FeatureType feature: String): Single<Boolean> =
  Single.create { emitter ->
    if (!emitter.isDisposed) {
      if (!emitter.checkMainThread()) {
        return@create
      }

      val result: BillingResult = isFeatureSupported(feature)
      when (result.responseCode) {
        BillingResponseCode.OK -> emitter.onSuccess(true)
        BillingResponseCode.FEATURE_NOT_SUPPORTED -> emitter.onSuccess(false)
        else -> emitter.tryOnError(
          IsFeatureSupportedException(
            responseCode = result.responseCode,
            debugMessage = result.debugMessage
          )
        )
      }
    }
  }