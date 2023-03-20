package gg.bayes.challenge.unit;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import gg.bayes.challenge.persistence.model.MatchEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static gg.bayes.challenge.service.MatchEventParser.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MatchEventParserTest {


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("PURCHASE_ITEM parser should...")
    public class PurchaseItemTests {

        @ParameterizedTest
        @MethodSource("successfullyParsePurchaseEventsSource")
        public void successfullyParsePurchaseEvents(
                String line, Long timestamp, String hero, String item) {

            MatchEntity matchEntity = new MatchEntity();
            matchEntity.setId(1L);
            CombatLogEntryEntity combatLogEntryEntity = (CombatLogEntryEntity) PURCHASE_ITEM.parseEvent(matchEntity, line);

            assertEquals(combatLogEntryEntity.getTimestamp(), timestamp);
            assertEquals(combatLogEntryEntity.getActor(), hero);
            assertEquals(combatLogEntryEntity.getItem(), item);
        }


        public Stream<Arguments> successfullyParsePurchaseEventsSource() {
            return Stream.of(
                    Arguments.of(
                            "[00:08:46.693] npc_dota_hero_snapfire buys item item_clarity",
                            526693L,
                            "snapfire",
                            "clarity"));
        }

        @Test
        public void doesntParseOtherTypesOfEvents() {

            MatchEntity matchEntity = new MatchEntity();
            matchEntity.setId(1l);
            String events =
                    "[00:00:04.999] game state is now 2\n"
                            + "[00:08:43.460] npc_dota_hero_pangolier casts ability pangolier_swashbuckle (lvl 1) on dota_unknown\n"
                            + "[00:09:11.953] npc_dota_hero_abyssal_underlord uses item_quelling_blade\n"
                            + "[00:10:41.998] npc_dota_hero_abyssal_underlord casts ability abyssal_underlord_firestorm (lvl 1) on dota_unknown\n"
                            + "[00:10:42.031] npc_dota_hero_bane hits npc_dota_hero_abyssal_underlord with dota_unknown for 51 damage (740->689)\n"
                            + "[00:11:02.859] npc_dota_hero_rubick uses item_tango\n"
                            + "[00:12:15.108] npc_dota_neutral_harpy_scout is killed by npc_dota_hero_pangolier";

            events.lines().forEach(line -> assertNull(PURCHASE_ITEM.parseEvent(matchEntity, line)));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Kill hero parser should...")
    public class KillHeroTests {

        @ParameterizedTest
        @MethodSource("successfullyParseKillHeroEventsSource")
        public void successfullyParseKillHeroEvents(
                String line, Long timestamp, String hero, String target) {

            MatchEntity matchEntity = new MatchEntity();
            matchEntity.setId(1L);
            CombatLogEntryEntity combatLogEntryEntity = (CombatLogEntryEntity) KILL_HERO.parseEvent(matchEntity, line);

            assertEquals(combatLogEntryEntity.getTimestamp(), timestamp);
            assertEquals(combatLogEntryEntity.getActor(), hero);
            assertEquals(combatLogEntryEntity.getTarget(), target);
        }


        public Stream<Arguments> successfullyParseKillHeroEventsSource() {
            return Stream.of(
                    Arguments.of(
                            "[00:08:46.693] npc_dota_hero_snapfire is killed by npc_dota_hero_mars",
                            526693L,
                            "mars",
                            "snapfire"));
        }


        @Test
        public void doesntParseOtherTypesOfEvents() {

            MatchEntity matchEntity = new MatchEntity();
            matchEntity.setId(1l);
            String events =
                    "[00:00:04.999] game state is now 2\n"
                            + "[00:08:43.460] npc_dota_hero_pangolier casts ability pangolier_swashbuckle (lvl 1) on dota_unknown\n"
                            + "[00:09:11.953] npc_dota_hero_abyssal_underlord uses item_quelling_blade\n"
                            + "[00:10:41.998] npc_dota_hero_abyssal_underlord casts ability abyssal_underlord_firestorm (lvl 1) on dota_unknown\n"
                            + "[00:10:42.031] npc_dota_hero_bane hits npc_dota_hero_abyssal_underlord with dota_unknown for 51 damage (740->689)\n"
                            + "[00:11:02.859] npc_dota_hero_rubick uses item_tango\n"
                            + "[00:12:15.108] npc_dota_neutral_harpy_scout is killed by npc_dota_hero_pangolier";

            events.lines().forEach(line -> assertNull(KILL_HERO.parseEvent(matchEntity, line)));
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Damage Hero parser should...")
    public class DamageHeroTests {

        @ParameterizedTest
        @MethodSource("successfullyParseDamageHeroEventsSource")
        public void successfullyParseDamageHeroEvents(
                String line, Long timestamp, String hero, String target, Integer damage) {

            MatchEntity matchEntity = new MatchEntity();
            matchEntity.setId(1L);
            CombatLogEntryEntity combatLogEntryEntity = (CombatLogEntryEntity) DAMAGE_HERO.parseEvent(matchEntity, line);

            assertEquals(combatLogEntryEntity.getTimestamp(), timestamp);
            assertEquals(combatLogEntryEntity.getActor(), hero);
            assertEquals(combatLogEntryEntity.getTarget(), target);
            assertEquals(combatLogEntryEntity.getDamage(), damage);
        }


        public Stream<Arguments> successfullyParseDamageHeroEventsSource() {
            return Stream.of(
                    Arguments.of(
                            "[00:08:46.693] npc_dota_hero_pangolier hits npc_dota_hero_rubick with dota_unknown for 29 damage (71->42)",
                            526693L,
                            "pangolier",
                            "rubick",
                            29
                            ));
        }


        @Test
        public void doesntParseOtherTypesOfEvents() {

            MatchEntity matchEntity = new MatchEntity();
            matchEntity.setId(1l);
            String events =
                    "[00:00:04.999] game state is now 2\n"
                            + "[00:08:43.460] npc_dota_hero_pangolier casts ability pangolier_swashbuckle (lvl 1) on dota_unknown\n"
                            + "[00:09:11.953] npc_dota_hero_abyssal_underlord uses item_quelling_blade\n"
                            + "[00:10:41.998] npc_dota_hero_abyssal_underlord casts ability abyssal_underlord_firestorm (lvl 1) on dota_unknown\n"
                            + "[00:11:02.859] npc_dota_hero_rubick uses item_tango\n";

            events.lines().forEach(line -> assertNull(DAMAGE_HERO.parseEvent(matchEntity, line)));
        }

    }



    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Cast Spell parser should...")
    public class CastSpellTests {

        @ParameterizedTest
        @MethodSource("successfullyParseCastSpellEventsSource")
        public void successfullyParseCastSpellEvents(
                String line, Long timestamp, String hero, String target, String ability, Integer abilityLevel) {

            MatchEntity matchEntity = new MatchEntity();
            matchEntity.setId(1L);
            CombatLogEntryEntity combatLogEntryEntity = (CombatLogEntryEntity) CAST_SPELL.parseEvent(matchEntity, line);

            assertEquals(combatLogEntryEntity.getTimestamp(), timestamp);
            assertEquals(combatLogEntryEntity.getActor(), hero);
            assertEquals(combatLogEntryEntity.getTarget(), target);
            assertEquals(combatLogEntryEntity.getAbility(), ability);
            assertEquals(combatLogEntryEntity.getAbilityLevel(), abilityLevel);
        }


        public Stream<Arguments> successfullyParseCastSpellEventsSource() {
            return Stream.of(
                    Arguments.of(
                            "[00:08:46.693] npc_dota_hero_rubick casts ability rubick_spell_steal (lvl 1) on npc_dota_hero_snapfire",
                            526693L,
                            "rubick",
                            "snapfire",
                            "rubick_spell_steal",
                            1
                    ));
        }


        @Test
        public void doesntParseOtherTypesOfEvents() {
            MatchEntity matchEntity = new MatchEntity();
            matchEntity.setId(1l);
            String events =
                    "[00:00:04.999] game state is now 2\n"
                            + "[00:09:11.953] npc_dota_hero_abyssal_underlord uses item_quelling_blade\n"
                            + "[00:10:42.031] npc_dota_hero_bane hits npc_dota_hero_abyssal_underlord with dota_unknown for 51 damage (740->689)\n"
                            + "[00:11:02.859] npc_dota_hero_rubick uses item_tango\n"
                            + "[00:12:15.108] npc_dota_neutral_harpy_scout is killed by npc_dota_hero_pangolier";

            events.lines().forEach(line -> assertNull(CAST_SPELL.parseEvent(matchEntity, line)));
        }

    }

}
