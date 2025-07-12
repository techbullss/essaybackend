package essay.essay.services;
import org.springframework.http.HttpHeaders;

import essay.essay.Models.WalletTransaction;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.net.URI;
import java.net.http.HttpClient;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Map;
@Service
public class PaypalVerificationService {
    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;
    public WalletTransaction verifyTransaction(String txId) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-m.sandbox.paypal.com/v2/checkout/orders/" + txId)) // use sandbox if testing
               .header("Authorization", "Bearer " + getPayPalAccessToken())
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new Exception("Invalid PayPal transaction");

        JSONObject json = new JSONObject(response.body());
        String status = json.getString("status");
        String amount = json.getJSONArray("purchase_units")
                .getJSONObject(0)
                .getJSONObject("amount")
                .getString("value");
        String payerEmail = json.getJSONObject("payer").getString("email_address");

        WalletTransaction trans= new WalletTransaction();
 trans.setTxId(txId);
 trans.setAmount(Double.parseDouble(amount));
 trans.setPayerEmail(payerEmail);
trans.setStatus(status);
        return trans;
    }
    private String getPayPalAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        String auth = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + auth);

        HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api-m.sandbox.paypal.com/v1/oauth2/token\n",
                HttpMethod.POST,
                entity,
                Map.class
        );

        Map<String, Object> body = response.getBody();
        return (String) body.get("access_token");
    }
}
