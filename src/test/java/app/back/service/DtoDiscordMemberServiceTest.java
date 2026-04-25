package app.back.service;

import app.back.dto.DiscordMember;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Transactional
public class DtoDiscordMemberServiceTest {

    private final DtoDiscordMemberService discordMemberService;

    private final DiscordMember BASE_DISCORD_MEMBER;

    public DtoDiscordMemberServiceTest(@Autowired DtoDiscordMemberService discordMemberService) {
        this.discordMemberService = discordMemberService;

        BASE_DISCORD_MEMBER = new DiscordMember();
        BASE_DISCORD_MEMBER.setDiscordId(1L);
        BASE_DISCORD_MEMBER.setFirstname("firstname");
        BASE_DISCORD_MEMBER.setNickname("nickname");
    }

    private DiscordMember createDiscordMember() {
        var discordMember = new DiscordMember();
        discordMember.setDiscordId(BASE_DISCORD_MEMBER.getDiscordId());
        discordMember.setNickname(BASE_DISCORD_MEMBER.getNickname());
        discordMember.setFirstname(BASE_DISCORD_MEMBER.getFirstname());

        return discordMember;
    }

    @Test
    @Order(1)
    void testCreate() {
        var discordMember = createDiscordMember();

        Assertions.assertNull(discordMember.getId());
        var result = discordMemberService.save(discordMember);

        Assertions.assertNotNull(discordMember.getId());
        Assertions.assertEquals(discordMember.getId(), result.getId());
        Assertions.assertEquals(discordMember.getDiscordId(), result.getDiscordId());
        Assertions.assertEquals(discordMember.getNickname(), result.getNickname());
        Assertions.assertEquals(discordMember.getFirstname(), result.getFirstname());
    }

    @Test
    @Order(2)
    void testFindOk() {
        var discordMember = createDiscordMember();
        var result = discordMemberService.save(discordMember);
        var resultFind = discordMemberService.findById(result.getId()).orElseThrow(() -> new RuntimeException("Aucun objet trouvé."));
        Assertions.assertEquals(result, resultFind);
    }

    @Test
    @Order(3)
    void testDelete() {
        var discordMember = createDiscordMember();
        discordMemberService.save(discordMember);
        discordMemberService.delete(discordMember.getId());
        var result = discordMemberService.findById(discordMember.getId());
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @Order(3)
    void testFindNok() {
        var result = discordMemberService.findById(3L);
        Assertions.assertTrue(result.isEmpty());
    }

}
