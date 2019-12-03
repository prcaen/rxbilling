@file:JvmName("RxBillingClient")
@file:JvmMultifileClass

package fr.prcaen.rxbilling

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import fr.prcaen.rxbilling.internal.checkMainThread
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.NonNull
import io.reactivex.annotations.SchedulerSupport

/**
 * Initiate the billing flow for an in-app purchase or subscription.
 *
 * It will show the Google Play purchase screen. The result will be delivered via the
 * [PurchasesUpdatedListener] interface implementation reported to the [BillingClient.Builder.setListener].
 *
 * Note: [launchBillingFlowAsync] does not operate by default on a particular [Scheduler].
 *
 * Warning: This method should be executed on MainThread, it will throw [IllegalStateException] if not.
 *
 * @param activity An activity reference from which the billing flow will be launched.
 * @param params Params specific to the request [BillingFlowParams].
 *
 * @see BillingClient.launchBillingFlow
 *
 * @return a Single that emits a [BillingResult].
 */
@CheckReturnValue
@NonNull
@SchedulerSupport(SchedulerSupport.NONE)
fun BillingClient.launchBillingFlowAsync(
  activity: Activity,
  params: BillingFlowParams
): Single<BillingResult> =
  Single.create { emitter ->
    if (!emitter.isDisposed) {
      if (!emitter.checkMainThread()) {
        return@create
      }
    }

    val result = launchBillingFlow(activity, params)

    if (!emitter.isDisposed) {
      emitter.onSuccess(result)
    }
  }