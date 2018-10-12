package activity;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pojo.Chatlog;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Umber Kapur.
 */

@RestController
public class ChatlogsController {

    private final AtomicLong messageId = new AtomicLong();
    private final Map<String, List<Chatlog>> userChatLogs = new HashMap<>();

    @RequestMapping("/")
    public String applicationCheck() {
        return "Chat Application Running!";
    }

    @PostMapping("/chatlogs/{user}/")
    @ResponseStatus(HttpStatus.CREATED)
    public Long createChatlog(@PathVariable("user") final String user, @RequestBody final String message) {

        final Long messageIdForCurrentChatLog = messageId.incrementAndGet();
        final Chatlog chatlogForUser = new Chatlog(message, new Date().getTime(), true, messageIdForCurrentChatLog);

        if (userChatLogs.get(user) == null) {
            final List<Chatlog> chatLogList = new LinkedList<>();
            chatLogList.add(chatlogForUser);
            userChatLogs.put(user, chatLogList);
        } else {
            final List<Chatlog> chatLogList = userChatLogs.get(user);
            chatLogList.add(chatlogForUser);
            userChatLogs.put(user, chatLogList);
        }
        return messageIdForCurrentChatLog;
    }

    @GetMapping("/chatlogs/{user}/{start}")
    public List<Chatlog> getChatlogsForUser(@PathVariable("user") final String user, @PathVariable("start") final Long start,
                                            @RequestBody Integer limit) {
        if (limit == null) {
            limit = 10;
        }

        final List<Chatlog> chatlogsList = userChatLogs.get(user);

        Collections.reverse(chatlogsList);

        final int startIndex = Collections.binarySearch(chatlogsList,
                new Chatlog(null, null, false, start),
                Comparator.comparing(Chatlog::getMessageId));

        for (int i = 0; i < chatlogsList.size(); ++i) {
            chatlogsList.get(i).setIsSent(false);   // changing isSent as Server is sending message to the user
        }

        return chatlogsList.subList(Math.max(0, startIndex), Math.min(chatlogsList.size(), limit));
    }

    @DeleteMapping("/chatlogs/{user}")
    public void deleteAllChatlogsForUser(@PathVariable("user") final String user) {
        userChatLogs.put(user, new LinkedList<>());
    }

    @DeleteMapping("/chatlogs/{user}/{msgid}")
    public void deleteChatlogForUser(@PathVariable("user") final String user, @PathVariable("msgid") final Long msgid)
            throws IllegalArgumentException {
        final List<Chatlog> chatlogsList = userChatLogs.get(user);
        final int startIndex = Collections.binarySearch(chatlogsList,
                new Chatlog(null, null, true, msgid),
                Comparator.comparing(Chatlog::getMessageId));
        if (startIndex == -1) {
            throw new IllegalArgumentException();
        }
        chatlogsList.remove(startIndex);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Message Id Not Found")
    @ExceptionHandler(IllegalArgumentException.class)
    public void badMsgIdException() {
        // nothing to do
    }
}