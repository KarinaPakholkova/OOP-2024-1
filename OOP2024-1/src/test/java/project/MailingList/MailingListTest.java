package project.MailingList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import project.Bot;
import project.auxiliaryFunctions.CreateString;
import project.database.BDForMailingList;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MailingListTest {

    @Mock
    private Bot bot;

    @Mock
    private BDForMailingList dbManager;

    @Mock
    private CreateString categoryObj;

    private MailingList mailingList;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mailingList = new MailingList(bot);
        mailingList.dbManager = dbManager;
        mailingList.categoryObj = categoryObj;
    }

    @Test
    public void testSendHourlyMessage_Success() throws Exception {
        List<AbstractMap.SimpleEntry<String, String>> chatIds = Arrays.asList(
                new AbstractMap.SimpleEntry<>("12345", "sports"),
                new AbstractMap.SimpleEntry<>("67890", "general")
        );

        when(dbManager.selectMailingList()).thenReturn(chatIds);
        when(categoryObj.printCategoryNews("sports")).thenReturn(new StringBuilder("Sports News Content"));
        when(categoryObj.printCategoryNews("general")).thenReturn(new StringBuilder("General News Content"));

        mailingList.sendHourlyMessage();

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot, times(2)).execute(messageCaptor.capture());

        List<SendMessage> messages = messageCaptor.getAllValues();
        assertEquals("Sports News Content", messages.get(0).getText());
        assertEquals("General News Content", messages.get(1).getText());
    }

    @Test
    public void testSendHourlyMessage_NoNews() throws Exception {
        List<AbstractMap.SimpleEntry<String, String>> chatIds = List.of(
                new AbstractMap.SimpleEntry<>("12345", "sports")
        );

        when(dbManager.selectMailingList()).thenReturn(chatIds);
        when(categoryObj.printCategoryNews("sports")).thenReturn(new StringBuilder());

        mailingList.sendHourlyMessage();

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot, times(1)).execute(messageCaptor.capture());

        SendMessage message = messageCaptor.getValue();
        assertEquals("К сожалению, не удалось получить новости для категории: sports", message.getText());
    }

    @Test
    public void testSendHourlyMessage_ErrorInFetchingNews() throws Exception {
        List<AbstractMap.SimpleEntry<String, String>> chatIds = List.of(
                new AbstractMap.SimpleEntry<>("12345", "sports")
        );

        when(dbManager.selectMailingList()).thenReturn(chatIds);
        when(categoryObj.printCategoryNews("sports")).thenReturn(new StringBuilder("Не удалось получить новости. Статус: 400"));

        mailingList.sendHourlyMessage();

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot, times(1)).execute(messageCaptor.capture());

        SendMessage message = messageCaptor.getValue();
        assertEquals("К сожалению, не удалось получить новости для категории: sports", message.getText());
    }
}