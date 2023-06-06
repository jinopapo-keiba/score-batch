package org.example.repository;

import lombok.AllArgsConstructor;
import org.example.entity.Race;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class RaceRepository {
    private final RestTemplateBuilder restTemplateBuilder;

    public Race fetchRace(int raceId) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        Map<String,String> param = new HashMap<>();
        param.put("raceId",String.valueOf(raceId));
        param.put("beforeFlag","true");
        ResponseEntity<List<Race>> response = restTemplate.exchange("http://localhost:8080/v1/race?beforeFlag=false&raceId="+ raceId, HttpMethod.GET,null, new ParameterizedTypeReference<>() {},param);

        return response.getBody().get(0);
    }

    public List<Integer> fetchAllRace() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<List<Integer>> response = restTemplate.exchange("http://localhost:8080/v1/race/all", HttpMethod.GET,null, new ParameterizedTypeReference<>() {});

        return response.getBody();
    }
}
