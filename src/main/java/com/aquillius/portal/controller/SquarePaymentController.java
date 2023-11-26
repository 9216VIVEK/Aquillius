package com.aquillius.portal.controller;

import com.aquillius.portal.dto.MembershipPurchaseDto;
import com.aquillius.portal.dto.PurchaseDto;
import com.aquillius.portal.dto.PurchaseTokenDto;
import com.aquillius.portal.entity.*;
import com.aquillius.portal.entity.Invoice;
import com.aquillius.portal.enums.MembershipPlan;
import com.aquillius.portal.enums.AnnualPaymentType;
import com.aquillius.portal.enums.PaymentStatus;
import com.aquillius.portal.enums.PaymentType;
import com.aquillius.portal.model.*;
import com.aquillius.portal.repository.FinalAmountRepository;
import com.aquillius.portal.repository.MembershipTypeRepository;
import com.aquillius.portal.service.*;
import com.aquillius.portal.util.CalculateAmount;
import com.squareup.square.SquareClient;
import com.squareup.square.api.PaymentsApi;
import com.squareup.square.models.*;
import com.squareup.square.models.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.squareup.square.Environment;

import com.squareup.square.exceptions.ApiException;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/payment")
@CrossOrigin(origins = "*")
@Slf4j
public class SquarePaymentController {
    // The environment variable containing a Square Personal Access Token.
    // This must be set in order for the application to start.
    private static final String SQUARE_ACCESS_TOKEN_ENV_VAR = "EAAAEL8lta_G5TDKR5putrB4XnT_zaOokgK3WNxW23aCj34WS1icxXFa7S0a0x6X";

    // The environment variable containing a Square application ID.
    // This must be set in order for the application to start.
    private static final String SQUARE_APP_ID_ENV_VAR = "sandbox-sq0idb-nGVn15ar5AtWM1Cp3Mtd4Q";

    // The environment variable containing a Square location ID.
    // This must be set in order for the application to start.
    private static final String SQUARE_LOCATION_ID_ENV_VAR = "LFR9PV185PGA4";

    // The environment variable indicate the square environment - sandbox or
    // production.
    // This must be set in order for the application to start.
    private static final String SQUARE_ENV_ENV_VAR = "sandbox";

    private final SquareClient squareClient;
    private final String squareLocationId;
    private final String squareAppId;
    private final String squareEnvironment;

    public SquarePaymentController() throws ApiException {
        squareEnvironment = SQUARE_ENV_ENV_VAR;
        squareAppId = SQUARE_APP_ID_ENV_VAR;
        squareLocationId = SQUARE_LOCATION_ID_ENV_VAR;

        squareClient = new SquareClient.Builder()
                .environment(Environment.SANDBOX)
                .accessToken(SQUARE_ACCESS_TOKEN_ENV_VAR)// Remove or replace this detail when building your own app
                .build();
    }

    private String mustLoadEnvironmentVariable(String name) {
        String value = System.getenv(name);
        if (value == null || value.length() == 0) {
            throw new IllegalStateException(
                    String.format("The %s environment variable must be set", name));
        }

        return value;
    }

    @GetMapping("/online")
    public ResponseEntity<?> index(Principal principal) throws InterruptedException, ExecutionException {

//        Map<String, Object> model=new HashMap<>();
        // Get currency and country for location
        Map<String, Object> model = new HashMap<>();
//        User user=userService.findByEmail(principal.getName());
//        Amount amount = new Amount();
//		try {
//			amount = membershipService.getMembershipPurchaseAmount(purchaseDto, user);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
        RetrieveLocationResponse locationResponse = getLocationInformation(squareClient).get();
        model.put("paymentFormUrl",
                squareEnvironment.equals("sandbox") ? "https://sandbox.web.squarecdn.com/v1/square.js"
                        : "https://web.squarecdn.com/v1/square.js");
        model.put("locationId", squareLocationId);
        model.put("appId", squareAppId);
        model.put("currency", locationResponse.getLocation().getCurrency());
        model.put("country", locationResponse.getLocation().getCountry());
        model.put("idempotencyKey", UUID.randomUUID().toString());
//        model.put("purchaseDto", purchaseDto);
//        model.put("amount", amount);

        return ResponseEntity.ok(model);
    }

    @Autowired
    UserService userService;
    @Autowired
    SquarePaymentService squarePaymentService;
    @Autowired
    FinalAmountRepository finalAmountRepository;

    @PostMapping("/process-payment")
    public ResponseEntity<?> processPayment(@RequestBody PurchaseTokenDto purchaseTokenDto, Principal principal)
            throws InterruptedException, ExecutionException {
        // To learn more about splitting payments with additional recipients,
        // see the Payments API documentation on our [developer site]
        // (https://developer.squareup.com/docs/payments-api/overview).


        log.info("============inside processPayment Api==============");
        TokenWrapper tokenObject = purchaseTokenDto.tokenWrapper();
        System.out.println("token = " + tokenObject.getToken());
        System.out.println("idempotency key = " + tokenObject.getIdempotencyKey());
        MembershipPurchaseDto membershipPurchaseDto = purchaseTokenDto.purchaseDto();
        System.out.println("membershipPurchaseDto = " + membershipPurchaseDto);
        User user = userService.findByEmail(principal.getName());

        Optional<FinalAmount> amountOptional = finalAmountRepository.findById(purchaseTokenDto.amountId());
        if(amountOptional.isEmpty()) {
            log.error("Final amount not found");
            return ResponseEntity.ok(new Message(0, "Something went wrong"));
        }
        FinalAmount amount=amountOptional.get();
        System.out.println("amount = " + amount.getTotalAmount());
        // Get currency for location
        RetrieveLocationResponse locationResponse = getLocationInformation(squareClient).get();
        System.out.println("location response");
        String currency = locationResponse.getLocation().getCurrency();
        System.out.println("currency = " + currency);

        Money bodyAmountMoney = new Money.Builder()
                .amount((long) amount.getTotalAmount() * 100)
                .currency(currency)
                .build();

        System.out.println("Money object built");
        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest
                .Builder(tokenObject.getToken(), tokenObject.getIdempotencyKey(), bodyAmountMoney)
                .build();
        System.out.println("Payment Request built");

        PaymentsApi paymentsApi = squareClient.getPaymentsApi();
        System.out.println("payment aoi fetched");

        return paymentsApi.createPaymentAsync(createPaymentRequest).thenApply(result -> {
            System.out.println("11111111111111111");
            try {
                String invoiceFilePath =
                        squarePaymentService.makeMembershipPayment(purchaseTokenDto, user, amount);
//                SquarePurchaseResponse squarePurchaseResponse =
//                        new SquarePurchaseResponse(new PaymentResult("SUCCESS", null), invoiceFilePath);
                System.out.println("22222222222222222");
                return ResponseEntity.ok(new PaymentResult("SUCCESS", null));
            } catch (Exception e) {
                e.printStackTrace();
                List<Error> list=new ArrayList<>();
                Error error=new Error(e.getMessage(), e.getMessage(), e.getMessage(), e.getMessage());
                list.add(error);
                return ResponseEntity.ok(new PaymentResult("FAILURE", list));
               // return ResponseEntity.ok(new Message(1, "Something went Wrong"));
            }
        }).exceptionally(exception -> {
            System.out.println("33333333333333333");
            exception.printStackTrace();
            ApiException e = (ApiException) exception.getCause();
            System.out.println("Failed to make the request");
            System.out.printf("Exception: %s%n", e.getMessage());
            return ResponseEntity.ok(new PaymentResult("FAILURE", e.getErrors()));
          //  return ResponseEntity.ok(new Message(0, "Payment Failed"));
        }).join();
    }

    /**
     * Helper method that makes a retrieveLocation API call using the configured
     * locationId and returns the future containing the response
     *
     * @param squareClient the API client
     * @return a future that holds the retrieveLocation response
     */
    private CompletableFuture<RetrieveLocationResponse> getLocationInformation(
            SquareClient squareClient) {
        return squareClient.getLocationsApi().retrieveLocationAsync(squareLocationId)
                .thenApply(result -> {
                    return result;
                })
                .exceptionally(exception -> {
                    System.out.println("Failed to make the request");
                    System.out.printf("Exception: %s%n", exception.getMessage());
                    return null;
                });
    }
}
