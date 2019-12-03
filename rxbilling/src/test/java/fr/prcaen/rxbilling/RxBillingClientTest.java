package fr.prcaen.rxbilling;

import android.app.Activity;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.PriceChangeConfirmationListener;
import com.android.billingclient.api.PriceChangeFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.RewardLoadParams;
import com.android.billingclient.api.RewardResponseListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import fr.prcaen.rxbilling.exception.AcknowledgePurchaseException;
import fr.prcaen.rxbilling.exception.ConsumeException;
import fr.prcaen.rxbilling.exception.IsFeatureSupportedException;
import fr.prcaen.rxbilling.exception.LaunchPriceChangeConfirmationFlowException;
import fr.prcaen.rxbilling.exception.LoadRewardedSkuException;
import fr.prcaen.rxbilling.exception.QueryPurchaseHistoryException;
import fr.prcaen.rxbilling.exception.QuerySkuDetailsException;
import fr.prcaen.rxbilling.exception.RetrievePurchasesException;
import io.reactivex.observers.TestObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

import static com.android.billingclient.api.BillingClient.FeatureType.SUBSCRIPTIONS;
import static com.android.billingclient.api.BillingClient.SkuType.INAPP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class RxBillingClientTest {

  @Test void acknowledgePurchaseAsync_should_complete_when_BillingResponseCode_is_OK() {
    // Given
    final AcknowledgePurchaseParams params = mock(AcknowledgePurchaseParams.class);
    final BillingClient client = mock(BillingClient.class);

    doAnswer(invocationOnMock -> {
      final BillingResult result = mock(BillingResult.class);

      given(result.getResponseCode()).willReturn(BillingResponseCode.OK);

      invocationOnMock.getArgument(1, AcknowledgePurchaseResponseListener.class)
          .onAcknowledgePurchaseResponse(result);

      return null;
    })
        .when(client)
        .acknowledgePurchase(eq(params), any());

    // When
    final TestObserver<Void> obs = RxBillingClient.acknowledgePurchaseAsync(client, params)
        .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertComplete();
    obs.assertNoErrors();
  }

  @Test void acknowledgePurchaseAsync_should_throw_error_when_BillingResponseCode_is_not_OK() {
    // Given
    final AcknowledgePurchaseParams params = mock(AcknowledgePurchaseParams.class);
    final BillingClient client = mock(BillingClient.class);
    final int responseCode = BillingResponseCode.ERROR;
    final String debugMessage = "Error";

    doAnswer(invocationOnMock -> {
      final BillingResult result = mock(BillingResult.class);

      given(result.getResponseCode()).willReturn(responseCode);
      given(result.getDebugMessage()).willReturn(debugMessage);

      invocationOnMock.getArgument(1, AcknowledgePurchaseResponseListener.class)
          .onAcknowledgePurchaseResponse(result);

      return null;
    })
        .when(client)
        .acknowledgePurchase(eq(params), any());

    // When
    final TestObserver<Void> obs = RxBillingClient.acknowledgePurchaseAsync(client, params)
        .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertError(e -> e instanceof AcknowledgePurchaseException
        && ((AcknowledgePurchaseException) e).getResponseCode() == responseCode
        && ((AcknowledgePurchaseException) e).getDebugMessage().equals(debugMessage)
    );
  }

  @Test void consumeAsync_should_return_value_when_BillingResponseCode_is_OK() {
    // Given
    final ConsumeParams params = mock(ConsumeParams.class);
    final BillingClient client = mock(BillingClient.class);
    final String purchaseToken = "A_TOKEN";

    doAnswer(invocationOnMock -> {
      final BillingResult result = mock(BillingResult.class);

      given(result.getResponseCode()).willReturn(BillingResponseCode.OK);

      invocationOnMock.getArgument(1, ConsumeResponseListener.class)
          .onConsumeResponse(result, purchaseToken);

      return null;
    })
        .when(client)
        .consumeAsync(eq(params), any());

    // When
    final TestObserver<String> obs = RxBillingClient.consumeAsync(client, params)
        .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertValue(purchaseToken);
    obs.assertNoErrors();
  }

  @Test void consumeAsync_should_throw_error_when_BillingResponseCode_is_not_OK() {
    // Given
    final ConsumeParams params = mock(ConsumeParams.class);
    final BillingClient client = mock(BillingClient.class);
    final int responseCode = BillingResponseCode.ERROR;
    final String debugMessage = "Error";

    doAnswer(invocationOnMock -> {
      final BillingResult result = mock(BillingResult.class);

      given(result.getResponseCode()).willReturn(responseCode);
      given(result.getDebugMessage()).willReturn(debugMessage);

      invocationOnMock.getArgument(1, ConsumeResponseListener.class)
          .onConsumeResponse(result, null);

      return null;
    })
        .when(client)
        .consumeAsync(eq(params), any());

    // When
    final TestObserver<String> obs = RxBillingClient.consumeAsync(client, params)
        .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertError(e -> e instanceof ConsumeException
        && ((ConsumeException) e).getResponseCode() == responseCode
        && ((ConsumeException) e).getDebugMessage().equals(debugMessage)
    );
  }

  @Test
  void isFeatureSupportedAsync_should_return_true_when_BillingResponseCode_is_OK() {
    // Given
    final BillingClient client = mock(BillingClient.class);
    final BillingResult result = mock(BillingResult.class);
    final String feature = SUBSCRIPTIONS;

    given(result.getResponseCode()).willReturn(BillingResponseCode.OK);
    given(client.isFeatureSupported(feature)).willReturn(result);

    // When
    final TestObserver<Boolean> obs = RxBillingClient.isFeatureSupportedAsync(client, feature)
        .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertValue(true);
    obs.assertNoErrors();
  }

  @Test
  void isFeatureSupportedAsync_should_return_false_when_BillingResponseCode_is_FEATURE_NOT_SUPPORTED() {
    // Given
    final BillingClient client = mock(BillingClient.class);
    final BillingResult result = mock(BillingResult.class);
    final String feature = SUBSCRIPTIONS;

    given(result.getResponseCode()).willReturn(BillingResponseCode.FEATURE_NOT_SUPPORTED);
    given(client.isFeatureSupported(feature)).willReturn(result);

    // When
    final TestObserver<Boolean> obs = RxBillingClient.isFeatureSupportedAsync(client, feature)
        .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertValue(false);
    obs.assertNoErrors();
  }

  @Test
  void isFeatureSupportedAsync_should_throw_when_BillingResponseCode_is_not_OK_or_FEATURE_NOT_SUPPORTED() {
    // Given
    final BillingClient client = mock(BillingClient.class);
    final BillingResult result = mock(BillingResult.class);
    final String feature = SUBSCRIPTIONS;
    final int responseCode = BillingResponseCode.ERROR;
    final String debugMessage = "Error";

    given(result.getResponseCode()).willReturn(responseCode);
    given(result.getDebugMessage()).willReturn(debugMessage);
    given(client.isFeatureSupported(feature)).willReturn(result);

    // When
    final TestObserver<Boolean> obs = RxBillingClient.isFeatureSupportedAsync(client, feature)
        .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertError(e -> e instanceof IsFeatureSupportedException
        && ((IsFeatureSupportedException) e).getResponseCode() == responseCode
        && ((IsFeatureSupportedException) e).getDebugMessage().equals(debugMessage)
    );
  }

  @Test
  void isReadyAsync_should_return_true_when_BillingClient_isReady_equals_true() {
    // Given
    final BillingClient client = mock(BillingClient.class);

    given(client.isReady()).willReturn(true);

    // When
    final TestObserver<Boolean> obs = RxBillingClient.isReadyAsync(client)
        .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertValue(true);
    obs.assertNoErrors();
  }

  @Test
  void isReadyAsync_should_return_false_when_BillingClient_isReady_equals_false() {
    // Given
    final BillingClient client = mock(BillingClient.class);

    given(client.isReady()).willReturn(false);

    // When
    final TestObserver<Boolean> obs = RxBillingClient.isReadyAsync(client)
        .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertValue(false);
    obs.assertNoErrors();
  }

  @Test
  void launchBillingFlowAsync_should_return_BillingResult() {
    // Given
    final BillingFlowParams params = mock(BillingFlowParams.class);
    final BillingClient client = mock(BillingClient.class);
    final BillingResult result = mock(BillingResult.class);
    final Activity activity = mock(Activity.class);

    given(client.launchBillingFlow(activity, params)).willReturn(result);

    // When
    final TestObserver<BillingResult> obs =
        RxBillingClient.launchBillingFlowAsync(client, activity, params)
            .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertValue(result);
    obs.assertNoErrors();
  }

  @Test
  void launchPriceChangeConfirmationFlowAsync_should_complete_when_BillingResponseCode_is_OK() {
    // Given
    final PriceChangeFlowParams params = mock(PriceChangeFlowParams.class);
    final BillingClient client = mock(BillingClient.class);
    final Activity activity = mock(Activity.class);

    doAnswer(invocationOnMock -> {
      final BillingResult result = mock(BillingResult.class);

      given(result.getResponseCode()).willReturn(BillingResponseCode.OK);

      invocationOnMock.getArgument(2, PriceChangeConfirmationListener.class)
          .onPriceChangeConfirmationResult(result);

      return null;
    })
        .when(client)
        .launchPriceChangeConfirmationFlow(eq(activity), eq(params), any());

    // When
    final TestObserver<Void> obs =
        RxBillingClient.launchPriceChangeConfirmationFlowAsync(client, activity, params)
            .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertComplete();
    obs.assertNoErrors();
  }

  @Test
  void launchPriceChangeConfirmationFlowAsync_should_throw_when_BillingResponseCode_is_not_OK() {
    // Given
    final PriceChangeFlowParams params = mock(PriceChangeFlowParams.class);
    final BillingClient client = mock(BillingClient.class);
    final Activity activity = mock(Activity.class);
    final int responseCode = BillingResponseCode.ERROR;
    final String debugMessage = "Error";

    doAnswer(invocationOnMock -> {
      final BillingResult result = mock(BillingResult.class);

      given(result.getResponseCode()).willReturn(responseCode);
      given(result.getDebugMessage()).willReturn(debugMessage);

      invocationOnMock.getArgument(2, PriceChangeConfirmationListener.class)
          .onPriceChangeConfirmationResult(result);

      return null;
    })
        .when(client)
        .launchPriceChangeConfirmationFlow(eq(activity), eq(params), any());

    // When
    final TestObserver<Void> obs =
        RxBillingClient.launchPriceChangeConfirmationFlowAsync(client, activity, params)
            .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertError(e -> e instanceof LaunchPriceChangeConfirmationFlowException
        && ((LaunchPriceChangeConfirmationFlowException) e).getResponseCode() == responseCode
        && ((LaunchPriceChangeConfirmationFlowException) e).getDebugMessage().equals(debugMessage)
    );
  }

  @Test void loadRewardedSkuAsync_should_complete_when_BillingResponseCode_is_OK() {
    // Given
    final RewardLoadParams params = mock(RewardLoadParams.class);
    final BillingClient client = mock(BillingClient.class);

    doAnswer(invocationOnMock -> {
      final BillingResult result = mock(BillingResult.class);

      given(result.getResponseCode()).willReturn(BillingResponseCode.OK);

      invocationOnMock.getArgument(1, RewardResponseListener.class)
          .onRewardResponse(result);

      return null;
    })
        .when(client)
        .loadRewardedSku(eq(params), any());

    // When
    final TestObserver<Void> obs = RxBillingClient.loadRewardedSkuAsync(client, params)
        .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertComplete();
    obs.assertNoErrors();
  }

  @Test void loadRewardedSkuAsync_should_throw_error_when_BillingResponseCode_is_not_OK() {
    // Given
    final RewardLoadParams params = mock(RewardLoadParams.class);
    final BillingClient client = mock(BillingClient.class);
    final int responseCode = BillingResponseCode.ERROR;
    final String debugMessage = "Error";

    doAnswer(invocationOnMock -> {
      final BillingResult result = mock(BillingResult.class);

      given(result.getResponseCode()).willReturn(responseCode);
      given(result.getDebugMessage()).willReturn(debugMessage);

      invocationOnMock.getArgument(1, RewardResponseListener.class)
          .onRewardResponse(result);

      return null;
    })
        .when(client)
        .loadRewardedSku(eq(params), any());

    // When
    final TestObserver<Void> obs = RxBillingClient.loadRewardedSkuAsync(client, params)
        .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertError(e -> e instanceof LoadRewardedSkuException
        && ((LoadRewardedSkuException) e).getResponseCode() == responseCode
        && ((LoadRewardedSkuException) e).getDebugMessage().equals(debugMessage)
    );
  }

  @Test void queryPurchaseHistoryAsync_should_return_value_when_BillingResponseCode_is_OK() {
    // Given
    final BillingClient client = mock(BillingClient.class);
    final String skuType = INAPP;
    final List<PurchaseHistoryRecord> list = new ArrayList<>();
    list.add(mock(PurchaseHistoryRecord.class));

    doAnswer(invocationOnMock -> {
      final BillingResult result = mock(BillingResult.class);

      given(result.getResponseCode()).willReturn(BillingResponseCode.OK);

      invocationOnMock.getArgument(1, PurchaseHistoryResponseListener.class)
          .onPurchaseHistoryResponse(result, list);

      return null;
    })
        .when(client)
        .queryPurchaseHistoryAsync(eq(skuType), any(PurchaseHistoryResponseListener.class));

    // When
    final TestObserver<List<PurchaseHistoryRecord>> obs =
        RxBillingClient.queryPurchaseHistoryAsync(client, skuType)
            .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertValue(list);
    obs.assertNoErrors();
  }

  @Test void queryPurchaseHistoryAsync_should_throw_error_when_BillingResponseCode_is_not_OK() {
    // Given
    final BillingClient client = mock(BillingClient.class);
    final int responseCode = BillingResponseCode.ERROR;
    final String debugMessage = "Error";
    final String skuType = INAPP;

    doAnswer(invocationOnMock -> {
      final BillingResult result = mock(BillingResult.class);

      given(result.getResponseCode()).willReturn(responseCode);
      given(result.getDebugMessage()).willReturn(debugMessage);

      invocationOnMock.getArgument(1, PurchaseHistoryResponseListener.class)
          .onPurchaseHistoryResponse(result, null);

      return null;
    })
        .when(client)
        .queryPurchaseHistoryAsync(eq(skuType), any(PurchaseHistoryResponseListener.class));

    // When
    final TestObserver<List<PurchaseHistoryRecord>> obs =
        RxBillingClient.queryPurchaseHistoryAsync(client, skuType)
            .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertError(e -> e instanceof QueryPurchaseHistoryException
        && ((QueryPurchaseHistoryException) e).getResponseCode() == responseCode
        && ((QueryPurchaseHistoryException) e).getDebugMessage().equals(debugMessage)
    );
  }

  @Test void querySkuDetailsAsync_should_return_value_when_BillingResponseCode_is_OK() {
    // Given
    final SkuDetailsParams params = mock(SkuDetailsParams.class);
    final BillingClient client = mock(BillingClient.class);
    final List<SkuDetails> list = new ArrayList<>();
    list.add(mock(SkuDetails.class));

    doAnswer(invocationOnMock -> {
      final BillingResult result = mock(BillingResult.class);

      given(result.getResponseCode()).willReturn(BillingResponseCode.OK);

      invocationOnMock.getArgument(1, SkuDetailsResponseListener.class)
          .onSkuDetailsResponse(result, list);

      return null;
    })
        .when(client)
        .querySkuDetailsAsync(eq(params), any(SkuDetailsResponseListener.class));

    // When
    final TestObserver<List<SkuDetails>> obs = RxBillingClient.querySkuDetailsAsync(client, params)
        .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertValue(list);
    obs.assertNoErrors();
  }

  @Test void querySkuDetailsAsync_should_throw_error_when_BillingResponseCode_is_not_OK() {
    // Given
    final SkuDetailsParams params = mock(SkuDetailsParams.class);
    final BillingClient client = mock(BillingClient.class);
    final int responseCode = BillingResponseCode.ERROR;
    final String debugMessage = "Error";

    doAnswer(invocationOnMock -> {
      final BillingResult result = mock(BillingResult.class);

      given(result.getResponseCode()).willReturn(responseCode);
      given(result.getDebugMessage()).willReturn(debugMessage);

      invocationOnMock.getArgument(1, SkuDetailsResponseListener.class)
          .onSkuDetailsResponse(result, null);

      return null;
    })
        .when(client)
        .querySkuDetailsAsync(eq(params), any(SkuDetailsResponseListener.class));

    // When
    final TestObserver<List<SkuDetails>> obs = RxBillingClient.querySkuDetailsAsync(client, params)
        .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertError(e -> e instanceof QuerySkuDetailsException
        && ((QuerySkuDetailsException) e).getResponseCode() == responseCode
        && ((QuerySkuDetailsException) e).getDebugMessage().equals(debugMessage)
    );
  }

  @Test void queryPurchasesAsync_should_return_value_when_BillingResponseCode_is_OK() {
    // Given
    final String skuType = INAPP;
    final BillingClient client = mock(BillingClient.class);
    final List<Purchase> list = new ArrayList<>();
    final Purchase.PurchasesResult purchasesResult = mock(Purchase.PurchasesResult.class);
    list.add(mock(Purchase.class));
    final BillingResult result = mock(BillingResult.class);

    doReturn(purchasesResult)
        .when(client)
        .queryPurchases(eq(skuType));

    doReturn(list)
        .when(purchasesResult)
        .getPurchasesList();

    doReturn(result)
        .when(purchasesResult)
        .getBillingResult();

    given(result.getResponseCode()).willReturn(BillingResponseCode.OK);

    // When
    final TestObserver<List<Purchase>> obs = RxBillingClient.queryPurchasesAsync(client, skuType)
        .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertValue(list);
    obs.assertNoErrors();
  }

  @Test void queryPurchasesAsync_should_throw_error_when_BillingResponseCode_is_not_OK() {
    // Given
    final String skuType = INAPP;
    final BillingClient client = mock(BillingClient.class);
    final int responseCode = BillingResponseCode.ERROR;
    final String debugMessage = "Error";
    final Purchase.PurchasesResult purchasesResult = mock(Purchase.PurchasesResult.class);
    final BillingResult result = mock(BillingResult.class);

    doReturn(purchasesResult)
        .when(client)
        .queryPurchases(eq(skuType));

    doReturn(result)
        .when(purchasesResult)
        .getBillingResult();

    given(result.getResponseCode()).willReturn(responseCode);
    given(result.getDebugMessage()).willReturn(debugMessage);

    // When
    final TestObserver<List<Purchase>> obs = RxBillingClient.queryPurchasesAsync(client, skuType)
        .test();
    obs.awaitTerminalEvent(50, TimeUnit.MILLISECONDS);

    // Then
    obs.assertError(e -> e instanceof RetrievePurchasesException
        && ((RetrievePurchasesException) e).getResponseCode() == responseCode
        && ((RetrievePurchasesException) e).getDebugMessage().equals(debugMessage)
    );
  }
}
