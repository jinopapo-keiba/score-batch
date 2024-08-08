package org.example.job;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.BetResult;
import org.example.repository.RaceRepository;
import org.example.service.BetService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@AllArgsConstructor
@Slf4j
public class MainJob implements CommandLineRunner {
    private final BetService betService;
    private final RaceRepository raceRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Integer> verifyRaces = raceRepository.fetchAllRace();

        AtomicInteger total = new AtomicInteger();

        List<Map<String,BetResult>> results = new ArrayList<>();

        verifyRaces
                .parallelStream()
                .forEach((verifyRace) -> {
                    results.add(betService.calcPaymentPrice(verifyRace));
                    total.addAndGet(1);

                    if(total.get() % 100 == 0) {
                        log.info("処理中:" + total.get() + "/" + verifyRaces.size());
                    }
                }
        );

        Map<String,List<BetResult>> resultMap = new TreeMap<>();
        results.forEach( result -> {
            for (Map.Entry<String,BetResult> resultEntry :result.entrySet()) {
                resultMap.computeIfAbsent(resultEntry.getKey(), k -> new ArrayList<>()).add(resultEntry.getValue());
            }
        });
        log.info("合計:" + verifyRaces.size());
        for (Map.Entry<String,List<BetResult>> resultEntry: resultMap.entrySet()) {
            List<BetResult> betResults = resultEntry.getValue().stream()
                    .filter(Objects::nonNull).toList();

            if (betResults.isEmpty()) {
                continue;
            }

            int hitCount = betResults.stream()
                    .filter(BetResult::isHit)
                    .toList()
                    .size();
            double totalPayout= betResults.stream().mapToDouble(BetResult::getPayout).sum();
            log.info(resultEntry.getKey() + "的中率:" + (double) hitCount / betResults.size());
            log.info(resultEntry.getKey() + "期待値:" + totalPayout / betResults.size());
        }
    }
}
