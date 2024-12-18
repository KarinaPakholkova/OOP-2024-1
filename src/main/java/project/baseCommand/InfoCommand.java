package project.baseCommand;

public class InfoCommand extends AbstractCommand{

    public String getDescription(){
        return "Информация о боте";
    }

    public String getMessage(){
        return "Бот-агрегатор новостей — ваш персональный помощник, который собирает самые важные и актуальные события из разных источников." +
                "Он фильтрует информацию по вашим интересам, предлагая свежие статьи, видео и аналитические материалы. Оставайтесь в курсе новостей легко и удобно!";
    }
}
