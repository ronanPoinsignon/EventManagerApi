package app.web.service.event;

import app.back.dto.Event;
import app.utils.DiscordMemberUtils;
import app.utils.EventUtils;
import app.web.api.DiscordMemberServiceApi;
import app.web.api.EventServiceApi;
import app.web.exception.NotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class EventServiceParticipantTest {

    private final EventServiceApi service;
    private final EventUtils eventUtils;

    private final DiscordMemberServiceApi discordMemberService;
    private final DiscordMemberUtils discordMemberUtils;

    protected EventServiceParticipantTest(@Autowired EventServiceApi service, @Autowired EventUtils eventUtils, @Autowired DiscordMemberServiceApi discordMemberService, @Autowired DiscordMemberUtils discordMemberUtils) {
        this.service = service;
        this.eventUtils = eventUtils;
        this.discordMemberService = discordMemberService;
        this.discordMemberUtils = discordMemberUtils;
    }

    @Test
    @Order(1)
    void testAddParticipant() {
        var event = eventUtils.createBasicPojo();
        event = service.save(event);
        var member = discordMemberService.save(discordMemberUtils.createBasicPojo());

        event = service.addTo(event.getId(), List.of(member.getId()));
        Assertions.assertEquals(1, event.getParticipants().size());
        Assertions.assertEquals(member.getId(), event.getParticipants().getFirst().getId());
    }

    @Test
    @Order(2)
    void testAddParticipantParticipantNotFound() {
        var event = eventUtils.createBasicPojo();
        event = service.save(event);

        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(NotFoundException.class, () -> service.addTo(finalEvent.getId(), List.of(1L)));
        Assertions.assertTrue(event.getParticipants().isEmpty());
    }

    @Test
    @Order(3)
    void testAddParticipantEventNotFound() {
        var member = eventUtils.addDiscordMember(new Event());
        Assertions.assertThrows(NotFoundException.class, () -> service.addTo(1L, List.of(member.getId())));
    }

    @Test
    @Order(4)
    void testAddParticipantNull() {
        var event = eventUtils.createBasicPojo();
        eventUtils.addDiscordMember(event);
        event = service.save(event);
        Assertions.assertEquals(1, event.getParticipants().size());

        event = service.addTo(event.getId(), null);
        Assertions.assertEquals(1, event.getParticipants().size());
    }

    @Test
    @Order(5)
    void testRemoveParticipant() {
        var event = eventUtils.createBasicPojo();
        var member = eventUtils.addDiscordMember(event);
        event = service.save(event);
        Assertions.assertEquals(1, event.getParticipants().size());

        event = service.removeTo(event.getId(), List.of(member.getId()));
        Assertions.assertTrue(event.getParticipants().isEmpty());
    }

    @Test
    @Order(6)
    void testRemoveParticipantParticipantNotFound() {
        var event = eventUtils.createBasicPojo();
        var member = eventUtils.addDiscordMember(event);
        event = service.save(event);
        Assertions.assertEquals(1, event.getParticipants().size());

        app.web.pojo.PojoEvent finalEvent = event;
        Assertions.assertThrows(NotFoundException.class, () -> service.removeTo(finalEvent.getId(), List.of(2L)));

        event = service.findOne(event.getId());
        Assertions.assertEquals(1, event.getParticipants().size());
    }

    @Test
    @Order(7)
    void testRemoveParticipantEventNotFound() {
        var member = eventUtils.addDiscordMember(new Event());
        Assertions.assertThrows(NotFoundException.class, () -> service.removeTo(1L, List.of(member.getId())));
    }

    @Test
    @Order(4)
    void testRemoveParticipantNull() {
        var event = eventUtils.createBasicPojo();
        var member = eventUtils.addDiscordMember(event);
        event = service.save(event);
        Assertions.assertEquals(1, event.getParticipants().size());

        event = service.removeTo(event.getId(), null);
        Assertions.assertEquals(1, event.getParticipants().size());
        Assertions.assertEquals(member.getId(), event.getParticipants().getFirst().getId());
    }

}
