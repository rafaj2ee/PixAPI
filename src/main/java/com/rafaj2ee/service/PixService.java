package com.rafaj2ee.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rafaj2ee.DTO.PixDTO;
import com.rafaj2ee.model.Pix;
import com.rafaj2ee.model.PurchaseTransaction;
import com.rafaj2ee.repository.PixRepository;
import com.rafaj2ee.repository.PurchaseTransactionRepository;
import com.rafaj2ee.util.Constant;
import com.rafaj2ee.util.PixCopyPasteGenerator;

@Service
public class PixService {

    @Autowired
    private PixRepository pixRepository;

    @Autowired
    private PurchaseTransactionRepository purchaseTransactionRepository;

    public PurchaseTransaction saveTransaction(PixDTO dto) throws Exception {
        // Convert transactionDate to LocalDateTime
        LocalDateTime transactionDate = LocalDate.parse(dto.getTransactionDate(), Constant.FORMAT).atStartOfDay();

        // Generate Pix Copy and Paste code
        String pixCode = PixCopyPasteGenerator.generatePixCopyPaste(dto.getPixKey(), dto.getAmount().toString(), dto.getMerchantCity(), dto.getMerchantName());

        // Create and save PurchaseTransaction entity
        PurchaseTransaction purchaseTransaction = new PurchaseTransaction();
        purchaseTransaction.setDescription(dto.getDescription());
        purchaseTransaction.setAmount(dto.getAmount());
        purchaseTransaction.setTransactionDate(transactionDate);

        Pix pix = new Pix();
        pix.setPixKey(dto.getPixKey());
        pix.setMerchantCity(dto.getMerchantCity());
        pix.setMerchantName(dto.getMerchantName());
        pix.setPixCode(pixCode);
        pix.setPurchaseTransaction(purchaseTransaction);

        //purchaseTransaction.setPix(pix);

        purchaseTransactionRepository.save(purchaseTransaction);

        return purchaseTransaction;
    }
}
