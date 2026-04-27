package app.web.controller;

import app.web.api.DiscordMemberServiceApi;
import app.web.pojo.PojoDiscordMember;
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
    public PojoDiscordMember create(@RequestBody PojoDiscordMember pojoDiscordMember) {
        return discordMemberService.save(pojoDiscordMember);
    }

    @GetMapping("/findById")
    public PojoDiscordMember findById(Long id) {
        return discordMemberService.findOne(id);
    }

    @GetMapping("/findByNickname")
    public PojoEntity findByNickname(String nickname) {
        return discordMemberService.findByNickname(nickname);
    }
}
