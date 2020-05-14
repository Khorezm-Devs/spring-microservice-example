package uz.agrobank.transactionservice.services;

import uz.agrobank.transactionservice.domain.Payment;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uz.agrobank.transactionservice.repositories.PaymentRepository;

import java.util.List;

/**
 *
 * @author armena
 */
@Service
@Component
@Slf4j
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Optional<Payment> addNewPayment(Payment payment) {
        Payment addedPayment = paymentRepository.save(payment);

        return Optional.ofNullable(addedPayment);
    }

    public Optional<List<Payment>> findAll(String userId) {
        Optional<List<Payment>> paymentList = paymentRepository.findAllByUserId(userId);

        return paymentList;
    }

    public Optional<Payment> findById(String id) {
        Optional<Payment> payment = paymentRepository.findById(id);

        return payment;
    }

}
