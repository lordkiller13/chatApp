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

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ChatlogsController mockChatlogsController;

    @Test
    public void applicationRunningCheck_HappyCase() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Chat Application Running!")));
    }

    @Test
    public void createChatlog_HappyCase() throws Exception {
        String dummyMessage = "dummyMessage";

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dummyMessage);

        mvc.perform(MockMvcRequestBuilders.post("/chatlogs/dummyUser/").accept(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isCreated());

        String actualMessageId =  mockChatlogsController.createChatlog("dummyUser", dummyMessage);
        assertTrue(actualMessageId.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"));
    }

    @Test
    public void getChatlogsForUser_HappyCase() throws Exception {
        String dummyMessage = "dummyMessage";
        String requestJson = "{\n" +
                "\t\"limit\": 10,\n" +
                "\t\"start\": \"dummyStart\"\n" +
                "}";

        mockChatlogsController.createChatlog("dummyUser", dummyMessage);

        mvc.perform(MockMvcRequestBuilders.get("/chatlogs/dummyUser/10/dummy").accept(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk());

        List<Chatlog> actualChatlogsForUser =  mockChatlogsController.getChatlogsForUser("dummyUser", 10, "dummyStart");

        assertEquals(actualChatlogsForUser.get(0).getMessage(), "dummyMessage");
    }
}