package com.coppel.services.impl;

import com.coppel.config.AppConfig;
import com.coppel.dto.asn.muebles.ASNMessageMuebles;
import com.coppel.dto.token.AuthResponseDTO;
import com.coppel.entities.AsnManhattanRequestAndRespose;
import com.coppel.entities.AsnToManhattan;
import com.coppel.mappers.JsonConverter;
import com.coppel.pubsub.PubSubSuscriber;
import com.coppel.repository.asn.AsnManhattanRequestAndResposeRepository;
import com.coppel.repository.asn.AsnToManhattanRepository;
import com.coppel.services.AsnToManhattanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class AsnToManhattanServiceImpl implements AsnToManhattanService {

    @Autowired
    private  final AsnToManhattanRepository asnToManhattanRepository;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private RestTemplate restTemplate;

    private final AsnManhattanRequestAndResposeRepository asnManhattanRequestAndResposeRepository;
    private final Logger logger = LoggerFactory.getLogger(AsnToManhattanServiceImpl.class);

    public AsnToManhattanServiceImpl(AsnToManhattanRepository asnToManhattanRepository,
                                     AsnManhattanRequestAndResposeRepository asnManhattanRequestAndResposeRepository) {
        this.asnToManhattanRepository = asnToManhattanRepository;
        this.asnManhattanRequestAndResposeRepository = asnManhattanRequestAndResposeRepository;
    }

    @Override
    public void insertAsnId(AsnToManhattan asnToManhattan) {
        asnToManhattanRepository.save(asnToManhattan);
    }

    @Override
    public void publishToManhattan(ASNMessageMuebles asnMessageMuebles) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());
        headers.set("SelectedLocation", "TEXCOCO");
        headers.set("SelectedOrganization", "TEXCOCO");
        String body = JsonConverter.convertObjectToJson(asnMessageMuebles);
        HttpEntity<String> requestEntity = new HttpEntity<>(body,headers);
        String uri = appConfig.getUrlServiceRestManhattan();

        try {
            String responseDTO = restTemplate.postForEntity(uri, requestEntity, String.class).getBody();
            AsnManhattanRequestAndRespose asnManhattanRequestAndRespose1 = new AsnManhattanRequestAndRespose();
            asnManhattanRequestAndRespose1.setRequest(body);
            asnManhattanRequestAndRespose1.setResponse(responseDTO);
            asnManhattanRequestAndResposeRepository.save(asnManhattanRequestAndRespose1);
        } catch (HttpClientErrorException e) {
            // Manejar error de cliente
            logger.error("Error de cliente: " + e.getMessage());

        } catch (HttpServerErrorException e) {
            // Manejar error de servidor
            logger.error("Error de servidor: " + e.getMessage());
        } catch (Exception e) {
            // Manejar otras excepciones
            logger.error("Error inesperado: " + e.getMessage());
        }
    }


    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + appConfig.getTokenAuthServiceManhattan());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String uri = appConfig.getUrlAuthServiceManhattan();
        AuthResponseDTO authResponseDTO = restTemplate.postForEntity(uri, requestEntity, AuthResponseDTO.class).getBody();

        if (Objects.isNull(authResponseDTO) || Objects.isNull(authResponseDTO.getAccessToken())) {
            System.out.println("AccessToken is null");
            return null;
        }

        return authResponseDTO.getAccessToken();
    }
}
