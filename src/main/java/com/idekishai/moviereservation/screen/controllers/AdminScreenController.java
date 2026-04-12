package com.idekishai.moviereservation.screen.controllers;

import com.idekishai.moviereservation.screen.dtos.ScreenDTO;
import com.idekishai.moviereservation.screen.dtos.ScreenRequestDTO;
import com.idekishai.moviereservation.screen.services.ScreenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/screens")
@Validated
public class AdminScreenController {
    private final ScreenService screenService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ScreenDTO>> getAllScreens() {
        return ResponseEntity.ok(screenService.getAllScreens());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScreenDTO> saveScreen(@RequestBody @Valid ScreenRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(screenService.saveScreen(dto));
    }

    @PutMapping("/{screenId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScreenDTO> updateScreen(@PathVariable int screenId, @RequestBody @Valid ScreenRequestDTO dto){
        return ResponseEntity.ok(screenService.updateScreen(screenId, dto));
    }

    @DeleteMapping("/{screenId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteScreen(@PathVariable int screenId){
        screenService.deleteScreen(screenId);
        return ResponseEntity.ok("Screen deleted successfully");
    }
}
