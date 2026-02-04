package com.barmalat.medicalclinic.controller;

import com.barmalat.medicalclinic.mapper.VisitMapper;
import com.barmalat.medicalclinic.model.commands.CreateVisitCommand;
import com.barmalat.medicalclinic.model.dtos.VisitDto;
import com.barmalat.medicalclinic.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService visitService;
    private final VisitMapper visitMapper;

    @GetMapping
    public Page<VisitDto> findAll(Pageable pageable) {
        return visitService.findAll(pageable)
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

    @GetMapping("/patient/{patientId}")
    public Page<VisitDto> findVisitsByPatientId(@PathVariable Long patientId, Pageable pageable) {
        return visitService.findVisitsByPatientId(patientId, pageable)
                .map(visitMapper::toDto);
    }
}