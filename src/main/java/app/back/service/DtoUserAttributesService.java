package app.back.service;

import app.back.api.DtoUserAttributesServiceApi;
import app.back.dto.UserAttributes;
import app.back.exception.BackBadRequestException;
import app.back.repository.UserAttributesRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DtoUserAttributesService extends DtoAbstractEntityService<UserAttributes, @NonNull UserAttributesRepository> implements DtoUserAttributesServiceApi {

    protected DtoUserAttributesService(UserAttributesRepository repository) {
        super(repository);
    }

    @Override
    public UserAttributes save(UserAttributes entity) {
        if(entity.getDiscordId() == null) {
            throw new BackBadRequestException("Il est obligatoire d'avoir un discord id.");
        }
        if(entity.getKeycloakUserId() == null) {
            throw new BackBadRequestException("Il est obligatoire d'avoir un keycloak id.");
        }

        return super.save(entity);
    }

    @Override
    protected void update(UserAttributes entityToSave, UserAttributes dbEntity) {
        dbEntity.setDiscordId(entityToSave.getDiscordId());
        dbEntity.setKeycloakUserId(entityToSave.getKeycloakUserId());
    }

    @Override
    public Optional<UserAttributes> findByDiscordId(Long id) {
        if(id == null) {
            return Optional.empty();
        }

        return repository.findByDiscordId(id);
    }

    @Override
    public List<UserAttributes> findByDiscordId(List<Long> idList) {
        if(idList == null || idList.isEmpty()) {
            return new ArrayList<>();
        }

        return repository.findByDiscordId(idList);
    }

    @Override
    public Optional<UserAttributes> findByKeycloakId(UUID userId) {
        return repository.findByKeycloakUserId(userId);
    }

    @Override
    public List<UserAttributes> findByKeycloakIds(List<UUID> userIds) {
        return repository.findByKeycloakUserIds(userIds);
    }

}
