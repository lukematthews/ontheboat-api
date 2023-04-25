package com.sailingwebtools.marina.controller;

import com.sailingwebtools.marina.model.Boat;
import com.sailingwebtools.marina.model.dto.BoatSearchResult;
import com.sailingwebtools.marina.service.TopYachtLoader;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController("/marina")
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
    public Page<Boat> getAllSavedBoats(@PageableDefault(size = Integer.MAX_VALUE)
                                       @SortDefault.SortDefaults({@SortDefault(sort = "boatName", direction = Sort.Direction.ASC)}) Pageable page) {
        return topYachtLoader.getAllBoats(page);
    }

    @GetMapping(
            value = "/boat-photo",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public ResponseEntity<byte[]> getImageWithMediaType(@RequestParam Long id) throws IOException {
        InputStream in = getClass()
                .getResourceAsStream(photoDataPath + id + ".png");
        if (in == null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(IOUtils.toByteArray(in));
    }

    @GetMapping(value = "/search")
    public List<BoatSearchResult> searchForBoats(@RequestParam String search) {
        return topYachtLoader.findBoats(search);
    }

    @GetMapping("/boat-details")
    public ResponseEntity<Boat> getBoatDetails(@RequestParam Long boatId) {
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
