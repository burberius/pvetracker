package net.troja.eve.pve.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.troja.eve.pve.db.account.AccountBean;
import net.troja.eve.pve.db.outcome.LootBean;
import net.troja.eve.pve.db.outcome.OutcomeBean;
import net.troja.eve.pve.db.outcome.ShipBean;
import net.troja.eve.pve.db.sites.SiteBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscordServiceTest {
    @Mock
    private JDA jda;
    @Mock
    private TextChannel channel;
    @Mock
    private MessageCreateAction messageAction;

    private DiscordService classToTest;

    @BeforeEach
    void setUp() {
        classToTest = new DiscordService();
        classToTest.setJda(jda);
        classToTest.setChannel(channel);
        classToTest.setMinItemValue(1_000_000L);
        classToTest.setMinLootValue(10_000_000L);
    }

    @Test
    void postOutcome() {
        OutcomeBean outcome = createOutcome();
        String expected = """
                **Marvin** just finished a *Angel Cartel's Red Light District (5/10)* in a *Gila* getting \
                **12,345,678** ISK with the following loot:
                2 x *Gistum C-Type 50MN Microwarpdrive* **123,455,234** ISK
                1 x *Cynabal Blueprint* **111,111,111** ISK""";
        when(channel.sendMessage(expected)).thenReturn(messageAction);

        classToTest.postOutcome(outcome);

        verify(messageAction).queue();
    }

    @Test
    void postOutcomeDedNull() {
        OutcomeBean outcome = createOutcome();
        outcome.getSite().setDed(null);
        outcome.getSite().setName("Angel Den");
        outcome.setLoot(Collections.emptyList());
        String expected = "**Marvin** just finished a *Angel Den* in a *Gila* getting **12,345,678** ISK";
        when(channel.sendMessage(expected)).thenReturn(messageAction);

        classToTest.postOutcome(outcome);

        verify(messageAction).queue();
    }


    @Test
    void postOutcomeDedZero() {
        OutcomeBean outcome = createOutcome();
        outcome.getSite().setDed(0);
        outcome.getSite().setName("Angel Den");
        outcome.setLoot(Collections.emptyList());
        String expected = "**Marvin** just finished a *Angel Den* in a *Gila* getting **12,345,678** ISK";
        when(channel.sendMessage(expected)).thenReturn(messageAction);

        classToTest.postOutcome(outcome);

        verify(messageAction).queue();
    }

    @Test
    void postOutcomeLowIsk() {
        OutcomeBean outcome = createOutcome();
        outcome.setLootValue(123L);
        outcome.setLoot(List.of(outcome.getLoot().getFirst()));

        classToTest.postOutcome(outcome);

        verifyNoInteractions(channel);
    }

    @Test
    void postOutcomeNoValueLoot() {
        OutcomeBean outcome = createOutcome();
        LootBean lootBean = new LootBean();
        lootBean.setValue(12345d);
        lootBean.setCount(1);
        lootBean.setName("Nothing");
        outcome.setLoot(List.of(lootBean));
        String expected = "**Marvin** just finished a *Angel Cartel's Red Light District (5/10)* in a *Gila* getting **12,345,678** ISK";
        when(channel.sendMessage(expected)).thenReturn(messageAction);

        classToTest.postOutcome(outcome);

        verify(messageAction).queue();
    }

    private OutcomeBean createOutcome() {
        AccountBean accountBean = new AccountBean();
        accountBean.setCharacterName("Marvin");
        OutcomeBean outcomeBean = new OutcomeBean();
        outcomeBean.setAccount(accountBean);
        outcomeBean.setLootValue(12_345_678L);
        ShipBean shipBean = new ShipBean();
        shipBean.setType("Gila");
        outcomeBean.setShip(shipBean);
        SiteBean siteBean = new SiteBean();
        siteBean.setName("Angel Cartel's Red Light District");
        siteBean.setDed(5);
        outcomeBean.setSite(siteBean);
        LootBean lootBean = new LootBean();
        lootBean.setValue(123_455_234d);
        lootBean.setCount(2);
        lootBean.setName("Gistum C-Type 50MN Microwarpdrive");
        LootBean lootBean2 = new LootBean();
        lootBean2.setValue(111_111_111d);
        lootBean2.setCount(1);
        lootBean2.setName("Cynabal Blueprint");
        outcomeBean.setLoot(List.of(lootBean, lootBean2));
        return outcomeBean;
    }

}