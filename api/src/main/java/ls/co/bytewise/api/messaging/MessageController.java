package ls.co.bytewise.api.messaging;

import jakarta.validation.Valid;
import ls.co.bytewise.api.user.User;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@Secured({"ROLE_SCHOOL_ADMIN", "ROLE_SYSTEM_ADMIN", "ROLE_PRINCIPAL", "ROLE_TEACHER", "ROLE_FINANCE_OFFICER", "ROLE_STUDENT"})
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/unread-count")
    public long unreadCount(@AuthenticationPrincipal User currentUser) {
        return messageService.unreadCount(currentUser);
    }

    @GetMapping("/recipients")
    public List<MessageUserDto> recipients(@AuthenticationPrincipal User currentUser) {
        return messageService.recipients(currentUser);
    }

    @GetMapping("/inbox")
    public List<MessageThreadDto> inbox(@AuthenticationPrincipal User currentUser) {
        return messageService.inbox(currentUser);
    }

    @GetMapping("/sent")
    public List<MessageThreadDto> sent(@AuthenticationPrincipal User currentUser) {
        return messageService.sent(currentUser);
    }

    @GetMapping("/inbox/page")
    public Page<MessageThreadDto> inboxPage(@AuthenticationPrincipal User currentUser, Pageable pageable) {
        return messageService.inboxPage(currentUser, safePage(pageable));
    }

    @GetMapping("/sent/page")
    public Page<MessageThreadDto> sentPage(@AuthenticationPrincipal User currentUser, Pageable pageable) {
        return messageService.sentPage(currentUser, safePage(pageable));
    }

    private Pageable safePage(Pageable pageable) {
        return PageRequest.of(Math.max(pageable.getPageNumber(), 0),
                Math.min(Math.max(pageable.getPageSize(), 1), 100));
    }

    @GetMapping("/threads/{threadId}")
    public List<MessageDto> thread(@AuthenticationPrincipal User currentUser, @PathVariable String threadId) {
        return messageService.openThread(currentUser, threadId);
    }

    @PostMapping
    public MessageDto send(@AuthenticationPrincipal User currentUser,
                           @Valid @RequestBody MessageSendDto dto) {
        return messageService.send(currentUser, dto);
    }

    @PutMapping("/{messageId}")
    public MessageDto edit(@AuthenticationPrincipal User currentUser,
                           @PathVariable Long messageId,
                           @Valid @RequestBody MessageEditDto dto) {
        return messageService.edit(currentUser, messageId, dto);
    }

    @DeleteMapping("/{messageId}")
    public void delete(@AuthenticationPrincipal User currentUser, @PathVariable Long messageId) {
        messageService.delete(currentUser, messageId);
    }
}
