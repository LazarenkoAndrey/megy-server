package ru.megy.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.megy.service.CheckFileSystemService;
import ru.megy.service.MessageService;
import ru.megy.service.entity.ResultCheckFileSystem;

@Service
public class PlaningService {
    private static final Logger logger = LoggerFactory.getLogger(PlaningService.class);

    @Autowired
    private CheckFileSystemService checkFileSystemService;

    @Autowired
    private MessageService messageService;

    @Scheduled(initialDelay = 10000, fixedRate= 60*60*1000)
    public void doCheckScheduling() {
        try {
            ResultCheckFileSystem result = checkFileSystemService.checkFreeSpace();

            if(!result.isResult()) {
                logger.info("Result of check files system: {}\n{}", result.isResult(), result.getMessage());
                messageService.addMessage("Result of check files system:\r\n" + result.getMessage());
            }
        } catch (Exception e) {
            logger.error("doCheckScheduling", e);
        }
    }

    @Scheduled(initialDelay = 60000, fixedRate=30*60*1000)
    public void doMessageSending() {
        messageService.doSending();
    }
}
