package com.sailingwebtools.marina;

import com.sailingwebtools.marina.repository.BoatRepository;
import com.sailingwebtools.marina.repository.CrewRepository;
import com.sailingwebtools.marina.service.CrewService;
import com.sailingwebtools.marina.service.Progress;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class MarinaApplicationTests {

    @Autowired
    private BoatRepository boatRepository;
    @Autowired
    private CrewRepository crewRepository;
    @Autowired
    private CrewService crewService;

    //    @Test
    void testBoatCrewManyToMany() {
//        log.info("starting...");
//        log.info("boats: {}", boatRepository.findAll().size());
//        log.info("crew: {}", crewRepository.findAll().size());
//        List<Boat> noOwners = boatRepository.findAll().stream().filter(b -> b.getOwners().size() == 0).collect(Collectors.toList());
//        noOwners.stream().forEach(b -> log.info("{}", b));
//        log.info("no owners: {}", noOwners.size());
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
