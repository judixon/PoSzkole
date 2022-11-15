package com.tomekw.poszkole.payments;

import com.tomekw.poszkole.payments.dtos.PaymentDto;
import com.tomekw.poszkole.payments.dtos.PaymentSaveDto;
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
        Long paymentId = paymentService.savePayment(paymentSaveDto);
        URI savedPaymentUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(paymentId)
                .toUri();
        return ResponseEntity.created(savedPaymentUri).body(paymentId);
    }

    @GetMapping("/{id}")
    ResponseEntity<PaymentDto> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPayment(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deletePayment(@PathVariable Long id){
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
