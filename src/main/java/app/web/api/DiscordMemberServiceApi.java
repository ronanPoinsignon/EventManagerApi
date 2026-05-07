package app.web.api;

import app.web.pojo.PojoUserAttributes;

public interface DiscordMemberServiceApi extends AbstractServiceApi<PojoUserAttributes> {

    PojoUserAttributes findByDiscordId(long discordId);

}
