package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.RequestAlreadyExistsException;
import pro.sky.telegrambot.exception.ResponseNotFoundException;
import pro.sky.telegrambot.model.Interaction;
import pro.sky.telegrambot.repository.InteractionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;

@Service
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final Logger logger = LoggerFactory.getLogger(InteractionService.class);

    public InteractionService(InteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }

    public Interaction postInteraction(Interaction interaction) {
        if (interactionRepository.findResponseByRequest(interaction.getRequest()).isEmpty()) {
            return interactionRepository.save(interaction);
        } else {
            logger.error("An error has occured when the method *postInteraction* was invoked");
            throw new RequestAlreadyExistsException("There is already exactly the same response on that particular request (" + interaction.getRequest() + ").");
        }
    }

    public Interaction editInteraction(Interaction interaction) {
        if (interactionRepository.findResponseByRequest(interaction.getRequest()).isEmpty()) {
            logger.error("An error has occured when the method *updateInteraction* was invoked");
            throw new ResponseNotFoundException("There is no response message for such a request (" + interaction.getRequest() + ").");
        }
        return interactionRepository.save(interaction);
    }

    public Interaction deleteInteraction(Interaction interaction) {
        if (interactionRepository.findResponseByRequest(interaction.getRequest()).isEmpty()) {
            logger.error("An error has occured when the method *deleteInteraction* was invoked");
            throw new ResponseNotFoundException("There is no response message for such a request (" + interaction.getRequest() + ").");
        } else {
            interactionRepository.delete(interaction);
            return interaction;
        }
    }

    public Collection<Interaction> getAllPossibleInteractions() {
        Collection<Interaction> responseList = interactionRepository.findAll();
        if (responseList.isEmpty()) {
            logger.error("An error has occured when the methos *getAllPossibleInteractions* was invoked");
            throw new ResponseNotFoundException("Unfortunately there is no interactions ever.");
        }
        return responseList;
    }

    public String getResponseByRequest(String request) {
        Optional<Interaction> response = interactionRepository.findResponseByRequest(request);
        if (response.isEmpty()) {
            logger.error("An error appeared when the method *getResponseByRequest was invoked");
            throw new ResponseNotFoundException("There is no response message for such a request (" + request + ").");
        }
        return request;
    }
}
