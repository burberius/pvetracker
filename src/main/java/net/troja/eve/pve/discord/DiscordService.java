package net.troja.eve.pve.discord;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

@Service
@Log4j2
public class DiscordService implements EventListener {
    @Value("${discord.token}")
    private String token;
    @Value("${discord.channel}")
    private String channelId;

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
}
