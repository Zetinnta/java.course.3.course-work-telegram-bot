package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.RequestAlreadyExistsException;
import pro.sky.telegrambot.exception.ResponseNotFoundException;
import pro.sky.telegrambot.model.Interaction;
import pro.sky.telegrambot.repository.InteractionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

@Service
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final Logger logger = LoggerFactory.getLogger(InteractionService.class);

    public InteractionService(InteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }

    public Interaction addResponse(Interaction interaction) {
        if (interactionRepository.findReplyByRequest(interaction.getRequest()).isEmpty()) {
            return interactionRepository.save(interaction);
        } else {
            throw new RequestAlreadyExistsException("There is already exactly the same response");
        }
    }

    //public Interaction updateResponse

    public Collection<Interaction> getAllResponses() {
        Collection<Interaction> responseList = interactionRepository.findAll();
        if (responseList.isEmpty()) {
            throw new ResponseNotFoundException("There is no such response");
        }
        return responseList;
    }
}
