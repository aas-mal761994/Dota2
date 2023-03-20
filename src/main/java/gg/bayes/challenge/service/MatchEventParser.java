package gg.bayes.challenge.service;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import gg.bayes.challenge.persistence.model.MatchEntity;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public enum MatchEventParser {

    PURCHASE_ITEM("^\\[(.*)\\] npc_dota_hero_(.*) buys item item_(.*)$") {
        @Override
        protected CombatLogEntryEntity createEvent(MatchEntity matchEntity, Matcher matcher) {
            CombatLogEntryEntity purchaseEntryLog = new CombatLogEntryEntity();
            purchaseEntryLog.setActor(matcher.group(2));
            purchaseEntryLog.setMatch(matchEntity);
            purchaseEntryLog.setItem(matcher.group(3));
            purchaseEntryLog.setType(CombatLogEntryEntity.Type.ITEM_PURCHASED);
            purchaseEntryLog.setTimestamp(PURCHASE_ITEM.parseTimestamp(matcher.group(1)));
            return purchaseEntryLog;
        }
    },

    KILL_HERO("^\\[(.*)\\] npc_dota_hero_(.*) is killed by npc_dota_hero_(.*)") {
        @Override
        protected CombatLogEntryEntity createEvent(MatchEntity matchEntity, Matcher matcher) {
            CombatLogEntryEntity killEntryLog = new CombatLogEntryEntity();
            killEntryLog.setMatch(matchEntity);
            killEntryLog.setActor(matcher.group(3));
            killEntryLog.setTarget(matcher.group(2));
            killEntryLog.setType(CombatLogEntryEntity.Type.HERO_KILLED);
            killEntryLog.setTimestamp(KILL_HERO.parseTimestamp(matcher.group(1)));
            return killEntryLog;
        }
    },

    DAMAGE_HERO(
            "^\\[(.*)\\] npc_dota_hero_(.*) hits npc_dota_hero_(.*) with (.*) for (\\d+) damage.*$") {
        @Override
        protected CombatLogEntryEntity createEvent(MatchEntity matchEntity, Matcher matcher) {
            CombatLogEntryEntity damageHeroEntryLog = new CombatLogEntryEntity();
            damageHeroEntryLog.setMatch(matchEntity);
            damageHeroEntryLog.setActor(matcher.group(2));
            damageHeroEntryLog.setTarget(matcher.group(3));
            damageHeroEntryLog.setDamage(Integer.parseInt(matcher.group(5)));
            damageHeroEntryLog.setType(CombatLogEntryEntity.Type.DAMAGE_DONE);
            damageHeroEntryLog.setTimestamp(DAMAGE_HERO.parseTimestamp(matcher.group(1)));
            return damageHeroEntryLog;
        }
    },

    CAST_SPELL("^\\[(.*)\\] npc_dota_hero_(.*) casts ability (.*) \\(lvl (\\d+)\\) on (.*)$") {
        @Override
        protected CombatLogEntryEntity createEvent(MatchEntity matchEntity, Matcher matcher) {
            String target;
            if (matcher.group(5).startsWith("npc_dota_hero_")) {
                target = matcher.group(5).substring(14);
            } else if (matcher.group(5).startsWith("npc_dota_")) {
                target = matcher.group(5).substring(9);
            } else {
                target = matcher.group(5);
            }
            CombatLogEntryEntity castSpellEntryLog = new CombatLogEntryEntity();
            castSpellEntryLog.setMatch(matchEntity);
            castSpellEntryLog.setActor(matcher.group(2));
            castSpellEntryLog.setTarget(target);
            castSpellEntryLog.setAbility(matcher.group(3));
            castSpellEntryLog.setAbilityLevel(Integer.parseInt(matcher.group(4)));
            castSpellEntryLog.setType(CombatLogEntryEntity.Type.SPELL_CAST);
            castSpellEntryLog.setTimestamp(CAST_SPELL.parseTimestamp(matcher.group(1)));
            return castSpellEntryLog;
        }
    };

    private final Pattern pattern;
    private final AtomicLong combatLogEntryGenerator = new AtomicLong();

    MatchEventParser(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    protected abstract CombatLogEntryEntity createEvent(MatchEntity matchEntity, Matcher matcher);

    public CombatLogEntryEntity parseEvent(MatchEntity matchEntity, String s) {
        Matcher matcher = pattern.matcher(s);
        return matcher.matches() ? createEvent(matchEntity, matcher) : null;
    }

    private Long parseTimestamp(String s) {
        return Duration.between(LocalTime.MIDNIGHT, LocalTime.parse(s)).toMillis();
    }


}
