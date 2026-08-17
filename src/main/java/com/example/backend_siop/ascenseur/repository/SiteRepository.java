package com.example.backend_siop.ascenseur.repository;

import com.example.backend_siop.ascenseur.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Long> {
    List<Site> findByClientId(Long clientId);
    List<Site> findByVilleId(Long villeId);
    List<Site> findByParcId(Long parcId);
    List<Site> findByParcIdIn(Collection<Long> parcIds);
    long countByParcId(Long parcId);
}