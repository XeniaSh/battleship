package battleship.forms;


import battleship.messages.ErrorMessages;
import battleship.messages.WinMessages;
import org.openqa.selenium.By;
import webdriver.BaseEntity;
import webdriver.BaseForm;
import webdriver.elements.Button;
import webdriver.elements.TextBox;

public class MainForm extends BaseForm{
    private Button btnChsRndRival = new Button(By.xpath("//a[contains(text(), \"случайный\")]"), "random enemy");
    private Button btnRndSet = new Button(By.xpath("//span[contains(text(), \"Случайным образом\")]"), "random set");
    private int intMaxNumber = 15;
    private Button btnStartGame = new Button(By.xpath("//div[contains(text(), \"Играть\")]"), "start game");
    private TextBox txbNotification = new TextBox(By.xpath("//div[(contains(@class, 'notification__')) and not(contains(@class,'none'))]"), "active notification");
    private TextBox txbNotifText = new TextBox(By.xpath("//div[(contains(@class, 'notification__')) and not(contains(@class,'none'))]//div[contains(text(), \"%s\")]"));
    private String myCourse = "аш ход";
    private Button btnCell = new Button(By.xpath("//div[contains(@class, 'rival')]//div[@data-y=%s and @data-x=%s]"));
    private int size = 10;
    private String classAttr = "class";
    private String empty = "empty";
    private String miss = "miss";
    private String hit = "hit";
    private String done = "done";
    private Button btnCellCont = new Button(By.xpath("//div[contains(@class, 'rival')]//div[@data-y=%s and @data-x=%s]/.."));


    public void game(){
        clickBtn(btnChsRndRival);
        for (int i = 0; i < BaseEntity.rndNumber(intMaxNumber); i++) {
            clickBtn(btnRndSet);
        }
        clickBtn(btnStartGame);

        while(!endGame()){
            play();
            if (endGame()) break;
        }

        if(looseGame()) {
            logger.fatal(txbNotification.getText());
            formatLogMsg(txbNotification.getText());
        }
    }

    public MainForm(){
        super(By.xpath("//a"), "main form");
    }


    /**
     * Checks if the game is going on: if the notification contains end game messages
     * @return true if the game ends
     */
    private boolean endGame(){
        String text= txbNotification.getText();
        if (text.contains(WinMessages.winMessages.WIN.toString())
                || text.equals(ErrorMessages.errMessages.LEAVED.toString())
                || text.equals(ErrorMessages.errMessages.LOOSE.toString())
                || text.equals(ErrorMessages.errMessages.UNAVAILABLE.toString())
                || text.equals(ErrorMessages.errMessages.UNEXPECTED.toString())){
            return true;
        }
        return false;
    }

    /**
     * Checks if the game is loosed: if the notification contains error or loose game messages
     * @return true if the game ends
     */
    private boolean looseGame(){
        String text= txbNotification.getText();
        if (text.equals(ErrorMessages.errMessages.LEAVED.toString()) || text.equals(ErrorMessages.errMessages.LOOSE.toString())
                || text.equals(ErrorMessages.errMessages.UNAVAILABLE.toString()) || text.equals(ErrorMessages.errMessages.UNEXPECTED.toString())){
            return true;
        }
        return false;
    }

    /**
     * Plays the game according to the algorithm: fills diagonals
     */
    public void play(){
        fillDiag(6, -6);
        fillDiag(2, -2);
        fillDiag(0, 6);
        fillDiag(0, 2);
        fillDiag(0, 0);
        fillDiag(4, -4);
        fillDiag(0, 4);
        fillDiag(8, -8);
        fillDiag(0, 8);
        fillOthers();
    }

    /**
     * Fills diagona cells
     * @param coordX x coordinate
     * @param differ y coordinate
     */
    private void fillDiag(int coordX, int differ) {
        for (int i = coordX; i < ( coordX + size - Math.abs(differ)); i++) {
            int j = i + differ;
            Button btn = getBtn(i,j);
            String status = getBtnAttr(i,j);
            if (status.contains(empty)) {
                clickCell(btn);
            }
            status = getBtnAttr(i,j);
            if (status.contains(hit)) {
                fillAround(i, j);
            }
        }
    }

    /**
     * Fills cells around the hit one
     * @param x x coordinate
     * @param y y coordinate
     */
    private void fillAround(int x, int y) {
        if (!endGame()) {
            if (nearCellsContain(x, y, hit)) {
                if (nearCellsInLineContainX(x, y, hit)) {
                    fillLineX(x, y);
                } else if (nearCellsInLineContainY(x, y, hit)) {
                    fillLineY(x, y);
                }
            } else if (!nearCellsAllAre(x, y, hit)) {
                if ((x < size - 1) && (getBtnAttr(x + 1, y).contains(empty))) {
                    clickCell(getBtn(x + 1, y));
                    fillAround(x, y);
                } else if ((x > 0) && (getBtnAttr(x - 1, y).contains(empty))) {
                    clickCell(getBtn(x - 1, y));
                    fillAround(x, y);
                } else if ((y < size - 1) && (getBtnAttr(x, y + 1).contains(empty))) {
                    clickCell(getBtn(x, y + 1));
                    fillAround(x, y);
                } else if ((y > 0) && (getBtnAttr(x, y - 1).contains(empty))) {
                    clickCell(getBtn(x, y - 1));
                    fillAround(x, y);
                }
            }
        }
    }

    /**
     * Fills cells around the hit one on x coordinate
     * @param x x coordinate
     * @param y y coordinate
     */
    private void fillLineX(int x, int y) {
        int n = 0;
        while (!getBtnAttr(x, y).contains(done)) {
            n = 1;
            if (endGame()) break;
            while (x >= 0) {
                if (endGame()) break;
                if (getBtnAttr(x - n, y).contains(hit)) {
                    n++;
                } else if (getBtnAttr(x - n, y).contains(empty)) {
                    clickCell(getBtn(x - n, y));
                } else if (getBtnAttr(x - n, y).contains(miss)){
                    break;
                }
                if (getBtnAttr(x, y).contains(done)) break;
            }
            n = 1;
            while (x < size){
                if (endGame()) break;
                if (getBtnAttr(x + n, y).contains(hit)) {
                    n++;
                } else if (getBtnAttr(x + n, y).contains(empty)) {
                    clickCell(getBtn(x + n, y));
                }
                if (getBtnAttr(x, y).contains(done)) break;
            }
        }
    }

    /**
     * Fills cells around the hit one on y coordinate
     * @param x x coordinate
     * @param y y coordinate
     */
    private void fillLineY(int x, int y) {
        int n = 0;
        while (!getBtnAttr(x, y).contains(done)) {
            n = 1;
            if (endGame()) break;
            while (y >= 0) {
                if (endGame()) break;
                if (getBtnAttr(x, y - n).contains(hit)) {
                    n++;
                } else if (getBtnAttr(x, y - n).contains(empty)) {
                    clickCell(getBtn(x, y - n));
                } else if (getBtnAttr(x, y - n).contains(miss)){
                    break;
                }
                if (getBtnAttr(x, y).contains(done)) break;
            }
            n = 1;
            while (y < size){
                if (endGame()) break;
                if (getBtnAttr(x, y + n).contains(hit)) {
                    n++;
                } else if (getBtnAttr(x, y + n).contains(empty)) {
                    clickCell( getBtn(x, y + n));
                }
                if (getBtnAttr(x, y).contains(done)) break;
            }
        }
    }

    /**
     * Fills remaining empty cells
     */
    private void fillOthers(){
        int i = -1;
        int j = -1;
        while(!endGame()) {
            i++;
            while (!endGame()){
                j++;
                String status = getBtnAttr(i, j);
                if (status.contains(empty)) {
                    clickCell(getBtn(i, j));
                }
            }
        }
    }

    /**
     * If cells around the cell are in status
     * @param x x coordinate
     * @param y y coordinate
     * @param status status of the cell
     * @return true if at least one is in status
     */
    private boolean nearCellsContain(int x, int y, String status){
        boolean is = false;
        if((x > 0) && (x < size-1) && (y > 0) && (y < size-1)){
            if(getBtnAttr(x + 1, y).contains(status) || getBtnAttr(x - 1, y).contains(status) ||
                    getBtnAttr(x, y + 1).contains(status) || getBtnAttr(x, y-  1).contains(status) ){
                is = true;
            }
        } else if ((x == 0)&& (y > 0) && (y < size-1)){
            if(getBtnAttr(x + 1, y).contains(status) ||
                    getBtnAttr(x, y + 1).contains(status) || getBtnAttr(x, y - 1).contains(status) ){
                is = true;
            }
        } else if ((x == size-1)&& (y > 0) && (y < size-1)) {
            if (getBtnAttr(x - 1, y).contains(status) ||
                    getBtnAttr(x, y + 1).contains(status) || getBtnAttr(x, y - 1).contains(status)) {
                is = true;
            }
        } else if ((y == 0)&& (x > 0) && (x < size-1)){
            if(getBtnAttr(x + 1, y).contains(status) || getBtnAttr(x - 1, y).contains(status) ||
                    getBtnAttr(x, y + 1).contains(status) ){
                is = true;
            }
        } else if ((y == size-1)&& (x > 0) && (x < size-1)) {
            if(getBtnAttr(x + 1, y).contains(status) || getBtnAttr(x - 1, y).contains(status) ||
                     getBtnAttr(x, y-1).contains(status) ){
                is = true;
            }
        } else if((x == 0) && (y == 0)){
            if(getBtnAttr(x + 1, y).contains(status) || getBtnAttr(x, y+1).contains(status) ){
                is = true;
            }
        } else if((x == 0) && (y == size-1)){
             if(getBtnAttr(x + 1, y).contains(status) || getBtnAttr(x, y-1).contains(status) ){
                 is = true;
             }
        } else if((x == size-1) && (y == 0)){
            if(getBtnAttr(x - 1, y).contains(status) || getBtnAttr(x, y+1).contains(status) ){
                is = true;
            }
        } else if((x == size-1) && (y == size-1)){
            if(getBtnAttr(x - 1, y).contains(status) || getBtnAttr(x, y-1).contains(status) ){
                is = true;
            }
        }
        return is;
    }

    /**
     * If cells near the cell in x coordinate are in status
     * @param x x coordinate
     * @param y y coordinate
     * @param status status of the cell
     * @return true if at least one is in status
     */
    private boolean nearCellsInLineContainX(int x, int y, String status){
        boolean is = false;
        if((x > 0) && (x < size-1)){
            if(getBtnAttr(x + 1, y).contains(status) || getBtnAttr(x - 1, y).contains(status)){
                is = true;
            }
        } else if ((x == 0)){
            if(getBtnAttr(x + 1, y).contains(status) ){
                is = true;
            }
        } else if ((x == size-1)) {
            if (getBtnAttr(x - 1, y).contains(status)) {
                is = true;
            }
        }
        return is;
    }

    /**
     * If cells near the cell in y coordinate are in status
     * @param x x coordinate
     * @param y y coordinate
     * @param status status of the cell
     * @return true if at least one is in status
     */
    private boolean nearCellsInLineContainY(int x, int y, String status){
        boolean is = false;
        if((y > 0) && (y < size-1)){
            if(getBtnAttr(x, y + 1).contains(status) || getBtnAttr(x, y - 1).contains(status)){
                is = true;
            }
        } else if ((y == 0)){
            if(getBtnAttr(x, y + 1).contains(status) ){
                is = true;
            }
        } else if ((y == size - 1)) {
            if (getBtnAttr(x , y - 1).contains(status)) {
                is = true;
            }
        }
        return is;
    }

    /**
     * If all cells near the cell are in status
     * @param x x coordinate
     * @param y y coordinate
     * @param status status of the cell
     * @return true if all near cells are in status
     */
    private boolean nearCellsAllAre(int x, int y, String status){
        boolean is = false;
        if((x > 0) && (x < size-1) && (y > 0) && (y < size-1)){
            if(getBtnAttr(x + 1, y).contains(status) && getBtnAttr(x - 1, y).contains(status) &&
                    getBtnAttr(x, y+1).contains(status) && getBtnAttr(x, y-1).contains(status) ){
                is = true;
            }
        } else if ((x == 0)&& (y > 0) && (y < size-1)){
            if(getBtnAttr(x + 1, y).contains(status) &&
                    getBtnAttr(x, y+1).contains(status) && getBtnAttr(x, y-1).contains(status) ){
                is = true;
            }
        } else if ((x == size-1)&& (y > 0) && (y < size-1)) {
            if (getBtnAttr(x - 1, y).contains(status) &&
                    getBtnAttr(x, y + 1).contains(status) && getBtnAttr(x, y - 1).contains(status)) {
                is = true;
            }
        } else if ((y == 0)&& (x > 0) && (x < size-1)){
            if(getBtnAttr(x + 1, y).contains(status) && getBtnAttr(x - 1, y).contains(status) &&
                    getBtnAttr(x, y+1).contains(status) ){
                is = true;
            }
        } else if ((y == size-1)&& (x > 0) && (x < size-1)) {
            if(getBtnAttr(x + 1, y).contains(status) && getBtnAttr(x - 1, y).contains(status) &&
                    getBtnAttr(x, y-1).contains(status) ){
                is = true;
            }
        } else if((x == 0) && (y == 0)){
            if(getBtnAttr(x + 1, y).contains(status) && getBtnAttr(x, y+1).contains(status) ){
                is = true;
            }
        } else if((x == 0) && (y == size-1)){
            if(getBtnAttr(x + 1, y).contains(status) && getBtnAttr(x, y-1).contains(status) ){
                is = true;
            }
        } else if((x == size-1) && (y == 0)){
            if(getBtnAttr(x - 1, y).contains(status) && getBtnAttr(x, y+1).contains(status) ){
                is = true;
            }
        } else if((x == size-1) && (y == size-1)){
            if(getBtnAttr(x - 1, y).contains(status) && getBtnAttr(x, y-1).contains(status) ){
                is = true;
            }
        }
        return is;
    }

    /**
     * Returns button with coordinates (i, j)
     * @param i x coordinate
     * @param j y coordinate
     * @return button
     */
    private Button getBtn(int i, int j){
        String loc = String.format(btnCell.getLocator().toString(), i, j);
        return new Button(loc, "btn");
    }

    /**
     * Returns button's with coordinates (i, j) attribute
     * @param i x coordinate
     * @param j y coordinate
     * @return attribute
     */
    private String getBtnAttr(int i, int j){
        String locCont = String.format(btnCellCont.getLocator().toString(), i, j);
        Button btnCont = new Button(locCont, "btn container");
        return btnCont.getAttribute(classAttr);
    }

    /**
     * Checks if the game is going on, waits for the course and clicks on the button
     * @param btn button
     */
    public void clickCell(Button btn){
        if (!endGame()){
            txbNotifText.waitUntilText(myCourse);
            btn.click();
        }
    }

    /**
     * If the game is going on clicks the button
     * @param btn button
     */
    public void clickBtn(Button btn){
        if(!endGame()){
            btn.click();
        }
    }
}
