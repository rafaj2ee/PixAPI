package com.rafaj2ee.resolver;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rafaj2ee.dto.PurchaseTransactionDTO;
import com.rafaj2ee.model.PurchaseTransaction;
import com.rafaj2ee.repository.PurchaseTransactionRepository;
import com.rafaj2ee.util.Constant;

import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class PurchaseTransactionResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    @Autowired
    private PurchaseTransactionRepository repository;

    public PurchaseTransaction getPurchaseTransaction(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<PurchaseTransaction> getAllPurchaseTransactions() {
        return repository.findAll();
    }

    public PurchaseTransaction createPurchaseTransaction(PurchaseTransactionDTO input) {
        PurchaseTransaction transaction = new PurchaseTransaction();
        transaction.setDescription(input.getDescription());
        transaction.setAmount(input.getAmount());
        transaction.setTransactionDate(LocalDate.parse(input.getTransactionDate(), Constant.FORMAT).atStartOfDay());
        return repository.save(transaction);
    }

    public PurchaseTransaction updatePurchaseTransaction(Long id, PurchaseTransactionDTO input) {
        Optional<PurchaseTransaction> optionalTransaction = repository.findById(id);
        if (!optionalTransaction.isPresent()) {
            throw new RuntimeException("PurchaseTransaction not found");
        }
        
        PurchaseTransaction transaction = optionalTransaction.get();
        transaction.setDescription(input.getDescription());
        transaction.setAmount(input.getAmount());
        transaction.setTransactionDate(LocalDate.parse(input.getTransactionDate(), Constant.FORMAT).atStartOfDay());
        return repository.save(transaction);
    }
}

