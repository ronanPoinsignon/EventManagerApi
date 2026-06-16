package app.back.security;

import org.jspecify.annotations.NonNull;

public interface UserServiceApi {

    @NonNull User getUser();

}
