package project.baseCommand;

public class AuthorsCommand extends AbstractCommand{

    public String getDescription(){
        return "Авторы бота";
    }

    public String getMessage(){
        return "Авторами проекта являются студенты 2-го курса специалитета \"Компьютерная безопасность\" Пахолкова Карина и Марченко Артем";
    }
}
