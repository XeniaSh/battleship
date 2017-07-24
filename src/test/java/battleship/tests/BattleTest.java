package battleship.tests;


import battleship.forms.MainForm;
import org.testng.Assert;
import webdriver.BaseTest;

public class BattleTest extends BaseTest{
    public void runTest(){
        logger.step(1);
        MainForm mainForm = new MainForm();
        mainForm.game();
    }
}
