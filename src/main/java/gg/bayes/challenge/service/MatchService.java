package gg.bayes.challenge.service;

import java.util.List;
import java.util.Map;

public interface MatchService {

    Long ingestMatch(String payload);

    List<Map<String, Object>> fetchHeroesKillCounts(Long matchId);

    List<Map<String, Object>> fetchHeroItems(Long matchId, String hero);

    List<Map<String, Object>> fetchHeroSpellsCasts(Long matchId, String hero);

    List<Map<String, Object>> fetchHeroDamages(Long matchId, String hero);
}
