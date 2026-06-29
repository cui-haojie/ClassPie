package org.example.classpiserver.controller.interaction;

import org.example.classpiserver.dto.interaction.AddCourseInteractionRequest;
import org.example.classpiserver.dto.interaction.AskInteractionQuestionRequest;
import org.example.classpiserver.dto.interaction.CloseInteractionRequest;
import org.example.classpiserver.dto.interaction.InteractionDetailDTO;
import org.example.classpiserver.dto.interaction.PickRandomStudentRequest;
import org.example.classpiserver.dto.interaction.PickRandomStudentResult;
import org.example.classpiserver.dto.interaction.SubmitInteractionRequest;
import org.example.classpiserver.dto.test.GetTestDetailRequest;
import org.example.classpiserver.service.interaction.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/editor")
public class InteractionController {

    @Autowired
    private InteractionService interactionService;

    @PostMapping("/addCourseInteraction")
    public boolean addCourseInteraction(@RequestBody AddCourseInteractionRequest request) {
        return interactionService.addCourseInteraction(request);
    }

    @PostMapping("/getInteractionDetail")
    public InteractionDetailDTO getInteractionDetail(@RequestBody GetTestDetailRequest request) {
        return interactionService.getInteractionDetail(request.getActivity_id(), request.getAccount());
    }

    @PostMapping("/submitInteraction")
    public boolean submitInteraction(@RequestBody SubmitInteractionRequest request) {
        return interactionService.submitInteractionResponse(request);
    }

    @PostMapping("/askInteractionQuestion")
    public boolean askInteractionQuestion(@RequestBody AskInteractionQuestionRequest request) {
        return interactionService.askInteractionQuestion(request);
    }

    @PostMapping("/pickRandomStudent")
    public PickRandomStudentResult pickRandomStudent(@RequestBody PickRandomStudentRequest request) {
        return interactionService.pickRandomStudent(request);
    }

    @PostMapping("/closeInteraction")
    public boolean closeInteraction(@RequestBody CloseInteractionRequest request) {
        return interactionService.closeInteraction(request);
    }
}
