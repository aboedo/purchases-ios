package com.revenuecat.samplecat.ui

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesErrorException
import com.revenuecat.purchases.PurchasesTransactionException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _offerings = MutableStateFlow<Offerings?>(null)
    val offerings: StateFlow<Offerings?> = _offerings.asStateFlow()

    private val _customerInfo = MutableStateFlow<CustomerInfo?>(null)
    val customerInfo: StateFlow<CustomerInfo?> = _customerInfo.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        // Purchases SDK is initialized in MainApplication.kt

        viewModelScope.launch {
            loadInitialData()
        }

        // Set up listener for customer info updates
        // Note: This listener is now set via Purchases.sharedInstance.updatedCustomerInfoStream
        // However, for simplicity and directness with StateFlow, updating manually after
        // key actions (like purchase or refresh) is also common.
        // The listener in init of MainApplication is good for global updates.
        // For this ViewModel, we'll primarily rely on explicit fetches or updates post-action.
        // Or, we can subscribe to the stream here:
        // Purchases.sharedInstance.updatedCustomerInfoStream.asLiveData().observeForever { ... }
        // For now, keeping it simple by fetching on init and after purchase.
         Purchases.sharedInstance.updatedCustomerInfoListener =
             Purchases.UpdatedCustomerInfoListener { info ->
                 _customerInfo.value = info
             }
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Fetch customer info first to see if they have existing subscriptions
                _customerInfo.value = Purchases.sharedInstance.getCustomerInfo()
                // Then fetch offerings
                _offerings.value = Purchases.sharedInstance.getOfferings()
            } catch (e: PurchasesErrorException) {
                Log.e("MainViewModel", "Error fetching initial data", e)
                _error.value = "Error fetching data: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun purchasePackage(activity: Activity, packageToPurchase: Package) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val purchaseResult = Purchases.sharedInstance.purchasePackage(
                    activity,
                    packageToPurchase
                )
                _customerInfo.value = purchaseResult.customerInfo
                // Handle successful purchase (e.g., log, analytics, update UI state)
                Log.i("MainViewModel", "Purchase successful for product: ${purchaseResult.storeTransaction.productIds.firstOrNull()}")
                // You might want to clear the error on successful purchase, if any was set from previous attempts
                _error.value = null
            } catch (e: PurchasesTransactionException) {
                Log.e("MainViewModel", "Purchase transaction error", e)
                if (e.userCancelled) {
                    _error.value = "Purchase cancelled by user."
                } else {
                    _error.value = "Purchase failed: ${e.localizedMessage}"
                }
            } catch (e: PurchasesErrorException) {
                // This might catch other errors during the purchase setup, though less common
                Log.e("MainViewModel", "Generic purchase error", e)
                _error.value = "Purchase error: ${e.localizedMessage}"
            }
            finally {
                _isLoading.value = false
            }
        }
    }
}
