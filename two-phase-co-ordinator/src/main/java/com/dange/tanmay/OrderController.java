package com.dange.tanmay;

import lombok.extern.log4j.Log4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Log4j
@RestController
public class OrderController {
    // @Autowired
    // NamedParameterJdbcTemplate jdbcTemplate;
    
    @Autowired
    RestTemplate restTemplate;
    
    int txnId=1;

    @RequestMapping("/createOrder")
    String createOrder(@RequestParam(value = "accountName") String accountName,
        @RequestParam(value = "product") String product, 
        @RequestParam(value="quantity") String quantity){
          
            log.info("\nRecieved Create Order :" +
            "\nProduct :" + product +
            "\nQuantity :" + quantity +
            "\naccountName : " + accountName);
            
            log.info("Fetching Price of Product");
            String url="http://localhost:8085/getPrice?product="+product;

            String response = fetch(url);
            log.info("Price of "+product +": "+ response);
            
            log.info("Phase 1 : Prepare");

            String url1="http://localhost:8085/prepare?txnId="+txnId+"&product="+ product+ "&quantity=" + quantity;
            int amount = Integer.parseInt(response) * Integer.parseInt(quantity);
            String url2="http://localhost:8086/prepare?txnId="+txnId+"&accountName="+ accountName+ "&amount=" + amount;
            
            Boolean response1 = Boolean.parseBoolean(fetch(url1));
            Boolean response2 = Boolean.parseBoolean(fetch(url2));

            if (response1 && response2){

                log.info("Phase 2 : Commit");
                String url3="http://localhost:8085/commit?txnId="+txnId;
                fetch(url3);
                String url4="http://localhost:8086/commit?txnId="+txnId;
                fetch(url4);
                log.info("Transaction Completed Successfully");

                String finalResponse = "{ status : Transaction Complete," +
                "Customer_Name :" + accountName + "," +
                "Product : " + product + "," +
                "Quantity : " + quantity + "," +
                "Total : " + String.valueOf(amount) +
                "}"; 
                return finalResponse;
            } else {
                log.info("Phase 2 : Rollback");
                String url3="http://localhost:8085/rollback?txnId="+txnId;
                fetch(url3);
                String url4="http://localhost:8086/rollback?txnId="+txnId;
                fetch(url4);
                log.info("Transaction Rolledback Successfully");
                
            }

            txnId++;
            String FailureReason = "";
            if (!response1){
                FailureReason = "Insufficient Product Quantity in Inventory";
            }
            if (!response2){
                FailureReason = "Insufficient balance in Account";
            }


            String finalResponse = "{ status : Transaction Failed," +
            "Reason :" + FailureReason + "," +
            "Customer_Name :" + accountName + "," +
            "Product : " + product + "," +
            "Quantity : " + quantity + "," +
            "Total : " + String.valueOf(amount) +
            "}"; 

        return finalResponse;
    }


    private String fetch(String url) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity <String> entity = new HttpEntity<String>(headers);
        String response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
        return response;
    }


   

    // @Data
    // static class Result {
    //     private final int left;
    //     private final int right;
    //     private final long answer;
    // }

    // // SQL sample
    // @RequestMapping("calc")
    // Result calc(@RequestParam int left, @RequestParam int right) {
    //     MapSqlParameterSource source = new MapSqlParameterSource()
    //             .addValue("left", left)
    //             .addValue("right", right);
    //     return jdbcTemplate.queryForObject("SELECT :left + :right AS answer", source,
    //             (rs, rowNum) -> new Result(left, right, rs.getLong("answer")));
    // }
}
