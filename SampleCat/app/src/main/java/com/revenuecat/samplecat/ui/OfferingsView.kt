package com.revenuecat.samplecat.ui

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revenuecat.purchases.Offering
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.CustomerInfo

@Composable
fun OfferingsView(
    viewModel: MainViewModel
) {
    val offerings by viewModel.offerings.collectAsState()
    val customerInfo by viewModel.customerInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState() // For loading indicator

    val activity = LocalContext.current as Activity // For purchase call

    Column(modifier = Modifier.fillMaxSize()) {
        // Display Active Subscriptions/Entitlements
        customerInfo?.let { info ->
            ActiveSubscriptionsView(customerInfo = info)
        }

        if (isLoading && offerings == null) { // Show loading only if offerings are not yet loaded
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        offerings?.let { currentOfferings ->
            if (currentOfferings.all.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No offerings available at the moment.", style = MaterialTheme.typography.bodyLarge)
                }
                return@Column
            }
            LazyColumn(modifier = Modifier.weight(1f).padding(16.dp)) {
                currentOfferings.current?.let { offering -> // Display current offering first
                    item {
                         OfferingItem(
                            offering = offering,
                            customerInfo = customerInfo,
                            onPackageSelected = { pkg ->
                                viewModel.purchasePackage(activity, pkg)
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Other Available Offerings:", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                items(currentOfferings.all.values.filter { it.identifier != currentOfferings.current?.identifier }) { offering ->
                    OfferingItem(
                        offering = offering,
                        customerInfo = customerInfo,
                        onPackageSelected = { pkg ->
                            viewModel.purchasePackage(activity, pkg)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        } ?: run {
             if (!isLoading) { // Only show "no offerings" if not loading
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No offerings loaded.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun ActiveSubscriptionsView(customerInfo: CustomerInfo) {
    if (customerInfo.entitlements.active.isEmpty()) {
        Text(
            text = "No active subscriptions.",
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }
    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        Text(
            "Active Entitlements:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        customerInfo.entitlements.active.values.forEach { entitlement ->
            Text(
                text = "- ${entitlement.identifier} (expires: ${entitlement.expirationDate ?: "never"})",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
    }
}

@Composable
fun OfferingItem(
    offering: Offering,
    customerInfo: CustomerInfo?,
    onPackageSelected: (Package) -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(
                text = offering.serverDescription,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            offering.availablePackages.forEach { pkg ->
                PackageItem(
                    pkg = pkg,
                    customerInfo = customerInfo,
                    onSelected = { onPackageSelected(pkg) }
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun PackageItem(
    pkg: Package,
    customerInfo: CustomerInfo?,
    onSelected: () -> Unit
) {
    val product = pkg.product
    // Check if any active entitlement corresponds to this product
    val isSubscribed = customerInfo?.entitlements?.active?.values?.any { entitlement ->
        entitlement.productIdentifier == product.sku // For subscriptions
        // For non-subscriptions, you might need a different check based on your logic
        // or if the product ID is directly in a list of purchased non-consumables.
    } ?: false

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = product.title, style = MaterialTheme.typography.titleMedium)
            Text(text = product.description ?: "", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${pkg.identifier} - ${product.price?.formatted}", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onSelected, enabled = !isSubscribed) {
            Text(if (isSubscribed) "Subscribed" else "Buy")
        }
    }
}

// Preview needs a mock MainViewModel or simplified data
@Preview(showBackground = true)
@Composable
fun OfferingsViewPreview() {
    // This preview is hard to maintain without a proper mock ViewModel.
    // For now, a simple placeholder:
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Offerings View Preview (Live data needed)")
        }
    }
}
