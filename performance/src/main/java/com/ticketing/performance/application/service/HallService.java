package com.ticketing.performance.application.service;

import com.ticketing.performance.application.dto.CreateHallResponseDto;
import com.ticketing.performance.application.dto.HallListResponseDto;
import com.ticketing.performance.domain.model.Hall;
import com.ticketing.performance.domain.model.HallSeat;
import com.ticketing.performance.domain.repository.HallRepository;
import com.ticketing.performance.presentation.dto.CreateHallRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public Page<HallListResponseDto> getHalls() {
        return null;
    }
}
