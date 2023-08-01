package au.com.ontheboat.api.service;

import au.com.ontheboat.api.model.Boat;
import au.com.ontheboat.api.model.BoatDetails;
import au.com.ontheboat.api.model.BoatMedia;
import au.com.ontheboat.api.model.Handicap;
import au.com.ontheboat.api.model.HandicapType;
import au.com.ontheboat.api.repository.BoatDetailsRepository;
import au.com.ontheboat.api.repository.BoatMediaRepository;
import au.com.ontheboat.api.repository.BoatRepository;
import au.com.ontheboat.api.repository.HandicapRepository;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Value("${au.com.ontheboat.api.topyacht.url}")
    private String boatListUrl;
    @Value("${au.com.ontheboat.api.topyacht.boat-detail}")
    private String boatDetailUrl;
    @Value("${au.com.ontheboat.api.topyacht.boat-photo}")
    private String boatPhotoUrl;
    private Elements link;

    @Autowired
    private BoatRepository boatRepository;
    @Autowired
    private BoatDetailsRepository boatDetailsRepository;
    @Autowired
    private HandicapRepository handicapRepository;
    @Autowired
    private BoatMediaRepository boatMediaRepository;
    private AtomicInteger count = new AtomicInteger();


    private static void accept(Long consumer) {
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
        log.info("Processing {} boats", boatTable.size());
        List<Boat> boatList = boatTable.stream()
                .filter(element -> element.childrenSize() > 4)
                .parallel()
                .map(element -> processor.apply(element))
                .collect(Collectors.toList());
        log.info("finished loading {} boats", boatList.size());
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
        boatToUpdate = boatRepository.save(boatToUpdate);
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
                .externalId(id)
                .boatName(element.child(0).text())
                .sailNumber(element.child(1).text())
                .contact(element.child(2).text())
                .design(element.child(3).text())
                .archived(archived)
                .build();
        boat = boatRepository.saveAndFlush(boat);
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
        Document doc = Jsoup.connect(boatDetailUrl + boat.getExternalId()).get();
        BoatDetails boatDetails = BoatDetails.builder()
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
            saveImage(boat);
            if (currentCount % 20 == 0) {
                log.info("" + currentCount);
            }
        });
    }

    @SneakyThrows
    public Boat saveImage(Boat boat) {
        URL imageUrl = new URL(boatPhotoUrl + boat.getExternalId());
        BufferedImage image = ImageIO.read(imageUrl);
        File output = new File("./data/photos-png/" + boat.getExternalId() + ".png");
        FileOutputStream fos = new FileOutputStream(output);
        ImageIO.write(image, "png", fos);
        fos.flush();
        fos.close();
        BoatMedia boatMedia = BoatMedia.builder().boat(boat).fileId(boat.getExternalId() + ".png").uploadDate(LocalDateTime.now()).build();
        boatMediaRepository.save(boatMedia);
        return boat;
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
}
