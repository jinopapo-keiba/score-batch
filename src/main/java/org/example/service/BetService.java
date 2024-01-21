package org.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.BetResult;
import org.example.entity.HorseScore;
import org.example.entity.Race;
import org.example.repository.RaceRepository;
import org.example.repository.ScoreRepository;
import org.example.repository.dto.HorseScoreResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@AllArgsConstructor
@Slf4j
public class BetService {
    private final ScoreRepository scoreRepository;
    private final RaceRepository raceRepository;

    public BetResult calcPaymentPrice(int raceId) {
        HorseScoreResponse scoreResponse = scoreRepository.fetchScore(raceId);
        List<HorseScore> scores = scoreResponse.getScores();
        Race race = raceRepository.fetchRace(raceId);

        List<HorseScore> sortedScores = scores.stream().sorted(Comparator.comparingDouble(HorseScore::getScore).reversed()).toList();
        HorseScore topHorseScores = sortedScores.get(0);
        List<HorseScore> prizeHorseScores = new ArrayList<>(sortedScores.subList(1, Math.min(race.getRaceHorses().size(),6)));


        AtomicBoolean popularFirstFlag = new AtomicBoolean(false);
        AtomicBoolean popularSecondFlag = new AtomicBoolean(false);
        AtomicBoolean popularThirdFlag = new AtomicBoolean(false);
        AtomicBoolean jikuFirstFlag = new AtomicBoolean(false);
        AtomicBoolean jikuSecondFlag = new AtomicBoolean(false);
        AtomicBoolean jikuThirdFlag = new AtomicBoolean(false);
        AtomicBoolean firstFlag = new AtomicBoolean(false);
        AtomicBoolean secondFlag = new AtomicBoolean(false);
        AtomicBoolean thirdFlag = new AtomicBoolean(false);
        race.getRaceHorses().forEach((raceHorse -> {
            if (raceHorse.getRaceResult() == null) {
                return;
            }
            int ranking = raceHorse.getRaceResult().getRanking();
            if (ranking <= 3) {
                if(ranking == 1) {
                    jikuFirstFlag.set(jikuFirstFlag.get() || raceHorse.getHorse().getId() == topHorseScores.getHorseId());
                    firstFlag.set(firstFlag.get() || prizeHorseScores.stream().anyMatch(((prizeHorseScore) -> prizeHorseScore.getHorseId() == raceHorse.getHorse().getId())));
                    if (raceHorse.getRaceResult().getPopular() != null && raceHorse.getRaceResult().getPopular() == 1) {
                        popularFirstFlag.set(true);
                    }
                }
                if (ranking == 2) {
                    jikuSecondFlag.set(jikuSecondFlag.get() || raceHorse.getHorse().getId() == topHorseScores.getHorseId());
                    secondFlag.set(secondFlag.get() ||  prizeHorseScores.stream().anyMatch(((prizeHorseScore) -> prizeHorseScore.getHorseId() == raceHorse.getHorse().getId())));
                    if (raceHorse.getRaceResult().getPopular() != null && raceHorse.getRaceResult().getPopular() == 1) {
                        popularSecondFlag.set(true);
                    }
                }
                if (ranking == 3) {
                    jikuThirdFlag.set(jikuThirdFlag.get() || raceHorse.getHorse().getId() == topHorseScores.getHorseId());
                    thirdFlag.set(thirdFlag.get() || prizeHorseScores.stream().anyMatch(((prizeHorseScore) -> prizeHorseScore.getHorseId() == raceHorse.getHorse().getId())));
                    if (raceHorse.getRaceResult().getPopular() != null && raceHorse.getRaceResult().getPopular() == 1) {
                        popularThirdFlag.set(true);
                    }
                }
            }

        }) );
       return BetResult.builder()
               .jikuFirst(jikuFirstFlag.get())
               .jikuSecond(jikuSecondFlag.get())
               .jikuThird(jikuThirdFlag.get())
               .popularFirst(popularFirstFlag.get())
               .popularSecond(popularSecondFlag.get())
               .popularThird(popularThirdFlag.get())
               .first(firstFlag.get())
               .second(secondFlag.get())
               .third(thirdFlag.get())
               .confident(scoreResponse.getConfident().isConfidentFlag())
               .build();
    }
}
