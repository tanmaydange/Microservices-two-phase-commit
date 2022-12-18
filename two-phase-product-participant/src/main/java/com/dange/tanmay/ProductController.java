package com.dange.tanmay;

import lombok.extern.log4j.Log4j;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@Log4j
@RestController
public class ProductController {
    // @Autowired
    // NamedParameterJdbcTemplate jdbcTemplate;

    Map<String, Integer> productMap = new HashMap<String, Integer>();
    Map<String, Integer> priceMap = new HashMap<String, Integer>();

    Map<String, Object> transactionMap = new HashMap<String, Object>();
    
    ProductController(){
        //Available Quantities 
        productMap.put("apple",100);
        productMap.put("orange",100);
        productMap.put("banana",100);

        // Product prices
        priceMap.put("apple",20);
        priceMap.put("orange",35);
        priceMap.put("banana",10);

    }


    @RequestMapping("/prepare")
    String prepare(@RequestParam(value = "txnId") String txnId,
        @RequestParam(value = "product") String product, 
        @RequestParam(value="quantity") String quantity){
        
            if (productMap.containsKey(product)) {
                if (productMap.get(product) >= Integer.parseInt(quantity)){
                    Map<String,Integer> p = new HashMap<>();
                    p.put(product, Integer.parseInt(quantity));
                    
                    transactionMap.put(txnId, p);
                    int value = productMap.get(product) - Integer.parseInt(quantity);
                    productMap.put(product, value);
                    
                    log.info("Transation :" + transactionMap.toString());  
                    log.info("Inventory :" + productMap.toString());  
                
                    return "True";
            }
        }     
        return "False";
    }

    @SuppressWarnings("unchecked") 
    @RequestMapping("/rollback")
    String rollback(@RequestParam(value = "txnId") String txnId){
        
        if (transactionMap.containsKey(txnId)) {
            Map<String, Integer> pMap = (HashMap<String, Integer>) transactionMap.get(txnId);
            
            for(Map.Entry<String, Integer> entry : pMap.entrySet()) {
                String product = entry.getKey();
                Integer quantity = entry.getValue();

                int value = productMap.get(product) + quantity;
                productMap.put(product, value);
                transactionMap.remove(txnId);
            }
            log.info("Transation :" + transactionMap.toString());  
            log.info("Inventory :" + productMap.toString());  
                            
            return "True";
        }
        log.info("Invalid TxnId :" + txnId);
        return "False";
    }



    @RequestMapping("/commit")
    String commit(@RequestParam(value = "txnId") String txnId){
        
        if (transactionMap.containsKey(txnId)) {
            transactionMap.remove(txnId);
            log.info("Transation :" + transactionMap.toString());  
            log.info("Inventory :" + productMap.toString());  
                            
            return "True";
        }
        log.info("Invalid TxnId :" + txnId);
        return "False";
    }

    @RequestMapping("/getPrice")
    Integer getPrice(@RequestParam(value = "product") String product){
        if (priceMap.containsKey(product)) {
            return priceMap.get(product);
        }    
        return 0;
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
