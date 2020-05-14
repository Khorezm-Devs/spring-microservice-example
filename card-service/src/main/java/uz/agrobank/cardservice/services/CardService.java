package uz.agrobank.cardservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uz.agrobank.cardservice.domain.Card;
import uz.agrobank.cardservice.domain.GeneralStatus;
import uz.agrobank.cardservice.repositories.CardRepository;

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

    public Optional<List<Card>> getUserCards(String phone) {
        return cardRepository.findAllByUserId(phone);
    }

    public Optional<Card> getByPan(String pan) {
        return cardRepository.findByPan(pan);
    }

    public Optional<Card> getCardById(String cardId) {
        return cardRepository.findById(cardId);
    }

    public void updateCard(Card updatedCard) {
        Card registeredCard = cardRepository.save(updatedCard);
    }

    public void deleteCard(Card deletedCard) {
//        deletedCard.setStatus(GeneralStatus.DELETED);
        cardRepository.delete(deletedCard);
    }
}
