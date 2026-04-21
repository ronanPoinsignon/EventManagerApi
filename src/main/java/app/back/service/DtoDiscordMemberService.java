package app.back.service;

import app.back.dto.DiscordMember;
import app.back.repository.DiscordMemberRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DtoDiscordMemberService extends DtoAbstractEntityService<DiscordMember, @NonNull DiscordMemberRepository> {

    protected DtoDiscordMemberService(DiscordMemberRepository repository) {
        super(repository);
    }

    @Override
    public DiscordMember save(DiscordMember entity) {
        var dbEntityOptional = repository.findByDiscordId(entity.getDiscordId());
        if(dbEntityOptional.isEmpty()) {
            return repository.save(entity);
        } else {
            var dbEntity = dbEntityOptional.get();
            entity.write(dbEntity);
            return repository.save(dbEntity);
        }
    }

    @Override
    public Optional<DiscordMember> findById(long id) {
        return repository.findByDiscordId(id);
    }

    public Optional<DiscordMember> findByNickname(String nickname) {
        return repository.findByNickname(nickname);
    }
}
