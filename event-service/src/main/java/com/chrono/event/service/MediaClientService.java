package com.chrono.event.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.chrono.event.dto.MediaDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MediaClientService {

    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${media-service.url:http://media-service:8082}")
    private String mediaServiceUrl;
    
    public List<MediaDTO> getMediaByEventId(Long eventId) {
        String url = mediaServiceUrl + "/media/event/" + eventId;
        log.info("Tentative de récupération des médias depuis: {}", url);
        
        try {
            ResponseEntity<MediaDTO[]> response = restTemplate.getForEntity(url, MediaDTO[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Récupération réussie des médias pour l'événement {}: {} médias trouvés", 
                         eventId, response.getBody().length);
                return Arrays.asList(response.getBody());
            } else {
                log.warn("Aucun média trouvé pour l'événement avec l'ID: {}, code de statut: {}", 
                         eventId, response.getStatusCode());
                return Collections.emptyList();
            }
        } catch (ResourceAccessException e) {
            log.error("Impossible d'accéder au service de médias à l'URL {}: {}", url, e.getMessage());
            // Ne propage pas l'exception, mais retourne une liste vide
            return Collections.emptyList();
        } catch (RestClientException e) {
            log.error("Erreur lors de la communication avec le service de médias: {}", e.getMessage());
            // Ne propage pas l'exception, mais retourne une liste vide
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la récupération des médias pour l'événement {}: {}", 
                      eventId, e.getMessage(), e);
            // Ne propage pas l'exception, mais retourne une liste vide
            return Collections.emptyList();
        }
    }
    
    /**
     * Supprime tous les médias associés à un événement.
     * Cette méthode appelle le service de médias pour supprimer tous les médias liés à l'événement.
     */
    public void deleteMediaByEventId(Long eventId) {
        String url = mediaServiceUrl + "/media/event/" + eventId;
        log.info("Tentative de suppression des médias pour l'événement {} depuis: {}", eventId, url);
        
        try {
            restTemplate.delete(url);
            log.info("Suppression réussie des médias pour l'événement {}", eventId);
        } catch (ResourceAccessException e) {
            log.error("Impossible d'accéder au service de médias à l'URL {}: {}", url, e.getMessage());
            throw e;
        } catch (RestClientException e) {
            log.error("Erreur lors de la communication avec le service de médias: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la suppression des médias pour l'événement {}: {}", 
                      eventId, e.getMessage(), e);
            throw e;
        }
    }
} 