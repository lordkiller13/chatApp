package activity;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pojo.Chatlog;

import java.util.*;

/**
 * @author Umber Kapur.
 */

@RestController
public class ChatlogsController {

    private final Map<String, List<Chatlog>> userChatLogs = new HashMap<>();

    @RequestMapping("/")
    public String applicationCheck() {
        return "Chat Application Running!";
    }

    @PostMapping("/chatlogs/{user}/")
    @ResponseStatus(HttpStatus.CREATED)
    public String createChatlog(@PathVariable("user") final String user, @RequestBody final String message) {
        final String messageId = UUID.randomUUID().toString();
        final Chatlog chatlogForUser = new Chatlog(message, new Date().getTime(), true, messageId);

        if (userChatLogs.get(user) == null) {
            final List<Chatlog> chatLogList = new LinkedList<>();
            chatLogList.add(chatlogForUser);
            userChatLogs.put(user, chatLogList);
        } else {
            final List<Chatlog> chatLogList = userChatLogs.get(user);
            chatLogList.add(chatlogForUser);
            userChatLogs.put(user, chatLogList);
        }
        return messageId;
    }

    @GetMapping("/chatlogs/{user}/{limit}/{start}")
    public List<Chatlog> getChatlogsForUser(@PathVariable("user") final String user, @PathVariable final int limit,
                                            @PathVariable final String start) {

        final List<Chatlog> chatlogsList = userChatLogs.get(user);

        Collections.reverse(chatlogsList);

        for (int i = 0; i < chatlogsList.size(); ++i) {
            chatlogsList.get(i).setIsSent(false);   // changing isSent as Server is sending message to the user
        }

        final int startIndex = Collections.binarySearch(chatlogsList,
                new Chatlog(null, null, true, start),
                Comparator.comparing(Chatlog::getMessageId));
        System.out.println(chatlogsList.subList(Math.max(0, startIndex), Math.min(chatlogsList.size(), limit)));
        return chatlogsList.subList(Math.max(0, startIndex), Math.min(chatlogsList.size(), limit));
    }

    @DeleteMapping("/chatlogs/{user}")
    public void deleteAllChatlogsForUser(@PathVariable("user") final String user) {
        userChatLogs.put(user, new LinkedList<>());
    }

    @DeleteMapping("/chatlogs/{user}/{msgid}")
    public void deleteChatlogForUser(@PathVariable("user") final String user, @PathVariable("msgid") final String msgid)
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