package com.rafaj2ee.DTO;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class PixDTO {

    @NotBlank(message = "Description cannot be blank.")
    @Size(max = 50, message = "Description cannot be longer than 50 characters.")
    private String description;

    @NotNull(message = "Amount cannot be null.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0.")
    private BigDecimal amount;

    @NotNull(message = "Transaction date cannot be null.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Transaction date must be in the format 'yyyy-MM-dd'.")
    private String transactionDate;

    @NotBlank(message = "Pix key cannot be blank.")
    private String pixKey;

    @NotBlank(message = "Merchant city cannot be blank.")
    private String merchantCity;

    @NotBlank(message = "Merchant name cannot be blank.")
    private String merchantName;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
        this.amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
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

}
