package battleship.messages;

public class ErrorMessages {
    public enum errMessages{
        LEAVED("Противник покинул игру. Дальнейшая игра невозможна."),
        LOOSE("Игра закончена. Вы проиграли."),
        UNAVAILABLE("Сервер недоступен."),
        UNEXPECTED("Непредвиденная ошибка. Дальнейшая игра невозможна.");
        private String message;
        private errMessages(String message){
            this.message = message;
        }
        public String toString(){
            return message;
        }
    }
}
