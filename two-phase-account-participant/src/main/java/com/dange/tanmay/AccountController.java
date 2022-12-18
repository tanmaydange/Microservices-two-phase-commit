package com.dange.tanmay;

import lombok.extern.log4j.Log4j;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Log4j
@RestController
public class AccountController {
    // @Autowired
    // NamedParameterJdbcTemplate jdbcTemplate;

    Map<String, Integer> accountMap = new HashMap<String, Integer>();
    Map<String, Object> transactionMap = new HashMap<String, Object>();
    
    AccountController(){
        accountMap.put("tanmay",1000);
        accountMap.put("jainesh",1000);
        accountMap.put("siya",1000);

    }



    @RequestMapping("/prepare")
    String prepare(@RequestParam(value = "txnId") String txnId,
        @RequestParam(value = "accountName") String accountName, 
        @RequestParam(value="amount") String amount){
        
            if (accountMap.containsKey(accountName)) {
                if (accountMap.get(accountName) >= Integer.parseInt(amount)){
                    Map<String,Integer> p = new HashMap<>();
                    p.put(accountName, Integer.parseInt(amount));
                    
                    transactionMap.put(txnId, p);
                    int value = accountMap.get(accountName) - Integer.parseInt(amount);
                    accountMap.put(accountName, value);
                    
                    log.info("Transation :" + transactionMap.toString());  
                    log.info("Bank :" + accountMap.toString());  
                
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
                String accountName = entry.getKey();
                Integer amount = entry.getValue();

                int value = accountMap.get(accountName) + amount;
                accountMap.put(accountName, value);
                transactionMap.remove(txnId);
            }
            log.info("Transation :" + transactionMap.toString());  
            log.info("Bank :" + accountMap.toString());  
                            
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
            log.info("Bank :" + accountMap.toString());  
                            
            return "True";
        }
        log.info("Invalid TxnId :" + txnId);
        return "False";
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
