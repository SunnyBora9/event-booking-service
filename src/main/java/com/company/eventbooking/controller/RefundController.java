package com.company.eventbooking.controller;

import com.company.eventbooking.entity.Refund;
import com.company.eventbooking.service.RefundService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
@SecurityRequirement(name="bearerAuth")
public class RefundController {

    @Autowired
    private RefundService refundService;

    @PostMapping("/{bookingId}")
    public ResponseEntity<String> refund(@PathVariable Long bookingId){
        try {
            String refunded=refundService.initiateRefund(bookingId);
            return ResponseEntity.ok("Refund initiated successfully"+"\n"+refunded);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }

    }
}
