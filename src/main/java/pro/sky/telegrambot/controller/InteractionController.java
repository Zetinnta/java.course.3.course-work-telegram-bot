package pro.sky.telegrambot.controller;

import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambot.model.Interaction;
import pro.sky.telegrambot.service.InteractionService;

@RestController
@RequestMapping("/requests")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping
    public Interaction addInteraction(@RequestBody Interaction interaction) {
        return interactionService.addInteraction(interaction);
    }

    @GetMapping
    public Interaction getRequest(@RequestParam(value = "request") String request) {
        return interactionService.find
    }
}
