package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.CreateHallResponseDto;
import com.ticketing.performance.application.dto.HallInfoResponseDto;
import com.ticketing.performance.application.dto.HallListResponseDto;
import com.ticketing.performance.application.dto.UpdateHallResponseDto;
import com.ticketing.performance.domain.model.Hall;
import com.ticketing.performance.domain.model.HallSeat;
import com.ticketing.performance.domain.repository.HallRepository;
import com.ticketing.performance.presentation.dto.CreateHallRequestDto;
import com.ticketing.performance.presentation.dto.UpdateHallRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HallService {

    private final HallRepository hallRepository;


    @Transactional
    public CreateHallResponseDto createHall(CreateHallRequestDto createHallRequestDto) {
        Hall hall = Hall.create(createHallRequestDto);

        List<HallSeat> hallSeats = createHallRequestDto.getSections()
                .stream()
                .map(HallSeat::create)
                .toList();

        hall.addSeats(hallSeats);

        hallRepository.save(hall);
        return CreateHallResponseDto.of(hall);
    }


    public Page<HallListResponseDto> getHalls(Pageable pageable) {
        return hallRepository.findAll(pageable).map(HallListResponseDto::of);
    }


    public HallInfoResponseDto getHall(UUID hallId) {

        return hallRepository.findById(hallId).map(HallInfoResponseDto::of)
                .orElseThrow(() -> new RuntimeException("error"));
    }

    @Transactional
    public UpdateHallResponseDto updateHall(UUID hallId, UpdateHallRequestDto updateHallRequestDto) {
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new RuntimeException("error"));
        hall.update(updateHallRequestDto);
        return UpdateHallResponseDto.of(hall);
    }

    @Transactional
    public void deleteHall(UUID hallId) {
        Hall hall = hallRepository.findById(hallId)
                .orElseThrow(() -> new RuntimeException("error"));
        hall.delete(1L);
    }
}
