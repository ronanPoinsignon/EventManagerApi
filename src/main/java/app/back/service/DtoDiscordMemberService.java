package app.back.service;

import app.back.dto.DiscordMember;
import app.back.repository.DiscordMemberRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DtoDiscordMemberService extends DtoAbstractEntityService<DiscordMember, @NonNull DiscordMemberRepository> {

    protected DtoDiscordMemberService(DiscordMemberRepository repository) {
        super(repository);
    }

    @Override
    protected void update(DiscordMember entityToSave, DiscordMember dbEntity) {
        dbEntity.setDiscordId(entityToSave.getDiscordId());
        dbEntity.setFirstname(entityToSave.getFirstname());
        dbEntity.setNickname(entityToSave.getNickname());
    }

    public Optional<DiscordMember> findByDiscordId(long id) {
        return repository.findByDiscordId(id);
    }

    public List<DiscordMember> findByDiscordId(List<Long> idList) {
        if(idList == null || idList.isEmpty()) {
            return new ArrayList<>();
        }

        return repository.findByDiscordId(idList);
    }

    public Optional<DiscordMember> findByNickname(String nickname) {
        return repository.findByNickname(nickname);
    }
}
