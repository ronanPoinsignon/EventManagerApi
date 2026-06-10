package app.web.controller.discordGuild;

import app.web.pojo.PojoDiscordGuild;
import app.web.pojo.ValueBased;
import app.web.service.DiscordGuildService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discordGuilds")
public class DiscordGuildController {

    private final DiscordGuildService discordGuildService;

    public DiscordGuildController(DiscordGuildService discordGuildService) {
        this.discordGuildService = discordGuildService;
    }

    @PostMapping("/setCommunicationChannel")
    public PojoDiscordGuild setGuildCommunicationChannel(@RequestParam("guildId") String guildId, @RequestParam("channelId") String channelId) {
        return discordGuildService.setGuildCommunicationChannel(guildId, channelId);
    }

    @GetMapping("/getCommunicationChannel")
    public ValueBased<String> getGuildCommunicationChannel(@RequestParam("guildId") String guildId) {
        var result = discordGuildService.getGuildCommunicationChannel(guildId);
        return new ValueBased<>(result);
    }
}
