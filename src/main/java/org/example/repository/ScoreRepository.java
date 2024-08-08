package org.example.repository;

import lombok.AllArgsConstructor;
import org.example.entity.HorseScore;
import org.example.repository.dto.HorseScoreResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@AllArgsConstructor
public class ScoreRepository {
   private final RestTemplateBuilder restTemplateBuilder;

   public HorseScoreResponse fetchScore(int raceId) {
       RestTemplate restTemplate = restTemplateBuilder.build();
       Map<String,Object> param = new HashMap<>();
       param.put("raceId",raceId);
       ResponseEntity<HorseScoreResponse> response = restTemplate.exchange("http://localhost:8888/v1/score?raceId="+ raceId, HttpMethod.GET,null, new ParameterizedTypeReference<>() {},param);
       return Objects.requireNonNull(response.getBody());
   }
}
