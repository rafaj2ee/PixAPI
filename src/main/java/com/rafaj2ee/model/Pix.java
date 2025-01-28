package com.rafaj2ee.model;

import javax.persistence.*;

@Entity
public class Pix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pixKey;
    private String merchantCity;
    private String merchantName;
    private String pixCode;

    @OneToOne
    @JoinColumn(name = "purchase_id")
    private PurchaseTransaction purchaseTransaction;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPixKey() {
		return pixKey;
	}

	public void setPixKey(String pixKey) {
		this.pixKey = pixKey;
	}

	public String getMerchantCity() {
		return merchantCity;
	}

	public void setMerchantCity(String merchantCity) {
		this.merchantCity = merchantCity;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getPixCode() {
		return pixCode;
	}

	public void setPixCode(String pixCode) {
		this.pixCode = pixCode;
	}

	public PurchaseTransaction getPurchaseTransaction() {
		return purchaseTransaction;
	}

	public void setPurchaseTransaction(PurchaseTransaction purchaseTransaction) {
		this.purchaseTransaction = purchaseTransaction;
	}

    // Getters and setters
}
