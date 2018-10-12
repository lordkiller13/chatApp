package activity;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pojo.Chatlog;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ChatlogsControllerTest {

    private final String dummyMessage = "dummyMessage";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ChatlogsController chatlogsController;

    @Test
    public void applicationRunningCheck_HappyCase_Test() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Chat Application Running!")));
    }

    @Test
    public void createChatlog_HappyCase_Test() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dummyMessage);

        mvc.perform(MockMvcRequestBuilders.post("/chatlogs/dummyUser/").accept(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isCreated());

        Long actualMessageId =  chatlogsController.createChatlog("dummyUser", dummyMessage);

        assertEquals((Long) 2L, actualMessageId);
    }

    @Test
    public void getChatlogsForUser_HappyCase_Test() throws Exception {

        chatlogsController.createChatlog("dummyUser", dummyMessage);

        mvc.perform(MockMvcRequestBuilders.get("/chatlogs/dummyUser/10/WrongMessageId").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        List<Chatlog> actualChatlogsForUser =  chatlogsController.getChatlogsForUser("dummyUser", 10, 2L);
        assertEquals(actualChatlogsForUser.get(0).getMessage(), "dummyMessage");
    }

    @Test
    public void deleteChatLogsForUser_HappyCase_Test() throws Exception {

        chatlogsController.createChatlog("dummyUser", dummyMessage);

        mvc.perform(MockMvcRequestBuilders.delete("/chatlogs/dummyUser").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(chatlogsController.getChatlogsForUser("dummyUser", 10, 1L).size(), 0);
    }

    @Test
    public void deleteChatLogForUser_HappyCase_Test() throws Exception {

        chatlogsController.createChatlog("dummyUser", dummyMessage);

        mvc.perform(MockMvcRequestBuilders.delete("/chatlogs/dummyUser/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(chatlogsController.getChatlogsForUser("dummyUser", 10, 1L).size(), 0);
    }
}