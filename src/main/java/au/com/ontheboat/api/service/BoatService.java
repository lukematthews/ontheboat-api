package au.com.ontheboat.api.service;

import au.com.ontheboat.api.model.Boat;
import au.com.ontheboat.api.model.Crew;
import au.com.ontheboat.api.model.dto.BoatDto;
import au.com.ontheboat.api.model.dto.CrewProfileResponse;
import au.com.ontheboat.api.repository.BoatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BoatService {
    @Autowired
    private BoatRepository boatRepository;

    public Page<Boat> getAllBoats(Pageable page) {
        log.info(page.toString());
        return boatRepository.findAll(Example.of(Boat.builder().archived(false).build()), page);
    }

    public List<Boat> findBoats(String search) {
        return boatRepository.findBoatByNameSailNumberContact(search == null ? "" : search.toUpperCase());
    }

    public Page<BoatDto> findBoats(String search, Pageable pageable) {
        Page<Boat> boats = boatRepository.findBySearchTerm(search, pageable);
        Page<BoatDto> boatDtoPage = boats.map(boat -> {
            BoatDto boatDto = new BoatDto();
            BeanUtils.copyProperties(boat, boatDto);
            boatDto.setOwners(boat.getOwners().stream()
                    .map(owner -> {
                        CrewProfileResponse crewDto = new CrewProfileResponse();
                        BeanUtils.copyProperties(owner, crewDto);
                        return crewDto;
                    }).collect(Collectors.toSet()));
            boatDto.setContact(boat.getOwners().stream()
                    .map(this::renderName)
                    .collect(Collectors.joining(", ")));
            return boatDto;
        });
        return boatDtoPage;
    }

    private String renderName(Crew crew) {
        StringBuffer name = new StringBuffer();
        if (!Objects.isNull(crew.getFirstName())) {
            name.append(crew.getFirstName());
        }
        if (!Objects.isNull(crew.getLastName())) {
            name.append(" " + crew.getLastName());
        }
        return name.toString();
    }

    public Boat getBoatDetails(Long boatId) {
        return boatRepository.findByExternalId(boatId.toString());
    }

}
