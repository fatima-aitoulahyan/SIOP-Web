package com.example.backend_siop.maintenance.service;

import com.example.backend_siop.maintenance.dto.CalendrierEventDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendrierService {

    List<CalendrierEventDTO> getEvenementsCalendrier(
            LocalDateTime debut, LocalDateTime fin, Long technicienId);
}