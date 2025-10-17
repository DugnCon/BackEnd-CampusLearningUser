package com.javaweb.service;

import com.javaweb.model.dto.PayPalPaymentDTO;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PayPalService {

    private final APIContext apiContext;

    public PayPalService(APIContext apiContext) {
        this.apiContext = apiContext;
    }

    public Payment createPayment(PayPalPaymentDTO dto) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(dto.getCurrency());
        amount.setTotal(String.format("%.2f", dto.getTotal()));

        Transaction transaction = new Transaction();
        transaction.setDescription(dto.getDescription());
        transaction.setAmount(amount);

        Payer payer = new Payer();
        payer.setPaymentMethod(dto.getMethod());

        Payment payment = new Payment();
        payment.setIntent(dto.getIntent());
        payment.setPayer(payer);
        payment.setTransactions(Arrays.asList(transaction));

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(dto.getCancelUrl());
        redirectUrls.setReturnUrl(dto.getSuccessUrl());
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }
    
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecution);
    }

}
