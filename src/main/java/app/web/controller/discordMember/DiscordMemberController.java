package app.web.controller.discordMember;

import app.web.api.DiscordMemberServiceApi;
import app.web.pojo.PojoUserAttributes;
import app.web.pojo.PojoEntity;
import app.web.service.DiscordMemberService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discord-members")
public class DiscordMemberController {

    private final DiscordMemberServiceApi discordMemberService;

    public DiscordMemberController(DiscordMemberService discordMemberService) {
        this.discordMemberService = discordMemberService;
    }

    @PostMapping("/save")
    public PojoUserAttributes create(@RequestBody PojoUserAttributes pojoUserAttributes) {
        return discordMemberService.save(pojoUserAttributes);
    }

    @GetMapping("/findById")
    public PojoUserAttributes findById(@RequestParam("id") Long id) {
        return discordMemberService.findOne(id);
    }

    @GetMapping("/findByDiscordId")
    public PojoEntity findByDiscordId(@RequestParam("discordId") long discordId) {
        return discordMemberService.findByDiscordId(discordId);
    }
}
