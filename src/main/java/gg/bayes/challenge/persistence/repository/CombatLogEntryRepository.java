package gg.bayes.challenge.persistence.repository;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CombatLogEntryRepository extends JpaRepository<CombatLogEntryEntity, Long> {

    @Query(
            value = "SELECT actor, count(*) FROM DOTA_COMBAT_LOG log " +
                    "WHERE log.match_id = :matchId " +
                    "and log.entry_type = :type " +
                    "group by actor",
            nativeQuery = true)
    List<Map<String,Object>> findHeroKills(Long matchId, String type);

    @Query(
            value = "SELECT entry_timestamp, item FROM DOTA_COMBAT_LOG log " +
                    "WHERE log.match_id = :matchId " +
                    "and log.entry_type = :type " +
                    "and log.actor = :heroName",
            nativeQuery = true)
    List<Map<String,Object>> findHeroItems(Long matchId, String type, String heroName);

    @Query(
            value = "SELECT log.ability, log.ability_level item FROM DOTA_COMBAT_LOG log " +
                    "WHERE log.match_id = :matchId " +
                    "and log.entry_type = :type " +
                    "and log.actor = :heroName",
            nativeQuery = true)
    List<Map<String,Object>> findHeroSpells(Long matchId, String type, String heroName);

    @Query(
            value = "SELECT target, count(*), sum(damage) FROM DOTA_COMBAT_LOG log " +
                    "WHERE log.match_id = :matchId " +
                    "and log.entry_type = :type " +
                    "group by target " +
                    "having log.actor = :heroName" ,
            nativeQuery = true)
    List<Map<String,Object>> findHeroDamages(Long matchId, String type, String heroName);
}
