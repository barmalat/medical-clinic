package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.mapper.VisitMapper;
import com.barmalat.medicalclinic.model.commands.CreateVisitCommand;
import com.barmalat.medicalclinic.model.dtos.VisitDto;
import com.barmalat.medicalclinic.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;
    private final VisitMapper visitMapper;

    @GetMapping
    public Page<VisitDto> find(@RequestParam(required = false) Long patientId, Pageable pageable) {
        return visitService.find(patientId, pageable)
                .map(visitMapper::toDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitDto addVisit(@RequestBody CreateVisitCommand createVisitCommand) {
        return visitMapper.toDto(visitService.addVisit(createVisitCommand));
    }

    @PatchMapping("/{visitId}/patient/{patientId}")
    public VisitDto addPatientToVisit(@PathVariable Long visitId, @PathVariable Long patientId) {
        return visitMapper.toDto(visitService.addPatientToVisit(visitId, patientId));
    }
}