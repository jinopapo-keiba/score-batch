package org.example.job;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.BetResult;
import org.example.repository.RaceRepository;
import org.example.service.BetService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
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

        AtomicInteger tanshouCount = new AtomicInteger();
        AtomicInteger umarenCount = new AtomicInteger();
        AtomicInteger hukuhyouCount = new AtomicInteger();
        AtomicInteger waidoCount = new AtomicInteger();
        AtomicInteger sanrenpukuCount = new AtomicInteger();
        AtomicInteger rentaiCount = new AtomicInteger();
        AtomicInteger chanceCount = new AtomicInteger();
        AtomicInteger popularTanshouCount = new AtomicInteger();
        AtomicInteger popularHukuhyouCount = new AtomicInteger();
        AtomicInteger popularRentaiCount = new AtomicInteger();

        verifyRaces
                .parallelStream()
                .forEach((verifyRace) -> {
                    BetResult betResult = betService.calcPaymentPrice(verifyRace);
                    boolean jikuFlag = betResult.isJikuFirst() || betResult.isJikuSecond() || betResult.isJikuThird();
                    if (betResult.isJikuFirst()) {
                        tanshouCount.addAndGet(1);
                    }
                    if ((betResult.isJikuFirst() || betResult.isJikuSecond()) && (betResult.isFirst() || betResult.isSecond())) {
                        umarenCount.addAndGet(1);
                    }
                    if (jikuFlag) {
                        hukuhyouCount.addAndGet(1);
                        if (betResult.isFirst() || betResult.isSecond() || betResult.isThird()) {
                            waidoCount.addAndGet(1);
                        }

                        if ((betResult.isFirst() && betResult.isSecond()) || (betResult.isFirst() && betResult.isThird()) || (betResult.isSecond() && betResult.isThird())) {
                            sanrenpukuCount.addAndGet(1);
                        }
                    }
                    if (jikuFlag || betResult.isFirst()) {
                        chanceCount.addAndGet(1);
                    }
                    if(betResult.isJikuFirst() || betResult.isJikuSecond()) {
                        rentaiCount.addAndGet(1);
                    }
                    if(betResult.isPopularFirst()) {
                        popularTanshouCount.addAndGet(1);
                    }
                    if(betResult.isPopularFirst() || betResult.isPopularSecond()) {
                        popularRentaiCount.addAndGet(1);
                        if(betResult.isPopularThird()) {
                            popularHukuhyouCount.addAndGet(1);
                        }
                    }
                    if(chanceCount.get() % 100 == 0) {
                        log.info("単勝的中率:" + (double) tanshouCount.get()/ verifyRaces.size());
                        log.info("連対率:" + (double) rentaiCount.get()/ verifyRaces.size());
                        log.info("副賞的中率:" + (double) hukuhyouCount.get()/ verifyRaces.size());
                        log.info("馬連的中率:" + (double) umarenCount.get()/ verifyRaces.size());
                        log.info("ワイド的中率:" + (double) waidoCount.get()/ verifyRaces.size());
                        log.info("３連複的中率:" + (double) sanrenpukuCount.get()/ verifyRaces.size());
                        log.info("1番人気単勝的中率:" + (double) popularTanshouCount.get()/ verifyRaces.size());
                        log.info("1番人気連対率:" + (double) popularRentaiCount.get()/ verifyRaces.size());
                        log.info("1番人気副賞的中率:" + (double) popularHukuhyouCount.get()/ verifyRaces.size());
                        log.info("単勝可能性:" + (double) chanceCount.get()/ verifyRaces.size());
                    }
                }
        );

        log.info("合計:" + verifyRaces.size());
        log.info("単勝的中率:" + (double) tanshouCount.get()/ verifyRaces.size());
        log.info("連対率:" + (double) rentaiCount.get()/ verifyRaces.size());
        log.info("副賞的中率:" + (double) hukuhyouCount.get()/ verifyRaces.size());
        log.info("馬連的中率:" + (double) umarenCount.get()/ verifyRaces.size());
        log.info("ワイド的中率:" + (double) waidoCount.get()/ verifyRaces.size());
        log.info("３連複的中率:" + (double) sanrenpukuCount.get()/ verifyRaces.size());
        log.info("1番人気単勝的中率:" + (double) popularTanshouCount.get()/ verifyRaces.size());
        log.info("1番人気連対率:" + (double) popularRentaiCount.get()/ verifyRaces.size());
        log.info("1番人気副賞的中率:" + (double) popularHukuhyouCount.get()/ verifyRaces.size());
        log.info("単勝可能性:" + (double) chanceCount.get()/ verifyRaces.size());
    }
}
