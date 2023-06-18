package au.com.ontheboat.api;

import au.com.ontheboat.api.repository.BoatRepository;
import au.com.ontheboat.api.repository.CrewRepository;
import au.com.ontheboat.api.service.AdminService;
import au.com.ontheboat.api.service.CrewService;
import au.com.ontheboat.api.service.Progress;
import au.com.ontheboat.api.service.TopYachtLoader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class OntheBoatApiApplicationTests {

    @Autowired
    private TopYachtLoader topYachtLoader;
    @Autowired
    private AdminService adminService;

    @Autowired
    private BoatRepository boatRepository;
    @Autowired
    private CrewRepository crewRepository;
    @Autowired
    private CrewService crewService;

    @Test
    void testBoatCrewManyToMany() {
//        log.info("starting...");
//        log.info("boats: {}", boatRepository.findAll().size());
//        log.info("crew: {}", crewRepository.findAll().size());
//        List<Boat> noOwners = boatRepository.findAll().stream().filter(b -> b.getOwners().size() == 0).collect(Collectors.toList());
//        noOwners.stream().forEach(b -> log.info("{}", b));
//        log.info("no owners: {}", noOwners.size());
    }

    @Test
    public void testInitialisation() {
//        topYachtLoader.loadFromTopYacht();
//        topYachtLoader.updateArchivedStateFromTopYacht();
//        adminService.migrateContacts();
    }

    //    @Test
    public void popluateUUID() {
        crewService.populateUUID();
    }

    @SneakyThrows
//    @Test
    public void testProgress() {
        Progress progress = Progress.builder().total(1000).build();
        for (int i = 0; i < 1000; i++) {
            Thread.sleep(10);
            progress.incrementProgressBar();
        }
    }
}
