@file:JvmName("Preconditions")

package fr.prcaen.rxbilling.internal

import android.os.Looper
import io.reactivex.CompletableEmitter
import io.reactivex.SingleEmitter

internal fun <T> SingleEmitter<T>.checkMainThread(): Boolean =
  if (Looper.myLooper() != Looper.getMainLooper()) {
    tryOnError(
      IllegalStateException(
        "Expected to be called on the main thread but was " + Thread.currentThread().name
      )
    )
    false
  } else {
    true
  }

internal fun CompletableEmitter.checkMainThread(): Boolean =
  if (Looper.myLooper() != Looper.getMainLooper()) {
    tryOnError(
      IllegalStateException(
        "Expected to be called on the main thread but was " + Thread.currentThread().name
      )
    )
    false
  } else {
    true
  }