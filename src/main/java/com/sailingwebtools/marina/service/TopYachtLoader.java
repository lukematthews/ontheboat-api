package com.sailingwebtools.marina.service;

import com.sailingwebtools.marina.model.Boat;
import com.sailingwebtools.marina.model.BoatDetails;
import com.sailingwebtools.marina.model.Handicap;
import com.sailingwebtools.marina.model.HandicapType;
import com.sailingwebtools.marina.repository.BoatDetailsRepository;
import com.sailingwebtools.marina.repository.BoatRepository;
import com.sailingwebtools.marina.repository.HandicapRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TopYachtLoader {
    @Value("${marina.topyacht.url}")
    private String boatListUrl;
    @Value("${marina.topyacht.boat-detail}")
    private String boatDetailUrl;
    @Value("${marina.topyacht.boat-photo}")
    private String boatPhotoUrl;
    private Elements link;

    @Autowired
    private BoatRepository boatRepository;
    @Autowired
    private BoatDetailsRepository boatDetailsRepository;
    @Autowired
    private HandicapRepository handicapRepository;

    private AtomicInteger count = new AtomicInteger();


    private static void accept(Long consumer) {
    }

    public Flux<Long> asyncBoatLoad() {
        Flux<Long> boatFlux = Flux.fromIterable(boatRepository.findAll()).map(boat -> boat.getId());
        boatFlux.subscribe(TopYachtLoader::accept);

        return boatFlux;
    }

    @SneakyThrows
    public List<Boat> loadFromTopYacht() {
        boatRepository.deleteAll();
        count = new AtomicInteger();
        return processPage(this::loadBoat);
    }

    @SneakyThrows
    public List<Boat> updateArchivedStateFromTopYacht() {
        count = new AtomicInteger();
        return processPage(this::updateArchivedState);
    }

    @SneakyThrows
    private List<Boat> processPage(Function<Element, Boat> processor) {
        Document doc = Jsoup.connect(boatListUrl).maxBodySize(0).get();
        String title = doc.title();
        log.info(title);
        Elements boatTable = doc.select("#boats > tbody > tr");

        List<Boat> boatList = boatTable.stream()
                .filter(element -> element.childrenSize() > 4)
                .parallel()
                .map(element -> processor.apply(element))
                .collect(Collectors.toList());
        boatRepository.saveAll(boatList);
        return boatList;
    }

    private Boat updateArchivedState(Element element) {
        int currentItem = count.incrementAndGet();
        String id = getId(element);
        Boat boatToUpdate = boatRepository.findById(Long.valueOf(id)).orElse(null);
        if (boatToUpdate == null) {
            log.info("Failed to find boat with id: {}", id);
            return null;
        }
        boolean archived = element.child(4).text().equals("Y") ? true : false;
        boatToUpdate.setArchived(archived);
        boatRepository.saveAndFlush(boatToUpdate);
        if (currentItem % 10 == 0) {
            log.info("" + currentItem);
        }
        return boatToUpdate;
    }

    private Boat loadBoat(Element element) {
        String id = getId(element);
        int currentItem = count.incrementAndGet();
        boolean archived = element.child(4).text().equals("Y") ? true : false;
        Boat boat = Boat.builder()
                .id(Long.valueOf(id))
                .boatName(element.child(0).text())
                .sailNumber(element.child(1).text())
                .contact(element.child(2).text())
                .design(element.child(3).text())
                .archived(archived)
                .build();
        boatRepository.save(boat);
        loadBoatDetailsForBoat(boat);
        if (currentItem % 20 == 0) {
            log.info("" + currentItem);
        }
        return boat;
    }

    private static String getId(Element element) {
        Elements link = element.select("td > a");
        String href = link.attr("href");
        Pattern pattern = Pattern.compile(".*boid=(\\d+)");
        Matcher matcher = pattern.matcher(href);
        String id = matcher.find() ? matcher.group(1) : null;
        return id;
    }

    public void loadBoatDetails(List<Boat> boatsToLoad) {
        boatsToLoad.stream()
                .filter(boat -> Objects.nonNull(boat.getId()))
                .forEach(boat -> {
                    loadBoatDetailsForBoat(boat);
                });
    }

    public Boat getBoatDetails(Long boatId) {
        Boat boat = boatRepository.findById(boatId).orElse(null);
        if (boat == null) {
            return null;
        }
        loadBoatDetailsForBoat(boat);
        return boatRepository.findById(boatId).orElse(null);
    }

    @SneakyThrows
    private void loadBoatDetailsForBoat(Boat boat) {
        Document doc = Jsoup.connect(boatDetailUrl + boat.getId()).get();
        BoatDetails boatDetails = BoatDetails.builder()
                .boat(boat)
                .boatName(boat.getBoatName())
                .launchYear(textFrom(doc, "#14"))
                .design(textFrom(doc, "#16"))
                .lengthOverall(textFrom(doc, "#19"))
                .hullMaterial(textFrom(doc, "#21"))
                .rig(textFrom(doc, "#24"))
                .hullColour(textFrom(doc, "#26"))
                .bio(textFromDocumentWithNewlines(doc, "#39"))
                .build();

        boatDetailsRepository.save(boatDetails);

        boat.setHandicaps(loadHandicaps(boat, doc));
        boat.setBoatDetails(boatDetails);
        boatRepository.save(boat);
    }

    private List<Handicap> loadHandicaps(Boat boat, Document doc) {
        return doc.select("li").stream()
                .filter(element -> element.attr("id").matches("5[a-z?]"))
                .map(handicapElement -> {
                    String handicapTypeValue = handicapElement.child(0).text();
                    Optional<HandicapType> handicapType = Arrays.stream(HandicapType.values()).filter(type -> type.getLabel().equals(handicapTypeValue)).findFirst();

                    String ratingDetails = handicapElement.child(3).text();
                    // Format: a number, one or more whitespace, dd/MM/yyyy
                    Pattern pattern = Pattern.compile("(\\d+)\\s+(\\d\\d/\\d\\d/\\d\\d\\d\\d)");
                    Matcher matcher = pattern.matcher(ratingDetails);
                    String certificate = null;
                    LocalDate expiryDate = null;
                    if (matcher.find()) {
                        certificate = StringUtils.isNotBlank(matcher.group(1)) ? matcher.group(1) : null;
                        String date = matcher.group(2);
                        expiryDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    }
                    String rating = handicapElement.child(1).text();
                    rating = StringUtils.isNotBlank(rating) ? rating : null;
                    return Handicap.builder()
                            .handicapType(handicapType.orElse(null))
                            .boat(boat)
                            .rating(rating)
                            .certificate(certificate)
                            .expiryDate(expiryDate)
                            .build();
                })
                .peek(handicap -> handicapRepository.save(handicap))
                .collect(Collectors.toList());
    }

    public void saveAllImages() {
        AtomicInteger count = new AtomicInteger();
        boatRepository.findAll().stream().parallel().forEach(boat -> {
            int currentCount = count.incrementAndGet();
            saveImage(boat.getId());
            if (currentCount % 20 == 0) {
                log.info("" + currentCount);
            }
        });
    }

    @SneakyThrows
    public Boat saveImage(Long boatId) {
        URL imageUrl = new URL(boatPhotoUrl + boatId);
        BufferedImage image = ImageIO.read(imageUrl);
        File output = new File("./data/photos-png/" + boatId + ".png");
        FileOutputStream fos = new FileOutputStream(output);
        ImageIO.write(image, "png", fos);
        fos.flush();
        fos.close();
        return boatRepository.findById(boatId).orElse(null);

    }

    private String textFrom(Document doc, String selector) {
        // the text contains <br> tags. We need to pick those up as newlines.
        return doc.select(selector).text();
    }

    private String textFromDocumentWithNewlines(Document doc, String selector) {
        // the text contains <br> tags. We need to pick those up as newlines.
        Optional<Element> element = doc.select(selector).stream().findFirst();
        if (element.isPresent()) {
            return element.get().html().replaceAll("<br>", "");
        }
        return null;
    }


    public Flux<Boat> processBoatDetails() {
        return Flux.just(null);
    }

}
