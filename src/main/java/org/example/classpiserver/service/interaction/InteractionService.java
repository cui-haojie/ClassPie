package org.example.classpiserver.service.interaction;

import org.example.classpiserver.dto.interaction.AddCourseInteractionRequest;
import org.example.classpiserver.dto.interaction.AskInteractionQuestionRequest;
import org.example.classpiserver.dto.interaction.CloseInteractionRequest;
import org.example.classpiserver.dto.interaction.InteractionDetailDTO;
import org.example.classpiserver.dto.interaction.PickRandomStudentRequest;
import org.example.classpiserver.dto.interaction.PickRandomStudentResult;
import org.example.classpiserver.dto.interaction.SubmitInteractionRequest;

public interface InteractionService {
    boolean addCourseInteraction(AddCourseInteractionRequest request);
    InteractionDetailDTO getInteractionDetail(Long activityId, String account);
    boolean submitInteractionResponse(SubmitInteractionRequest request);
    boolean askInteractionQuestion(AskInteractionQuestionRequest request);
    PickRandomStudentResult pickRandomStudent(PickRandomStudentRequest request);
    boolean closeInteraction(CloseInteractionRequest request);
}
