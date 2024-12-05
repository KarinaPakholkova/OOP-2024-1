package project.MailingList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import project.API.Api;
import project.Bot;
import project.database.BDForMailingList;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MailingListTest {

    @Mock
    private Bot bot;

    @Mock
    private BDForMailingList dbManager;

    @Mock
    private Api apiCategories;

    private MailingList mailingList;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mailingList = new MailingList(bot);
        mailingList.dbManager = dbManager;
        mailingList.apiCategories = apiCategories;
    }

    @Test
    public void testSendHourlyMessage_Success() throws Exception {
        // Arrange
        List<AbstractMap.SimpleEntry<String, String>> chatIds = Arrays.asList(
                new AbstractMap.SimpleEntry<>("12345", "sports"),
                new AbstractMap.SimpleEntry<>("67890", "news")
        );

        List<AbstractMap.SimpleEntry<String, String>> newsList = Arrays.asList(
                new AbstractMap.SimpleEntry<>("News Title 1", "News Description 1"),
                new AbstractMap.SimpleEntry<>("News Title 2", "News Description 2")
        );

        when(dbManager.selectMailingList()).thenReturn(chatIds);
        when(apiCategories.fetchNewsCategory("sports")).thenReturn(newsList);
        when(apiCategories.fetchNewsCategory("news")).thenReturn(newsList);

        mailingList.sendHourlyMessage();

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot, times(2)).execute(messageCaptor.capture());

        List<SendMessage> messages = messageCaptor.getAllValues();
        assertEquals("Рассылка новостей по категории 'sports':\n1. News Title 1\nNews Description 1\n2. News Title 2\n", messages.get(0).getText());
        assertEquals("Рассылка новостей по категории 'news':\n1. News Title 1\nNews Description 1\n2. News Title 2\n", messages.get(1).getText());
    }

    @Test
    public void testSendHourlyMessage_NoNews() throws Exception {
        List<AbstractMap.SimpleEntry<String, String>> chatIds = Arrays.asList(
                new AbstractMap.SimpleEntry<>("12345", "sports")
        );

        when(dbManager.selectMailingList()).thenReturn(chatIds);
        when(apiCategories.fetchNewsCategory("sports")).thenReturn(Arrays.asList());

        mailingList.sendHourlyMessage();

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot, times(1)).execute(messageCaptor.capture());

        SendMessage message = messageCaptor.getValue();
        assertEquals("К сожалению, не удалось получить новости для категории: sports", message.getText());
    }

    @Test
    public void testSendHourlyMessage_ErrorInFetchingNews() throws Exception {

        List<AbstractMap.SimpleEntry<String, String>> chatIds = Arrays.asList(
                new AbstractMap.SimpleEntry<>("12345", "sports")
        );

        when(dbManager.selectMailingList()).thenReturn(chatIds);
        when(apiCategories.fetchNewsCategory("sports")).thenReturn(
                Arrays.asList(new AbstractMap.SimpleEntry<>("Не удалось получить новости. Статус: 400", ""))
        );

        mailingList.sendHourlyMessage();

        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(bot, times(1)).execute(messageCaptor.capture());

        SendMessage message = messageCaptor.getValue();
        assertEquals("К сожалению, не удалось получить новости для категории: sports", message.getText());
    }
}
