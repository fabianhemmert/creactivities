package de.ips.creactivities.chatbot.process;

import de.ips.creactivities.chatbot.cms.ICmsService;
import de.ips.creactivities.chatbot.constants.IProcessVariables;
import de.ips.creactivities.chatbot.dm.SolutionEntity;
import de.ips.creactivities.chatbot.repo.SolutionRepository;
import de.ips.creactivities.chatbot.telegram.CreactivitiesBot;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class PublishSolutionToCmsDelegate implements JavaDelegate {

    private SolutionRepository solutionRepository;

    private ICmsService cmsService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        long solutionId = (long) execution.getVariable(IProcessVariables.SOLUTION_ID);
        Optional<SolutionEntity> s = solutionRepository.findById(solutionId);

        if (s.isPresent() && !s.get().isBlocked()) {
            SolutionEntity solution = s.get();

            List<String> values = solution.getSolutionValues();

            if (values != null) {
                List<String> images = new ArrayList<>();
                String caption = null;
                for (String value : values) {
                    if (value.startsWith("__photo__")) {
                        images.add(value.replace("__photo__", ""));
                    } else {
                        caption = value;
                    }
                }

                List<byte[]> pictures = downloadImages(images);
                cmsService.uploadSolution(solution.getUser().getId(), solution.getChallenge().getId(), caption, pictures);
            }
        }
    }

    private List<byte[]> downloadImages(List<String> imageIds) {

        List<byte[]> list = new ArrayList<>();
        for (String id : imageIds) {
            try {
                byte[] downloaded = CreactivitiesBot.getInstance().downloadImage(id);
                list.add(downloaded);
            } catch (TelegramApiException | IOException e) {
                log.error("Failed to download image.", e);
            }
        }
        return list;
    }

    @Autowired
    public void setSolutionRepository(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    @Autowired
    public void setCmsService(ICmsService cmsService) {
        this.cmsService = cmsService;
    }
}
