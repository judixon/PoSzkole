package com.tomekw.poszkole.payments;

import com.tomekw.poszkole.payments.DTOs_Mappers.PaymentDto;
import com.tomekw.poszkole.payments.DTOs_Mappers.PaymentSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    ResponseEntity<List<PaymentDto>> getPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @PostMapping
    ResponseEntity<?> savePayment(PaymentSaveDto paymentSaveDto){
        PaymentDto savedPayment = paymentService.savePayment(paymentSaveDto);
        URI savedPaymentUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPayment.getId())
                .toUri();
        return ResponseEntity.created(savedPaymentUri).body(savedPayment);
    }

    @GetMapping("/{id}")
    ResponseEntity<PaymentDto> getPayment(@PathVariable Long id) {
        return paymentService.getPayment(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deletePayment(@PathVariable Long id){
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
