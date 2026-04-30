package app.back.service;

import app.back.api.DtoDiscordMemberServiceApi;
import app.back.dto.DiscordMember;
import app.back.exception.BackBadRequestException;
import app.back.repository.DiscordMemberRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DtoDiscordMemberService extends DtoAbstractEntityService<DiscordMember, @NonNull DiscordMemberRepository> implements DtoDiscordMemberServiceApi {

    protected DtoDiscordMemberService(DiscordMemberRepository repository) {
        super(repository);
    }

    @Override
    public DiscordMember save(DiscordMember entity) {
        if(entity.getDiscordId() == null) {
            throw new BackBadRequestException("Il est obligatoire d'avoir un discordId.");
        }

        return super.save(entity);
    }

    @Override
    protected void update(DiscordMember entityToSave, DiscordMember dbEntity) {
        dbEntity.setDiscordId(entityToSave.getDiscordId());
        dbEntity.setFirstname(entityToSave.getFirstname());
        dbEntity.setNickname(entityToSave.getNickname());
    }

    @Override
    public Optional<DiscordMember> findByDiscordId(Long id) {
        if(id == null) {
            return Optional.empty();
        }

        return repository.findByDiscordId(id);
    }

    @Override
    public List<DiscordMember> findByDiscordId(List<Long> idList) {
        if(idList == null || idList.isEmpty()) {
            return new ArrayList<>();
        }

        return repository.findByDiscordId(idList);
    }

    @Override
    public Optional<DiscordMember> findByNickname(String nickname) {
        return repository.findByNickname(nickname);
    }
}
