package project.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import project.database.BDForMailingList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DeleteMailingListActionTest {
    private DeleteMailingListAction deleteMailingListAction;
    private BDForMailingList dbManager;

    @BeforeEach
    public void setUp() {
        dbManager = mock(BDForMailingList.class);
        deleteMailingListAction = new DeleteMailingListAction();
        deleteMailingListAction.dbManager = dbManager; // инжектим бд
    }

    @Test
    public void testHandle() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(123456L);

        BotApiMethod response = deleteMailingListAction.handle(update);

        assertEquals(SendMessage.class, response.getClass());
        SendMessage sendMessage = (SendMessage) response;
        assertEquals("123456", sendMessage.getChatId());
        assertEquals("Выберите категорию, рассылку для которой хотите удалить: business, entertainment, general, health, science, sports, technology", sendMessage.getText());
    }

    @Test
    public void testCallback_Success() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(123456789L);
        when(message.getText()).thenReturn("business");
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(987654321L);

        BotApiMethod response = deleteMailingListAction.callback(update);

        verify(dbManager, times(1)).deleteUser(987654321L, "business");
        assertEquals(SendMessage.class, response.getClass());
        SendMessage sendMessage = (SendMessage) response;
        assertEquals("123456789", sendMessage.getChatId());
        assertEquals("Рассылка успешно удалена", sendMessage.getText());
    }

    @Test
    public void testCallback_CategoryNotFound() {
        // Arrange
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class); // Mock the User object
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(123456789L);
        when(message.getText()).thenReturn("invalid_category");
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(987654321L);

        BotApiMethod response = deleteMailingListAction.callback(update);

        verify(dbManager, never()).deleteUser(anyLong(), anyString());
        assertEquals(SendMessage.class, response.getClass());
        SendMessage sendMessage = (SendMessage) response;
        assertEquals("123456789", sendMessage.getChatId());
        assertEquals("Такой категории не существует.\n" +
                "Существующие категории: business, entertainment, general, health, science, sports, technology", sendMessage.getText());
    }
}
