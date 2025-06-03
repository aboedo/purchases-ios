package com.revenuecat.samplecat

import android.app.Application
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.LogLevel

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize the RevenueCat SDK
        // Replace "YOUR_API_KEY" with your actual RevenueCat Google Play API key
        // For testing, you might use a sandbox key if available.
        val apiKey = "goog_YOUR_REVENUECAT_API_KEY" // Placeholder API Key

        Purchases.logLevel = LogLevel.DEBUG // Enable debug logs for development

        val builder = PurchasesConfiguration.Builder(this, apiKey)
        // Optionally, set an appUserID if you have one at this point
        // builder.appUserID("your_app_user_id")
        Purchases.configure(builder.build())

        // Optional: Set up a listener for ready state (e.g., to ensure SDK is ready before certain UI actions)
        // Purchases.isReadyForUse { isReady ->
        //     if (isReady) {
        //         // SDK is ready
        //     }
        // }
    }
}
