//
//  ConfiguredPurchases.swift
//  PurchaseTester
//
//  Created by Nacho Soto on 10/25/22.
//

import Foundation

import RevenueCat

public final class ConfiguredPurchases {

    public let purchases: Purchases
    private let delegate: Delegate

    public init(purchases: Purchases) {
        self.purchases = purchases
        self.delegate = Delegate()

        self.purchases.delegate = self.delegate
    }

    public convenience init(
        apiKey: String,
        proxyURL: String?,
        useStoreKit2: Bool,
        observerMode: Bool
    ) {
        Purchases.logLevel = .debug
        Purchases.logHandler = Self.logger.logHandler

        if let proxyURL {
            Purchases.proxyURL = URL(string: proxyURL)!
        }

        let purchases = Purchases.configure(
            with: .builder(withAPIKey: apiKey)
                .with(usesStoreKit2IfAvailable: useStoreKit2)
                .with(observerMode: observerMode)
                .build()
        )

        self.init(purchases: purchases)
    }

    // MARK: -

    public static let logger: Logger = .init()

}

private final class Delegate: NSObject, PurchasesDelegate {

    func purchases(_ purchases: Purchases, readyForPromotedProduct product: StoreProduct, purchase makeDeferredPurchase: @escaping StartPurchaseBlock) {
        makeDeferredPurchase { (transaction, customerInfo, error, success) in
            print("Yay")
        }
    }

}
