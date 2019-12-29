@file:JvmName("Preconditions")

package fr.prcaen.rxbilling3.internal

import android.os.Looper
import io.reactivex.rxjava3.core.CompletableEmitter
import io.reactivex.rxjava3.core.SingleEmitter

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