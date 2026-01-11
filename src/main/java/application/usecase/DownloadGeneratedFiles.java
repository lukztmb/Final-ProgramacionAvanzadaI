package application.usecase;


import org.springframework.scheduling.annotation.Scheduled;

public class DownloadGeneratedFiles {
    @Scheduled(fixedDelay = 10000)
    public void execute() {

    }
}
