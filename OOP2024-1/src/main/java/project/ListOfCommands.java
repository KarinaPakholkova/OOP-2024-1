package project;

import project.commands.*;
import java.util.HashMap;
import java.util.Set;

public class ListOfCommands {
    private final HashMap<String, AbstractCommand> commandHashMap = new HashMap<String, AbstractCommand>();

    public ListOfCommands(){
        commandHashMap.put("/info", new InfoCommand());
        commandHashMap.put("/start", new StartCommand());
        commandHashMap.put("/authors", new AuthorsCommand());
        commandHashMap.put("/help", new HelpCommand(commandHashMap));
    }

    public String findCommand(String text){
        if (commandHashMap.containsKey(text)) {
            commandHashMap.get(text);
            return commandHashMap.get(text).getMessage();
        }
        Set obEntrySet = commandHashMap.entrySet();
        System.out.println(obEntrySet);
        return "Неверная команда";
    }

}
