package net.troja.eve.pve.discord;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.troja.eve.pve.PvEApplication;
import net.troja.eve.pve.db.outcome.LootBean;
import net.troja.eve.pve.db.outcome.OutcomeBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Log4j2
public class DiscordService implements EventListener {
    private static final NumberFormat ISK_FORMAT = NumberFormat.getInstance(PvEApplication.DEFAULT_LOCALE);

    @Value("${discord.token}")
    private String token;
    @Value("${discord.channel}")
    private String channelId;
    @Value("${discord.min-loot-value}")
    private long minLootValue;
    @Value("${discord.min-item-value}")
    private long minItemValue;

    private JDA jda;
    private TextChannel channel;

    @PostConstruct
    public void init() {
        log.info("Init");
        try {
            jda = JDABuilder.createDefault(token).build();
            jda.addEventListener(this);
        } catch (LoginException e) {
            log.error("Could not create discord bot", e);
        }
    }

    public void sendMessage(String message) {
        if(channel != null) {
            channel.sendMessage(message).queue();
        } else {
            log.info("Channel null - {}", message);
        }
    }

    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            channel = jda.getTextChannelById(channelId);
            if(channel == null) {
                log.error("Could not get channel with id: {}", channelId);
            }
        }
        /*if(event instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageEvent = (MessageReceivedEvent) event;
            if(messageEvent.getAuthor().isBot()) {
                return;
            }
            log.info("Channel: {}", messageEvent.getTextChannel());
            sendMessage(messageEvent.getAuthor().getName() + " wrote " + messageEvent.getMessage().getContentDisplay());
        }*/
    }

    public void postOutcome(OutcomeBean outcomeDb) {
        List<LootBean> loot = outcomeDb.getLoot().stream()
                .filter(l -> l.getValue() * l.getCount() > minLootValue).collect(Collectors.toList());
        if(outcomeDb.getLootValue() < minLootValue) {
            return;
        }
        StringBuilder result = new StringBuilder();
        result.append("**").append(outcomeDb.getAccount().getCharacterName()).append("** just finished a *").append(outcomeDb.getSite().getName());
        Integer ded = outcomeDb.getSite().getDed();
        if(ded != null && ded > 0) {
            result.append(" (").append(ded).append("/10)");
        }
        result.append("* in a *").append(outcomeDb.getShip().getType());
        result.append("* getting **").append(ISK_FORMAT.format(outcomeDb.getLootValue())).append("** ISK");
        String lootString = getLootString(loot);
        if(lootString != null) {
            result.append(" with the following loot:\n").append(lootString);
        }
        sendMessage(result.toString());
    }

    private String getLootString(List<LootBean> loot) {
        StringBuilder result = new StringBuilder();
        loot.stream()
                .forEach(l -> {
                    if(result.length() > 0) {
                        result.append("\n");
                    }
                    result.append(l.getCount()).append(" x *").append(l.getName())
                            .append("*");
                    if(l.getValue() > 0) {
                        result.append(" **").append(ISK_FORMAT.format(Math.round(l.getValue()))).append("** ISK");
                    }
                });
        return result.length() == 0 ? null : result.toString();
    }

    protected void setMinLootValue(long minLootValue) {
        this.minLootValue = minLootValue;
    }

    protected void setMinItemValue(long minItemValue) {
        this.minItemValue = minItemValue;
    }

    protected void setJda(JDA jda) {
        this.jda = jda;
    }

    protected void setChannel(TextChannel channel) {
        this.channel = channel;
    }
}
