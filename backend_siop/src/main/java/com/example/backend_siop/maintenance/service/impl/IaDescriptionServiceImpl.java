package com.example.backend_siop.maintenance.service.impl;

import com.example.backend_siop.common.exception.BusinessRuleException;
import com.example.backend_siop.maintenance.entity.DemandeMaintenance;
import com.example.backend_siop.maintenance.service.IaDescriptionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class IaDescriptionServiceImpl implements IaDescriptionService {

    @Value("${groq.api-key}")
    private String apiKey;

    @Value("${groq.model}")
    private String model;

    @Value("${groq.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String genererDescription(DemandeMaintenance demande) {
        String contexte = construireContexte(demande);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", "Tu es un assistant pour une entreprise de maintenance "
                                        + "d'ascenseurs. À partir des informations fournies par un client, "
                                        + "rédige une description claire, professionnelle et structurée du "
                                        + "problème, destinée à un technicien qui va intervenir. Reste concis "
                                        + "(3-5 phrases), factuel, en français."
                        ),
                        Map.of("role", "user", "content", contexte)
                ),
                "temperature", 0.4
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        try {
            var response = restTemplate.postForEntity(
                    baseUrl, new HttpEntity<>(body, headers), String.class);

            JsonNode racine = objectMapper.readTree(response.getBody());

            // Vérification d'erreur de l'API (ajout deploy-dokploy)
            if (racine.has("error")) {
                String errorMsg = racine.path("error").path("message").asText("Erreur inconnue de l'API IA");
                throw new BusinessRuleException("Erreur API Groq : " + errorMsg);
            }

            return racine.path("choices").get(0).path("message").path("content").asText();

        } catch (BusinessRuleException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessRuleException("Erreur lors de la génération IA : " + e.getMessage());
        }
    }

    private String construireContexte(DemandeMaintenance demande) {
        String nomAscenseur = (demande.getAscenseur() != null) ? demande.getAscenseur().getNom() : "Non défini (Demande d'évaluation)";

        String adresse = "Non définie";
        if (demande.getAscenseur() != null && demande.getAscenseur().getSiteEntity() != null) {
            adresse = demande.getAscenseur().getSiteEntity().getAdresse();
        } else if (demande.getAdresseSaisie() != null) {
            adresse = demande.getAdresseSaisie() + (demande.getVilleSaisie() != null ? " (" + demande.getVilleSaisie() + ")" : "");
        }

        return """
                Type de problème : %s
                Priorité signalée par le client : %s
                Ascenseur : %s
                Site / Adresse : %s
                Description originale du client : "%s"
                """.formatted(
                demande.getTypeDemande(),
                demande.getPriorite(),
                nomAscenseur,
                adresse,
                demande.getDescription()
        );
    }
}