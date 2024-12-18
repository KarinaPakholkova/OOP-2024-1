package project.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import project.database.BDForMailingList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AddMailingListActionTest {

    private AddMailingListAction action;
    private BDForMailingList dbManagerMock;
    private Update updateMock;
    private Message messageMock;

    @BeforeEach
    public void setUp() {
        action = new AddMailingListAction();
        dbManagerMock = Mockito.mock(BDForMailingList.class);
        action.dbManager = dbManagerMock; // Заменяем реальный dbManager на мок

        updateMock = Mockito.mock(Update.class);
        messageMock = Mockito.mock(Message.class);
    }

    @Test
    public void testHandle() {
        when(messageMock.getChatId()).thenReturn(12345L);
        when(updateMock.getMessage()).thenReturn(messageMock);

        SendMessage response = (SendMessage) action.handle(updateMock);

        assertEquals("12345", response.getChatId());
        assertEquals("Выберите категорию для рассылки: business, entertainment, general, health, science, sports, technology", response.getText());
    }

    @Test
    public void testCallbackWithValidCategory() {
        when(messageMock.getChatId()).thenReturn(12345L);
        when(messageMock.getText()).thenReturn("business");
        when(messageMock.getFrom()).thenReturn(Mockito.mock(org.telegram.telegrambots.meta.api.objects.User.class));
        when(updateMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getFrom().getId()).thenReturn(1L);

        SendMessage response = (SendMessage) action.callback(updateMock);

        verify(dbManagerMock).insertUser(1L, "business");
        assertEquals("12345", response.getChatId());
        assertEquals("Рассылка успешно подключена", response.getText());
    }

    @Test
    public void testCallbackWithInvalidCategory() {
        when(messageMock.getChatId()).thenReturn(12345L);
        when(messageMock.getText()).thenReturn("invalid_category");
        when(messageMock.getFrom()).thenReturn(Mockito.mock(org.telegram.telegrambots.meta.api.objects.User.class));
        when(updateMock.getMessage()).thenReturn(messageMock);
        when(messageMock.getFrom().getId()).thenReturn(1L);

        SendMessage response = (SendMessage) action.callback(updateMock);

        verify(dbManagerMock, never()).insertUser(anyLong(), anyString());
        assertEquals("12345", response.getChatId());
        assertEquals("Такой категории не существует.\n" +
                "Существующие категории: business, entertainment, general, health, science, sports, technology", response.getText());
    }
}
