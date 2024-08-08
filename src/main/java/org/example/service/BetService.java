package org.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.*;
import org.example.repository.RaceRepository;
import org.example.repository.ScoreRepository;
import org.example.repository.dto.HorseScoreResponse;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class BetService {
    private final ScoreRepository scoreRepository;
    private final RaceRepository raceRepository;

    public Map<String,BetResult> calcPaymentPrice(int raceId) {
        HorseScoreResponse scoreResponse = scoreRepository.fetchScore(raceId);
        List<HorseScore> scores = scoreResponse.getScores();
        Confident confident = scoreResponse.getConfident();
        Race race = raceRepository.fetchRace(raceId);
        List<RaceHorse> raceHorses = race.getRaceHorses();

        Map<String,BetResult> results = new HashMap<>();
        results.put("最大スコア",judgeTanshou(raceHorses,betMaxScore(raceHorses,scores)));
        results.put("最大スコア(買い)", confident.isBuyFlag() ? judgeTanshou(raceHorses,betMaxScore(raceHorses,scores)) : null);
        results.put("最大スコア(自信)", confident.isConfidentFlag() ? judgeTanshou(raceHorses,betMaxScore(raceHorses,scores)) : null);
        results.put("最大スコア(カオス)", confident.isChaosFlag() ? judgeTanshou(raceHorses,betMaxScore(raceHorses,scores)) : null);
        results.put("Best3スコア",judgeTanshou(raceHorses,betBest3Score(raceHorses,scores)));
        results.put("Best3スコア(買い)", confident.isBuyFlag() ? judgeTanshou(raceHorses,betBest3Score(raceHorses,scores)) : null);
        results.put("Best3スコア(自信)", confident.isConfidentFlag() ? judgeTanshou(raceHorses,betBest3Score(raceHorses,scores)) : null);
        results.put("Best3スコア(カオス)", confident.isChaosFlag() ? judgeTanshou(raceHorses,betBest3Score(raceHorses,scores)) : null);
        results.put("最大期待値",judgeTanshou(raceHorses,betMaxExpect(raceHorses,scores)));
        results.put("最大期待値(買い)", confident.isBuyFlag() ? judgeTanshou(raceHorses,betMaxExpect(raceHorses,scores)) : null);
        results.put("最大期待値(自信)", confident.isConfidentFlag() ? judgeTanshou(raceHorses,betMaxExpect(raceHorses,scores)) : null);
        results.put("最大期待値(カオス)", confident.isChaosFlag() ? judgeTanshou(raceHorses,betMaxExpect(raceHorses,scores)) : null);
        results.put("期待値1以上",judgeTanshou(raceHorses,betExpectOverOne(raceHorses,scores)));
        results.put("期待値1以上(買い)", confident.isBuyFlag() ? judgeTanshou(raceHorses,betExpectOverOne(raceHorses,scores)) : null);
        results.put("期待値1以上(自信)", confident.isConfidentFlag() ? judgeTanshou(raceHorses,betExpectOverOne(raceHorses,scores)) : null);
        results.put("期待値1以上(カオス)", confident.isChaosFlag() ? judgeTanshou(raceHorses,betExpectOverOne(raceHorses,scores)) : null);

        return results;
    }

    private BetResult judgeTanshou(List<RaceHorse> raceHorses, List<Integer> betHorseIds) {
        if (betHorseIds.isEmpty()) {
            return null;
        }

        RaceHorse topHorse = raceHorses.stream()
                .filter(raceHorse -> raceHorse.getRaceResult() != null)
                .filter(raceHorse -> raceHorse.getRaceResult().getRanking() == 1)
                .toList().get(0);

        if(betHorseIds.contains(topHorse.getHorse().getId())) {
            return BetResult
                    .builder()
                    .hit(true)
                    .payout(topHorse.getRaceResult().getOdds() / betHorseIds.size())
                    .build();
        } else {
            return BetResult.builder()
                    .hit(false)
                    .payout(0)
                    .build();
        }
    }

    /**
     * 一番単勝の確率が高い馬を買う
     *
     * @param raceHorses
     * @param horseScores
     * @return
     */
    private List<Integer> betMaxScore(List<RaceHorse> raceHorses, List<HorseScore> horseScores) {
        return Arrays.asList(
                horseScores.stream().sorted(Comparator.comparingDouble(HorseScore::getScore).reversed()).toList().get(0).getHorseId()
        );
    }

    /**
     * 単勝の確率が高い馬best3を買う
     *
     * @param raceHorses
     * @param horseScores
     * @return
     */
    private List<Integer> betBest3Score(List<RaceHorse> raceHorses, List<HorseScore> horseScores) {
        return horseScores.stream()
                .sorted(Comparator.comparingDouble(HorseScore::getScore).reversed()).toList()
                .subList(0,3).stream()
                .map(HorseScore::getHorseId).toList();
    }

    /**
     * 期待値が一番高く1以上の馬を買う
     *
     * @param raceHorses
     * @param horseScores
     * @return
     */
    private List<Integer> betMaxExpect(List<RaceHorse> raceHorses, List<HorseScore> horseScores) {
        Map<Integer,Double> horseExpects = getHorseExpects(raceHorses,horseScores);

        Integer buyHorse = null;
        double maxExpect = 0;
        for (Map.Entry<Integer,Double> horseExpect : horseExpects.entrySet()) {
            if(horseExpect.getValue() > maxExpect) {
                buyHorse = horseExpect.getKey();
                maxExpect = horseExpect.getValue();
            }
        }
        if ( maxExpect >= 1 ) {
            return Arrays.asList(buyHorse);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 期待値が1以上の馬をすべて買う
     *
     * @param raceHorses
     * @param horseScores
     * @return
     */
    private List<Integer> betExpectOverOne(List<RaceHorse> raceHorses, List<HorseScore> horseScores) {
        Map<Integer,Double> horseExpects = getHorseExpects(raceHorses,horseScores);

        List<Integer> buyHorseIds = new ArrayList<>();
        for (Map.Entry<Integer,Double> horseExpect : horseExpects.entrySet()) {
            if(horseExpect.getValue() >= 1) {
                buyHorseIds.add(horseExpect.getKey());
            }
        }
        return buyHorseIds;
    }


    private Map<Integer,Double> getHorseExpects(List<RaceHorse> raceHorses, List<HorseScore> horseScores) {
        Map<Integer,Double> horseExpects = new HashMap<>();
        raceHorses.forEach((raceHorse -> {
            HorseScore horseScore = horseScores.stream().filter(socre -> socre.getHorseId() == raceHorse.getHorse().getId()).findFirst().orElse(null);
            if (raceHorse.getRaceResult() == null || horseScore == null){
                return;
            }
            double expect = horseScore.getScore()*raceHorse.getRaceResult().getOdds()/100;
            horseExpects.put(raceHorse.getHorse().getId(), expect);
        }));

        return horseExpects;
    }
}
