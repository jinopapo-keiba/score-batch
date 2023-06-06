package org.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.BetResult;
import org.example.entity.HorseScore;
import org.example.entity.Race;
import org.example.repository.RaceRepository;
import org.example.repository.ScoreRepository;
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
        List<HorseScore> scores = scoreRepository.fetchScore(raceId);

        List<HorseScore> sortedScores = scores.stream().sorted(Comparator.comparingInt(HorseScore::getScore).reversed()).toList();
        HorseScore maxScores = scores.stream().sorted(Comparator.comparingInt(HorseScore::getMaxScore).reversed()).toList().get(0);
        HorseScore topHorseScores = sortedScores.get(0);
        List<HorseScore> prizeHorseScores = new ArrayList<>();
        prizeHorseScores.addAll(sortedScores.subList(1,sortedScores.size() <= 10 ? 4 : 6));
        if(maxScores.getHorseId() != topHorseScores.getHorseId()) {
            prizeHorseScores.add(prizeHorseScores.size()-1,maxScores);
        }

        Race race = raceRepository.fetchRace(raceId);
        AtomicBoolean jikuFirstFlag = new AtomicBoolean(false);
        AtomicBoolean jikuSecondFlag = new AtomicBoolean(false);
        AtomicBoolean jikuThirdFlag = new AtomicBoolean(false);
        AtomicBoolean firstFlag = new AtomicBoolean(false);
        AtomicBoolean secondFlag = new AtomicBoolean(false);
        AtomicBoolean thirdFlag = new AtomicBoolean(false);
        race.getRaceHorses().forEach((raceHorse -> {
            int ranking =raceHorse.getRaceResult().getRanking();
            if (ranking <= 3) {
                if(ranking == 1) {
                    jikuFirstFlag.set(raceHorse.getHorse().getId() == topHorseScores.getHorseId());
                    firstFlag.set(prizeHorseScores.stream().anyMatch(((prizeHorseScore) -> prizeHorseScore.getHorseId() == raceHorse.getHorse().getId())));
                    if(jikuFirstFlag.get() || firstFlag.get()) {
                        log.info("1着"+raceHorse.getHorse().getName()+jikuFirstFlag.get());
                    }
                }
                if (ranking == 2) {
                    jikuSecondFlag.set(raceHorse.getHorse().getId() == topHorseScores.getHorseId());
                    secondFlag.set(prizeHorseScores.stream().anyMatch(((prizeHorseScore) -> prizeHorseScore.getHorseId() == raceHorse.getHorse().getId())));
                    if(jikuSecondFlag.get() || secondFlag.get()) {
                        log.info("2着"+raceHorse.getHorse().getName()+jikuSecondFlag.get());
                    }
                }
                if (ranking == 3) {
                    jikuThirdFlag.set(raceHorse.getHorse().getId() == topHorseScores.getHorseId());
                    thirdFlag.set(prizeHorseScores.stream().anyMatch(((prizeHorseScore) -> prizeHorseScore.getHorseId() == raceHorse.getHorse().getId())));
                    if(jikuThirdFlag.get() || thirdFlag.get()) {
                        log.info("3着"+raceHorse.getHorse().getName()+jikuThirdFlag);
                    }
                }
            }
        }) );
       return BetResult.builder()
               .jikuFirst(jikuFirstFlag.get())
               .jikuSecond(jikuSecondFlag.get())
               .jikuThird(jikuThirdFlag.get())
               .first(firstFlag.get())
               .second(secondFlag.get())
               .third(thirdFlag.get())
               .build();
    }
}
