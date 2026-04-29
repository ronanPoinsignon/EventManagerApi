package app.web.service.discordmember;

import app.utils.DiscordMemberUtils;
import app.web.api.DiscordMemberServiceApi;
import app.web.pojo.PojoDiscordMember;
import app.web.service.BasicTestService;
import org.springframework.beans.factory.annotation.Autowired;

public class DiscordMemberServiceTest extends BasicTestService<PojoDiscordMember, DiscordMemberServiceApi> {

    private final DiscordMemberUtils discordMemberUtils;

    public DiscordMemberServiceTest(@Autowired DiscordMemberServiceApi memberService, @Autowired DiscordMemberUtils discordMemberUtils) {
        super(memberService);
        this.discordMemberUtils = discordMemberUtils;
    }

    @Override
    protected PojoDiscordMember createBasicPojo() {
        return discordMemberUtils.createBasicPojo();
    }

}
