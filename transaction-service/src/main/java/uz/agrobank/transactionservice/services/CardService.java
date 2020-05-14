package uz.agrobank.transactionservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uz.agrobank.transactionservice.domain.Card;
import uz.agrobank.transactionservice.repositories.CardRepository;

import java.util.List;
import java.util.Optional;

@Service
@Component
@Slf4j
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public Optional<Card> insertCard(Card card) {
        card.setId(null);
        Card registeredCard = cardRepository.save(card);
        return Optional.ofNullable(registeredCard);

    }

    public Optional<Card> updateCard(Card card) {
        return Optional.ofNullable(cardRepository.save(card));

    }

    public Optional<List<Card>> getUserCards(String phone) {
        return cardRepository.findAllByUserId(phone);
    }

    public Optional<Card> getCardById(String cardId) {
        return cardRepository.findById(cardId);
    }

    public Optional<Card> getCardByCardNumber(String cardNumber) {
        return cardRepository.findByPan(cardNumber);
    }
}
