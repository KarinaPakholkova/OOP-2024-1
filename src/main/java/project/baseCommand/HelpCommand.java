package project.baseCommand;

import java.util.HashMap;
import java.util.Map;

public class HelpCommand extends AbstractCommand {

    private final HashMap<String, AbstractCommand> actionCommands;

    public HelpCommand(HashMap<String, AbstractCommand> actionCommands) {
        this.actionCommands = actionCommands;
    }

    public String getDescription() {
        return "Список команд";
    }

    public String getMessage() {
        StringBuilder msg = new StringBuilder("Вот список доступных мне команд:\n");
        for (Map.Entry<String, AbstractCommand> entry : actionCommands.entrySet()) {
            String commandText = entry.getKey();
            String description = entry.getValue().getDescription();
            msg.append(commandText).append(" - ").append(description).append("\n");
        }
        msg.append("/latestnews - Показывает последние пять новостей\n");
        msg.append("/mylikednews - Показывает список сохраненных новостей\n");
        msg.append("/category - показывает 5 популярных новостей выбранной категории\n");
        msg.append("/addmailinglist - создает рассылку по выбранной категории\n");
        msg.append("/deletemailinglist - удаляет рассылку по выбранной категории\n");

        return msg.toString();
    }
}
