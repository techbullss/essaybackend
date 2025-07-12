package essay.essay.controllers;
import essay.essay.Models.Order;
import essay.essay.Models.UserModel;
import essay.essay.Models.WalletTransaction;
import essay.essay.repository.OrderRepo;
import essay.essay.repository.UserRepo;
import essay.essay.repository.WalletTransactionRepo;
import essay.essay.services.PaypalVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class WalletController {
    @Autowired
    private UserRepo userRepository;
    @Autowired
    Optional<Order> order;

    @Autowired
    private WalletTransactionRepo walletTransactionRepo;
    @Autowired
    OrderRepo orderRepo;
 @Autowired
 PaypalVerificationService paypalVerificationService;
    @PostMapping("wallet/paypal-success")
    public Map<String, String> handlePaypalSuccess(@RequestBody Map<String, Object> payload) throws Exception {
        String txId = (String) payload.get("txId");
        String payerEmail = (String) payload.get("payerEmail");
        String userEmail =(String)payload.get("userEmail");
        double amount = Double.parseDouble(payload.get("amount").toString());

        UserModel user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
       WalletTransaction transaction =paypalVerificationService.verifyTransaction(txId);
        if (!transaction.getStatus().equals("COMPLETED")) {
            return (Map<String, String>) ResponseEntity.status(400).body("Transaction not completed");
        }

        WalletTransaction tx = new WalletTransaction();
        tx.setTxId(txId);
        tx.setPayerEmail(payerEmail);
        tx.setEmailFromUser(userEmail);
        tx.setAmount(amount);
        tx.setPaymentMethod("PayPal");
        tx.setUser(user);
        tx.setStatus(transaction.getStatus());

        // Save transaction
        walletTransactionRepo.save(tx);

        // Update balance
        user.setWalletBalance(user.getWalletBalance() + amount);
        userRepository.save(user);

        return Map.of("status", "success");
    }
    @GetMapping("/api/balance")
    public ResponseEntity<Double> getWalletBalance(@RequestParam String email) {
        Optional<UserModel> userOpt = userRepository.findByEmail(email);

        return userOpt.map(userModel -> ResponseEntity.ok(userModel.getWalletBalance())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(0.0));

    }
    @PutMapping("/updateWallet")
            public ResponseEntity<?> updateWallet(@RequestParam String email,@RequestParam double balance, @RequestParam String orderId){
        Optional<UserModel> userOpt = userRepository.findByEmail(email);
        Optional<Order> oder= orderRepo.findByOrderId(orderId);
        if(userOpt.isPresent()&& oder.isPresent()){
         UserModel user= userOpt.get();
         user.setWalletBalance(balance);
         Order order= oder.get();
         order.setPaymentStatus("paid");
            order.setOrderStatus("inprogress");
         orderRepo.save(order);
         userRepository.save(user);
            return ResponseEntity.ok("balance updated");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

    }

}

