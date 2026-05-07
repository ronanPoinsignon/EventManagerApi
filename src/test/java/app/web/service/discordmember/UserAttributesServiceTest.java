package app.web.service.discordmember;

import app.utils.UserAttributesUtils;
import app.web.api.DiscordMemberServiceApi;
import app.web.pojo.PojoUserAttributes;
import app.web.service.BasicTestService;
import org.springframework.beans.factory.annotation.Autowired;

public class UserAttributesServiceTest extends BasicTestService<PojoUserAttributes, DiscordMemberServiceApi> {

    private final UserAttributesUtils userAttributesUtils;

    public UserAttributesServiceTest(@Autowired DiscordMemberServiceApi memberService, @Autowired UserAttributesUtils userAttributesUtils) {
        super(memberService);
        this.userAttributesUtils = userAttributesUtils;
    }

    @Override
    protected PojoUserAttributes createBasicPojo() {
        return userAttributesUtils.createBasicPojo();
    }

}
