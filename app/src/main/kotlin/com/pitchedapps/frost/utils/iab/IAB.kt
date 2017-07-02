package com.pitchedapps.frost.utils.iab

import android.app.Activity
import android.content.Context
import ca.allanwang.kau.utils.isFromGooglePlay
import com.crashlytics.android.answers.PurchaseEvent
import com.pitchedapps.frost.BuildConfig
import com.pitchedapps.frost.SettingsActivity
import com.pitchedapps.frost.utils.L
import com.pitchedapps.frost.utils.Prefs
import com.pitchedapps.frost.utils.frostAnswers
import com.pitchedapps.frost.utils.frostAnswersCustom

/**
 * Created by Allan Wang on 2017-06-23.
 */
object IAB {

    var helper: IabHelper? = null

    fun setupAsync(activity: Activity) {
        if (helper == null) {
            L.d("IAB setup async")
            if (!activity.isFromGooglePlay && !BuildConfig.DEBUG) return L.d("IAB not from google play")
            try {
                helper = IabHelper(activity.applicationContext, PUBLIC_BILLING_KEY)
                helper!!.enableDebugLogging(BuildConfig.DEBUG, "Frost:")
                helper!!.startSetup {
                    result ->
                    L.d("IAB result ${result.message}")
                    if (!result.isSuccess) L.eThrow("IAB Setup error: $result")
                }
            } catch (e: Exception) {
                L.e(e, "IAB error")
                activity.playStoreNoLongerPro()
            }
        }
    }

    /**
     * If user has pro, check if it's valid and destroy the helper
     */
    fun validatePro(activity: Activity) {

    }
}

private const val FROST_PRO = "frost_pro"

val IS_FROST_PRO: Boolean
    get() = (BuildConfig.DEBUG && Prefs.debugPro) || Prefs.previouslyPro

private val Context.isFrostPlay: Boolean
    get() = isFromGooglePlay || BuildConfig.DEBUG

fun SettingsActivity.restorePurchases() {

}

fun Activity.openPlayProPurchase(code: Int) = openPlayPurchase(FROST_PRO, code) {
    Prefs.previouslyPro = true
}

fun Activity.openPlayPurchase(key: String, code: Int, onSuccess: (key: String) -> Unit) {
    L.d("Open play purchase $key $code")
    if (!isFrostPlay) return playStoreNotFound()
    frostAnswersCustom("PLAY_PURCHASE") {
        putCustomAttribute("Key", key)
    }
    L.d("IAB flag end async")
    IAB.helper?.flagEndAsync() ?: return playStoreGenericError("Null flag end async")
    L.d("IAB query inv async")
    try {
        IAB.helper!!.queryInventoryAsync {
            res, inv ->
            if (res.isFailure) return@queryInventoryAsync playStoreGenericError("Query res error")
            if (inv?.getSkuDetails(key) != null) return@queryInventoryAsync playStoreAlreadyPurchased(key)
            L.d("IAB: inventory ${inv.allOwnedSkus}")
            IAB.helper!!.launchPurchaseFlow(this@openPlayPurchase, key, code) {
                result, _ ->
                if (result.isSuccess) {
                    onSuccess(key)
                    playStorePurchasedSuccessfully(key)
                }
                frostAnswers {
                    logPurchase(PurchaseEvent()
                            .putItemId(key)
                            .putCustomAttribute("result", result.message)
                            .putSuccess(result.isSuccess))
                }
            }
        }
    } catch(e: IabHelper.IabAsyncInProgressException) {
        L.e(e, "IAB query dup")
    }
}