package app.web.controller.discordMember;

import app.web.api.UserAttributesServiceApi;
import app.web.pojo.PojoUserAttributes;
import app.web.pojo.PojoEntity;
import app.web.service.UserAttributesService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-attributes")
public class UserAttributesController {

    private final UserAttributesServiceApi discordMemberService;

    public UserAttributesController(UserAttributesService userAttributesService) {
        this.discordMemberService = userAttributesService;
    }

    @PostMapping("/link-discord")
    public PojoUserAttributes linkDiscordId(@RequestParam(name = "discordId") long discordId) {
        return discordMemberService.linkDiscordId(discordId);
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
