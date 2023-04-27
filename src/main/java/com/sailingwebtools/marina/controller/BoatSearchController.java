package com.sailingwebtools.marina.controller;

import com.sailingwebtools.marina.model.Boat;
import com.sailingwebtools.marina.service.TopYachtLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController("/marina")
@CrossOrigin
@Slf4j
public class BoatSearchController {
    @Autowired
    private TopYachtLoader topYachtLoader;

    @Value("${marina.photos.path}")
    private String photoDataPath;

    @GetMapping("/boats-from-partner")
    public ResponseEntity<List<Boat>> getAllBoats() {
        return ResponseEntity.ok(topYachtLoader.loadFromTopYacht());
    }

    @GetMapping("/update-archived-state-from-partner")
    public ResponseEntity<List<Boat>> updateArchivedState() {
        return ResponseEntity.ok(topYachtLoader.updateArchivedStateFromTopYacht());
    }

    @GetMapping("/boats")
    @CrossOrigin(origins = "http://marina-ui")
    public Page<Boat> getAllSavedBoats(@PageableDefault(size = Integer.MAX_VALUE)
                                       @SortDefault.SortDefaults({@SortDefault(sort = "boatName", direction = Sort.Direction.ASC)}) Pageable page) {
        log.info("/api/boats {}", page);
        return topYachtLoader.getAllBoats(page);
    }

    @GetMapping(
            value = "/boat-photo",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    @CrossOrigin(origins = "http://marina-ui")
    public ResponseEntity<byte[]> getImageWithMediaType(@RequestParam Long id) throws IOException {
        File photoFile = new File(photoDataPath + id + ".png");
        log.info("/api/boat-photo {}", photoFile.getAbsolutePath());
        InputStream in = new FileInputStream(photoFile);
        if (in == null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(IOUtils.toByteArray(in));
    }

    @GetMapping(value = "/search")
    @CrossOrigin(origins = "http://marina-ui")
    public List<Boat> searchForBoats(@RequestParam String search) {
        log.info("/api/search?search={}", search);
        return topYachtLoader.findBoats(search);
    }

    @GetMapping("/boat-details")
    @CrossOrigin(origins = "http://marina-ui")
    public ResponseEntity<Boat> getBoatDetails(@RequestParam Long boatId) {
        log.info("/api/boat-details?boatId={}", boatId);
        return ResponseEntity.ok(topYachtLoader.getBoatDetails(boatId));
    }

    @GetMapping("/save-image")
    public ResponseEntity fetchBoatImage(@RequestParam Long boatId) {
        topYachtLoader.saveImage(boatId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/save-all-images")
    public ResponseEntity fetchBoatImages() {
        topYachtLoader.saveAllImages();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/process-boat-details")
    public Flux<Boat> processBoatDetails() {
        return topYachtLoader.processBoatDetails();
    }

}
