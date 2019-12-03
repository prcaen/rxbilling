@file:JvmName("RxBillingClient")
@file:JvmMultifileClass

package fr.prcaen.rxbilling

import com.android.billingclient.api.BillingClient
import fr.prcaen.rxbilling.internal.checkMainThread
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.NonNull
import io.reactivex.annotations.SchedulerSupport

/**
 * Checks if the client is currently connected to the service, so that requests to other methods
 * will succeed.
 *
 * Note: [isReadyAsync] does not operate by default on a particular [Scheduler].
 *
 * Warning: This method should be executed on MainThread, it will throw [IllegalStateException] if not.
 *
 * @see BillingClient.isReady
 *
 * @return a Single that emits true if the client is currently connected to the service, false otherwise.
 */
@CheckReturnValue
@NonNull
@SchedulerSupport(SchedulerSupport.NONE)
fun BillingClient.isReadyAsync(): Single<Boolean> =
  Single.create { emitter ->
    if (!emitter.isDisposed) {
      if (!emitter.checkMainThread()) {
        return@create
      }

      emitter.onSuccess(isReady)
    }
  }