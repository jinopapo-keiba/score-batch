package org.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.*;
import org.example.repository.RaceRepository;
import org.example.repository.ScoreRepository;
import org.example.repository.dto.HorseScoreResponse;
import org.example.valueobject.OldLimit;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        results = mergeMap(results,betRace("単勝：最大スコア",judgeTanshou(raceHorses,betMaxScore(raceHorses,scores)),confident,race));
        results = mergeMap(results,betRace("単勝：Best3スコア",judgeTanshou(raceHorses,betBest3Score(raceHorses,scores)),confident,race));
        results = mergeMap(results,betRace("単勝：期待値1以上",judgeTanshou(raceHorses,betExpectOver(raceHorses,scores,1)),confident,race));
        results = mergeMap(results,betRace("単勝：期待値1.5以上",judgeTanshou(raceHorses,betExpectOver(raceHorses,scores,1.5F)),confident,race));
        results = mergeMap(results,betRace("単勝：期待値2以上",judgeTanshou(raceHorses,betExpectOver(raceHorses,scores,2)),confident,race));
        results = mergeMap(results,betRace("単勝：最大期待値",judgeTanshou(raceHorses,betMaxExpect(raceHorses,scores,1)),confident,race));
        results = mergeMap(results,betRace("単勝：最大期待値(1.5)",judgeTanshou(raceHorses,betMaxExpect(raceHorses,scores,1.5F)),confident,race));
        results = mergeMap(results,betRace("単勝：最大期待値(2)",judgeTanshou(raceHorses,betMaxExpect(raceHorses,scores,2)),confident,race));
        results = mergeMap(results,betRace("複勝：最大スコア",judgeHukushou(raceHorses,betMaxScore(raceHorses,scores),race.getPayouts()),confident,race));
        results = mergeMap(results,betRace("複勝：最大期待値",judgeHukushou(raceHorses,betMaxExpect(raceHorses,scores,1),race.getPayouts()),confident,race));
        results = mergeMap(results,betRace("複勝：最大期待値(1.5)",judgeHukushou(raceHorses,betMaxExpect(raceHorses,scores,1.5F),race.getPayouts()),confident,race));
        results = mergeMap(results,betRace("複勝：最大期待値(2)",judgeHukushou(raceHorses,betMaxExpect(raceHorses,scores,2),race.getPayouts()),confident,race));
        results = mergeMap(results,betRace("ワイド：軸：最大スコア 紐：最大期待値",judgeWide(raceHorses,betMaxScore(raceHorses,scores),betMaxExpect(raceHorses,scores,1),race.getPayouts()),confident,race));
        results = mergeMap(results,betRace("ワイド：軸：最大スコア 紐：最大期待値(1.5)",judgeWide(raceHorses,betMaxScore(raceHorses,scores),betMaxExpect(raceHorses,scores,1.5F),race.getPayouts()),confident,race));
        results = mergeMap(results,betRace("ワイド：軸：最大スコア 紐：最大期待値(2)",judgeWide(raceHorses,betMaxScore(raceHorses,scores),betMaxExpect(raceHorses,scores,2),race.getPayouts()),confident,race));
        results = mergeMap(results,betRace("ワイド：軸：最大スコア 紐：期待値1以上",judgeWide(raceHorses,betMaxScore(raceHorses,scores),betExpectOver(raceHorses,scores,1),race.getPayouts()),confident,race));
        results = mergeMap(results,betRace("ワイド：軸：最大スコア 紐：期待値1.5以上",judgeWide(raceHorses,betMaxScore(raceHorses,scores),betExpectOver(raceHorses,scores,1.5F),race.getPayouts()),confident,race));
        results = mergeMap(results,betRace("ワイド：軸：最大スコア 紐：期待値2以上)",judgeWide(raceHorses,betMaxScore(raceHorses,scores),betExpectOver(raceHorses,scores,2),race.getPayouts()),confident,race));
        return results;
    }

    private BetResult judgeTanshou(List<RaceHorse> raceHorses, List<Integer> betHorseIds) {
        if (betHorseIds.isEmpty()) {
            return null;
        }

        List<RaceHorse> topHorses = raceHorses.stream()
                .filter(raceHorse -> raceHorse.getRaceResult() != null)
                .filter(raceHorse -> raceHorse.getRaceResult().getRanking() == 1)
                .toList();

        if(topHorses.isEmpty()) {
            return null;
        }

        RaceHorse topHorse = topHorses.get(0);

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

    private BetResult judgeHukushou(List<RaceHorse> raceHorses, List<Integer> betHorseIds,List<Payout> payouts) {
        if (betHorseIds.isEmpty()) {
            return null;
        }

        List<Integer> betFrameNumber = betHorseIds.stream().map(
                betHorseId -> raceHorses.stream().filter(raceHorse -> Objects.equals(raceHorse.getHorse().getId(), betHorseId)).findFirst().get().getFrameNumber())
                .toList();

        List<Payout> hitPayouts = payouts.stream()
                .filter(payout -> payout.getBetType().equals("複勝"))
                .filter(payout -> betFrameNumber.contains(Integer.valueOf(payout.getFrameNumber())))
                .toList();
        if(hitPayouts.isEmpty()) {
            return BetResult.builder()
                    .payout(0)
                    .hit(false)
                    .build();
        } else {
            Float payout = hitPayouts.stream()
                    .map(payout1 -> payout1.getPayout() / 100 / betFrameNumber.size())
                    .reduce(Float::sum).get();
            return BetResult.builder()
                    .payout(payout)
                    .hit(true)
                    .build();
        }
    }

    private BetResult judgeWide(List<RaceHorse> raceHorses, List<Integer> jikuBetHorseIds,List<Integer> himoBetHorseIds,List<Payout> payouts) {
        if (jikuBetHorseIds.isEmpty() || himoBetHorseIds.isEmpty()) {
            return null;
        }

        List<Integer> jikuFrameNumber = jikuBetHorseIds.stream().map(
                        betHorseId -> raceHorses.stream().filter(raceHorse -> Objects.equals(raceHorse.getHorse().getId(), betHorseId)).findFirst().get().getFrameNumber())
                .toList();
        List<Integer> himoFrameNumber = himoBetHorseIds.stream().map(
                        betHorseId -> raceHorses.stream().filter(raceHorse -> Objects.equals(raceHorse.getHorse().getId(), betHorseId)).findFirst().get().getFrameNumber())
                .toList();

        List<Payout> hitPayouts = payouts.stream()
                .filter(payout -> payout.getBetType().equals("ワイド"))
                .filter(payout -> {
                    List<Integer> hitFrameNumbers = Arrays.stream(payout.getFrameNumber().split("-"))
                            .map(Integer::valueOf).toList();

                    Boolean jikuHit = hitFrameNumbers.stream()
                            .map(jikuFrameNumber::contains)
                            .reduce(Boolean::logicalOr).get();

                    Boolean himoHit = hitFrameNumbers.stream()
                            .map(himoFrameNumber::contains)
                            .reduce(Boolean::logicalOr).get();

                    return jikuHit && himoHit;
                })
                .toList();
        if(hitPayouts.isEmpty()) {
            return BetResult.builder()
                    .payout(0)
                    .hit(false)
                    .build();
        } else {
            Float payout = hitPayouts.stream()
                    .map(payout1 -> payout1.getPayout()/100 / (jikuFrameNumber.size() * himoFrameNumber.size()))
                    .reduce(Float::sum).get();
            return BetResult.builder()
                    .payout(payout)
                    .hit(true)
                    .build();
        }
    }

    private Map<String,BetResult> betRace(String key, BetResult betResult, Confident confident, Race race) {
        Map<String,BetResult> results = new HashMap<>();
        results.put(key + " レース:全部",betResult);
        results.put(key + " レース:買い ",confident.isBuyFlag() ?  betResult : null);
        results.put(key + " レース:自信 ",confident.isConfidentFlag() ? betResult : null);
        results.put(key + " レース:カオス ",confident.isChaosFlag() ? betResult : null);
//        results.put(key + " レース:2歳 ", Objects.equals(race.getOldLimit(), "2歳") ? betResult : null);
//        results.put(key + " レース:3歳 ", Objects.equals(race.getOldLimit(), "3歳") ? betResult : null);
//        results.put(key + " レース:3歳以上 ",Objects.equals(race.getOldLimit(), "3歳以上") ? betResult : null);
//        results.put(key + " レース:4歳以上 ",Objects.equals(race.getOldLimit(), "4歳以上") ? betResult : null);
//        results.put(key + " レース:買い,2歳 ",confident.isBuyFlag() && Objects.equals(race.getOldLimit(), "2歳") ?  betResult : null);
//        results.put(key + " レース:買い,3歳 ",confident.isBuyFlag() && Objects.equals(race.getOldLimit(), "3歳") ?  betResult : null);
//        results.put(key + " レース:買い,3歳以上 ",confident.isBuyFlag() && Objects.equals(race.getOldLimit(), "3歳以上") ?  betResult : null);
//        results.put(key + " レース:買い,4歳以上 ",confident.isBuyFlag() && Objects.equals(race.getOldLimit(), "4歳以上") ?  betResult : null);
//        results.put(key + " レース:自信,2歳 ",confident.isConfidentFlag() && Objects.equals(race.getOldLimit(), "2歳") ?  betResult : null);
//        results.put(key + " レース:自信,3歳 ",confident.isConfidentFlag() && Objects.equals(race.getOldLimit(), "3歳") ?  betResult : null);
//        results.put(key + " レース:自信,3歳以上 ",confident.isConfidentFlag() && Objects.equals(race.getOldLimit(), "3歳以上") ?  betResult : null);
//        results.put(key + " レース:自信,4歳以上 ",confident.isConfidentFlag() && Objects.equals(race.getOldLimit(), "4歳以上") ?  betResult : null);
//        results.put(key + " レース:カオス,2歳 ",confident.isChaosFlag() && Objects.equals(race.getOldLimit(), "2歳") ?  betResult : null);
//        results.put(key + " レース:カオス,3歳 ",confident.isChaosFlag() && Objects.equals(race.getOldLimit(), "3歳") ?  betResult : null);
//        results.put(key + " レース:カオス,3歳以上 ",confident.isChaosFlag() && Objects.equals(race.getOldLimit(), "3歳以上") ?  betResult : null);
//        results.put(key + " レース:カオス,4歳以上 ",confident.isChaosFlag() && Objects.equals(race.getOldLimit(), "4歳以上") ?  betResult : null);
        return results;
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
    private List<Integer> betMaxExpect(List<RaceHorse> raceHorses, List<HorseScore> horseScores,float limitExpect) {
        Map<Integer,Double> horseExpects = getHorseExpects(raceHorses,horseScores);

        Integer buyHorse = null;
        double maxExpect = 0;
        for (Map.Entry<Integer,Double> horseExpect : horseExpects.entrySet()) {
            if(horseExpect.getValue() > maxExpect) {
                buyHorse = horseExpect.getKey();
                maxExpect = horseExpect.getValue();
            }
        }
        if ( maxExpect >= limitExpect ) {
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
    private List<Integer> betExpectOver(List<RaceHorse> raceHorses, List<HorseScore> horseScores, float limit) {
        Map<Integer,Double> horseExpects = getHorseExpects(raceHorses,horseScores);

        List<Integer> buyHorseIds = new ArrayList<>();
        for (Map.Entry<Integer,Double> horseExpect : horseExpects.entrySet()) {
            if(horseExpect.getValue() >= limit) {
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

    private Map<String,BetResult> mergeMap(Map<String,BetResult> map1, Map<String,BetResult> map2) {
        map1.putAll(map2);
        return map1;
    }
}
