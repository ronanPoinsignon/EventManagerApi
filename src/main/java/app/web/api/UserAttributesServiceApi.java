package app.web.api;

import app.web.pojo.PojoUserAttributes;

public interface UserAttributesServiceApi extends AbstractServiceApi<PojoUserAttributes> {

    PojoUserAttributes findByDiscordId(long discordId);

    PojoUserAttributes linkDiscordId(long id);

}
