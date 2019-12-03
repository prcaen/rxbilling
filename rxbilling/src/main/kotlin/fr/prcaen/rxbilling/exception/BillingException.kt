package fr.prcaen.rxbilling.exception

import com.android.billingclient.api.BillingClient.BillingResponseCode

sealed class BillingException(
  @BillingResponseCode open val responseCode: Int,
  open val debugMessage: String,
  prefix: String
) : RuntimeException("$prefix error. Response code: $responseCode, debug message: $debugMessage")

class AcknowledgePurchaseException(
  @BillingResponseCode override val responseCode: Int,
  override val debugMessage: String
) : BillingException(
  responseCode = responseCode,
  debugMessage = debugMessage,
  prefix = "Acknowledge purchase"
)

class ConsumeException(
  @BillingResponseCode override val responseCode: Int,
  override val debugMessage: String
) : BillingException(
  responseCode = responseCode,
  debugMessage = debugMessage,
  prefix = "Consume"
)

class IsFeatureSupportedException(
  @BillingResponseCode override val responseCode: Int,
  override val debugMessage: String
) : BillingException(
  responseCode = responseCode,
  debugMessage = debugMessage,
  prefix = "Is feature supported"
)

class LaunchPriceChangeConfirmationFlowException(
  @BillingResponseCode override val responseCode: Int,
  override val debugMessage: String
) : BillingException(
  responseCode = responseCode,
  debugMessage = debugMessage,
  prefix = "Launch price change confirmation flow"
)

class LoadRewardedSkuException(
  @BillingResponseCode override val responseCode: Int,
  override val debugMessage: String
) : BillingException(
  responseCode = responseCode,
  debugMessage = debugMessage,
  prefix = "Load rewarded Sku"
)

class QueryPurchaseHistoryException(
  @BillingResponseCode override val responseCode: Int,
  override val debugMessage: String
) : BillingException(
  responseCode = responseCode,
  debugMessage = debugMessage,
  prefix = "Query purchase history"
)

class RetrievePurchasesException(
  @BillingResponseCode override val responseCode: Int,
  override val debugMessage: String
) : BillingException(
  responseCode = responseCode,
  debugMessage = debugMessage,
  prefix = "Query purchases"
)

class QuerySkuDetailsException(
  @BillingResponseCode override val responseCode: Int,
  override val debugMessage: String
) : BillingException(
  responseCode = responseCode,
  debugMessage = debugMessage,
  prefix = "Query Sku details"
)