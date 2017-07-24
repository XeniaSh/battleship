package battleship.messages;


public class WinMessages {
        public enum winMessages{
            WIN("Игра закончена. Поздравляем, вы победили!");

            private String message;
            private winMessages(String message){
                this.message = message;
            }
            public String toString(){
                return message;
            }
        }
    }
