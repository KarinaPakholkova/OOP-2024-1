package project.commands;

public class StartCommand extends AbstractCommand{

    public String getDescription(){
        return "Перезапуск бота";
    }

    public String getMessage(){
        return "Приветствую, это бот агрегатор новостей. Напиши /info, чтобы получить больше информации";
    }
}
