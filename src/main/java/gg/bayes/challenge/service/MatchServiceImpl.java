package gg.bayes.challenge.service;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import gg.bayes.challenge.persistence.model.MatchEntity;
import gg.bayes.challenge.persistence.repository.CombatLogEntryRepository;
import gg.bayes.challenge.persistence.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class MatchServiceImpl implements MatchService {

    private final AtomicLong matchIdGenerator = new AtomicLong();

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private CombatLogEntryRepository combatLogEntryRepository;

    @Override
    @Transactional
    public Long ingestMatch(String payload) {
        Long matchId = matchIdGenerator.incrementAndGet();
        MatchEntity newMatchEntity = new MatchEntity();
        matchRepository.save(newMatchEntity);

        Set<CombatLogEntryEntity> combatLogEntryEntitySet = new HashSet<>();
        payload
                .lines()
                .forEach(
                        line -> {
                            Arrays.stream(MatchEventParser.values())
                                    .map(parser -> parser.parseEvent(newMatchEntity, line))
                                    .filter(e -> e != null)
                                    .findFirst()
                                    .ifPresent(e -> {
                                        combatLogEntryRepository.save(e);
                                        combatLogEntryEntitySet.add(e);
                                    });
                        });

        newMatchEntity.setCombatLogEntries(combatLogEntryEntitySet);
        matchRepository.save(newMatchEntity);
        return matchId;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> fetchHeroesKillCounts(Long matchId) {
        return combatLogEntryRepository.findHeroKills(matchId, CombatLogEntryEntity.Type.HERO_KILLED.toString());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> fetchHeroItems(Long matchId, String hero) {
        return combatLogEntryRepository.findHeroItems(matchId, CombatLogEntryEntity.Type.ITEM_PURCHASED.toString(), hero);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> fetchHeroSpellsCasts(Long matchId, String hero) {
        return combatLogEntryRepository.findHeroSpells(matchId, CombatLogEntryEntity.Type.SPELL_CAST.toString(), hero);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> fetchHeroDamages(Long matchId, String hero) {
        return combatLogEntryRepository.findHeroDamages(matchId, CombatLogEntryEntity.Type.DAMAGE_DONE.toString(), hero);
    }


}
