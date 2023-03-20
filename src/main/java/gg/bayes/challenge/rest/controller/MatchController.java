package gg.bayes.challenge.rest.controller;

import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroItem;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;
import gg.bayes.challenge.service.MatchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/match")
@Validated
public class MatchController {

    @Autowired
    private MatchService matchService;

    /**
     * Ingests a DOTA combat log file, parses and persists relevant events data. All events are associated with the same
     * match id.
     *
     * @param combatLog the content of the combat log file
     * @return the match id associated with the parsed events
     */
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Long> ingestCombatLog(@RequestBody @NotBlank String combatLog) {
        log.info("Starting ingesting Combat log");
        return ResponseEntity.ok(matchService.ingestMatch(combatLog));
    }

    /**
     * Fetches the heroes and their kill counts for the given match.
     *
     * @param matchId the match identifier
     * @return a collection of heroes and their kill counts
     */
    @GetMapping(
            path = "{matchId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<HeroKills>> getMatch(@PathVariable("matchId") Long matchId) {
        log.info("Fetch Hero Kills for matchId: {}", matchId);
        return ResponseEntity.ok(
                matchService.fetchHeroesKillCounts(matchId).stream()
                        .map(m -> {
                            String hero = (String) m.get("ACTOR");
                            BigInteger kills = (BigInteger) m.get("COUNT(*)");
                            return new HeroKills(hero, kills.intValue());
                        }).collect(Collectors.toList()));
    }

    /**
     * For the given match, fetches the items bought by the named hero.
     *
     * @param matchId  the match identifier
     * @param heroName the hero name
     * @return a collection of items bought by the hero during the match
     */
    @GetMapping(
            path = "{matchId}/{heroName}/items",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<HeroItem>> getHeroItems(
            @PathVariable("matchId") Long matchId,
            @PathVariable("heroName") String heroName) {
        log.info("Fetch Hero Items of {},  for matchId: {}", heroName, matchId);
        return ResponseEntity.ok(
                matchService.fetchHeroItems(matchId, heroName).stream()
                        .map(m -> {
                            String item = (String) m.get("ITEM");
                            BigInteger timeStamp = (BigInteger) m.get("ENTRY_TIMESTAMP");
                            return new HeroItem(item, timeStamp.longValue());
                        }).collect(Collectors.toList()));
    }

    /**
     * For the given match, fetches the spells cast by the named hero.
     *
     * @param matchId  the match identifier
     * @param heroName the hero name
     * @return a collection of spells cast by the hero and how many times they were cast
     */
    @GetMapping(
            path = "{matchId}/{heroName}/spells",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<HeroSpells>> getHeroSpells(
            @PathVariable("matchId") Long matchId,
            @PathVariable("heroName") String heroName) {
        log.info("Fetch Hero spells of {},  for matchId: {}", heroName, matchId);
        return ResponseEntity.ok(
                matchService.fetchHeroSpellsCasts(matchId, heroName).stream()
                        .map(m -> {
                            String ability = (String) m.get("ABILITY");
                            Integer abilityLevel = (Integer) m.get("ITEM");
                            return new HeroSpells(ability, abilityLevel);
                        }).collect(Collectors.toList()));
    }

    /**
     * For a given match, fetches damage done data for the named hero.
     *
     * @param matchId  the match identifier
     * @param heroName the hero name
     * @return a collection of "damage done" (target, number of times and total damage) elements
     */
    @GetMapping(
            path = "{matchId}/{heroName}/damage",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<HeroDamage>> getHeroDamages(
            @PathVariable("matchId") Long matchId,
            @PathVariable("heroName") String heroName) {
        log.info("Fetch Hero Damages of {} for matchId: {}", heroName, matchId);
        return ResponseEntity.ok(
                matchService.fetchHeroDamages(matchId, heroName).stream()
                        .map(
                                m -> {
                                    String target = (String) m.get("TARGET");
                                    BigInteger count = (BigInteger) m.get("COUNT(*)");
                                    BigInteger totalDamage = (BigInteger) m.get("SUM(DAMAGE)");
                                    return new HeroDamage(target, count.intValue(), totalDamage.intValue());
                                }
                        )
                        .collect(Collectors.toList()));
    }
}
