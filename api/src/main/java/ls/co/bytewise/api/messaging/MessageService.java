package ls.co.bytewise.api.messaging;

import jakarta.persistence.EntityNotFoundException;
import ls.co.bytewise.api.common.Status;
import ls.co.bytewise.api.common.UserRole;
import ls.co.bytewise.api.school.School;
import ls.co.bytewise.api.school.SchoolRepository;
import ls.co.bytewise.api.security.TenantContext;
import ls.co.bytewise.api.user.User;
import ls.co.bytewise.api.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          SchoolRepository schoolRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
    }

    @Transactional(readOnly = true)
    public long unreadCount(User currentUser) {
        return messageRepository.countUnread(currentSchoolId(), currentUser.getId());
    }

    @Transactional(readOnly = true)
    public List<MessageUserDto> recipients(User currentUser) {
        Long schoolId = currentSchoolId();
        return userRepository.findAllBySchoolId(schoolId).stream()
                .filter(user -> user.getStatus() == Status.ACTIVE)
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .filter(user -> canMessage(currentUser, user))
                .sorted(java.util.Comparator.comparing(User::getFirstName)
                        .thenComparing(User::getLastName))
                .map(MessageUserDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MessageThreadDto> inbox(User currentUser) {
        return summarize(messageRepository.findAllBySchoolIdAndRecipientIdOrderBySentAtDesc(
                currentSchoolId(), currentUser.getId()), currentUser);
    }

    @Transactional(readOnly = true)
    public List<MessageThreadDto> sent(User currentUser) {
        return summarize(messageRepository.findAllBySchoolIdAndSenderIdOrderBySentAtDesc(
                currentSchoolId(), currentUser.getId()), currentUser);
    }

    @Transactional(readOnly = true)
    public Page<MessageThreadDto> inboxPage(User currentUser, Pageable pageable) {
        return messageRepository.findInboxThreads(currentSchoolId(), currentUser.getId(), pageable)
                .map(message -> toThread(message, currentUser));
    }

    @Transactional(readOnly = true)
    public Page<MessageThreadDto> sentPage(User currentUser, Pageable pageable) {
        return messageRepository.findSentThreads(currentSchoolId(), currentUser.getId(), pageable)
                .map(message -> toThread(message, currentUser));
    }

    @Transactional
    public List<MessageDto> openThread(User currentUser, String threadId) {
        Long schoolId = currentSchoolId();
        List<Message> messages = messageRepository.findAllByThreadIdAndSchoolIdOrderBySentAtAsc(threadId, schoolId);
        if (messages.isEmpty() || messages.stream().noneMatch(message -> isParticipant(message, currentUser))) {
            throw new EntityNotFoundException("Message thread not found");
        }

        Instant readAt = Instant.now();
        messages.stream()
                .filter(message -> message.getRecipient().getId().equals(currentUser.getId()) && message.getReadAt() == null)
                .forEach(message -> message.setReadAt(readAt));
        messageRepository.saveAll(messages);
        return messages.stream().map(MessageDto::new).toList();
    }

    @Transactional
    public MessageDto send(User currentUser, MessageSendDto dto) {
        Long schoolId = currentSchoolId();
        User recipient = userRepository.findById(dto.getRecipientId())
                .filter(user -> belongsToSchool(user, schoolId))
                .filter(user -> user.getStatus() == Status.ACTIVE)
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .filter(user -> canMessage(currentUser, user))
                .orElseThrow(() -> new IllegalArgumentException("You cannot message this user"));

        String threadId = dto.getThreadId();
        if (threadId == null || threadId.isBlank()) {
            threadId = UUID.randomUUID().toString();
        } else {
            List<Message> thread = messageRepository.findAllByThreadIdAndSchoolIdOrderBySentAtAsc(threadId, schoolId);
            if (thread.isEmpty() || thread.stream().noneMatch(message -> isParticipant(message, currentUser))) {
                throw new EntityNotFoundException("Message thread not found");
            }
            User originalParticipant = thread.get(0).getSender().getId().equals(currentUser.getId())
                    ? thread.get(0).getRecipient() : thread.get(0).getSender();
            if (!originalParticipant.getId().equals(recipient.getId())) {
                throw new IllegalArgumentException("Replies must stay within the existing conversation");
            }
        }

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new EntityNotFoundException("School not found"));
        Message message = new Message();
        message.setSchool(school);
        message.setThreadId(threadId);
        message.setSender(currentUser);
        message.setRecipient(recipient);
        message.setSubject(dto.getSubject() == null ? "" : dto.getSubject().trim());
        message.setBody(dto.getBody().trim());
        message.setSentAt(Instant.now());
        message.setCreatedBy(currentUser);
        message.setUpdatedBy(currentUser);
        return new MessageDto(messageRepository.save(message));
    }

    @Transactional
    public MessageDto edit(User currentUser, Long messageId, MessageEditDto dto) {
        Message message = getOwnedMessage(currentUser, messageId);
        if (message.getDeletedAt() != null) {
            throw new IllegalArgumentException("Deleted messages cannot be edited");
        }

        message.setSubject(dto.getSubject() == null ? "" : dto.getSubject().trim());
        message.setBody(dto.getBody().trim());
        message.setEditedAt(Instant.now());
        message.setUpdatedBy(currentUser);
        return new MessageDto(messageRepository.save(message));
    }

    @Transactional
    public void delete(User currentUser, Long messageId) {
        Message message = getOwnedMessage(currentUser, messageId);
        if (message.getDeletedAt() == null) {
            message.setDeletedAt(Instant.now());
            message.setUpdatedBy(currentUser);
            messageRepository.save(message);
        }
    }

    private Message getOwnedMessage(User currentUser, Long messageId) {
        return messageRepository.findByIdAndSchoolId(messageId, currentSchoolId())
                .filter(message -> message.getSender().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));
    }

    private List<MessageThreadDto> summarize(List<Message> messages, User currentUser) {
        Map<String, List<Message>> grouped = new LinkedHashMap<>();
        for (Message message : messages) {
            grouped.computeIfAbsent(message.getThreadId(), ignored -> new ArrayList<>()).add(message);
        }

        return grouped.values().stream().map(thread -> {
            Message latest = thread.get(0);
            User other = latest.getSender().getId().equals(currentUser.getId())
                    ? latest.getRecipient() : latest.getSender();
            boolean unread = thread.stream().anyMatch(message ->
                    message.getRecipient().getId().equals(currentUser.getId()) && message.getReadAt() == null);
            return new MessageThreadDto(latest.getThreadId(), latest.getSubject(), other.getFullName(),
                    other.getId(), latest.getBody(), latest.getSentAt(), unread);
        }).toList();
    }

    private MessageThreadDto toThread(Message latest, User currentUser) {
        User other = latest.getSender().getId().equals(currentUser.getId())
                ? latest.getRecipient() : latest.getSender();
        boolean unread = latest.getRecipient().getId().equals(currentUser.getId())
                && latest.getReadAt() == null;
        return new MessageThreadDto(latest.getThreadId(), latest.getSubject(), other.getFullName(),
                other.getId(), latest.getBody(), latest.getSentAt(), unread);
    }

    private boolean canMessage(User sender, User recipient) {
        if (sender.hasRole(UserRole.STUDENT)) {
            return recipient.hasRole(UserRole.TEACHER)
                    || recipient.hasRole(UserRole.SCHOOL_ADMIN)
                    || recipient.hasRole(UserRole.PRINCIPAL);
        }
        return recipient.hasRole(UserRole.SCHOOL_ADMIN)
                || recipient.hasRole(UserRole.PRINCIPAL)
                || recipient.hasRole(UserRole.TEACHER)
                || recipient.hasRole(UserRole.FINANCE_OFFICER)
                || recipient.hasRole(UserRole.STUDENT);
    }

    private boolean belongsToSchool(User user, Long schoolId) {
        return user.getSchools().stream().anyMatch(school -> school.getId().equals(schoolId));
    }

    private boolean isParticipant(Message message, User user) {
        return message.getSender().getId().equals(user.getId())
                || message.getRecipient().getId().equals(user.getId());
    }

    private Long currentSchoolId() {
        Long schoolId = TenantContext.getSchoolId();
        if (schoolId == null) throw new IllegalStateException("No active school selected");
        return schoolId;
    }
}
