package app.web.api;

import app.web.pojo.PojoDiscordMember;

public interface DiscordMemberServiceApi extends AbstractServiceApi<PojoDiscordMember> {

    PojoDiscordMember findByNickname(String nickname);

    PojoDiscordMember findByDiscordId(long discordId);

}
