import context.ContextState;
import fontend.DemoDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Launcher {

    public static void main(String[] args) {
        Launcher launcher = new Launcher();
    }

    public Launcher(){
        DemoDriver driver = new DemoDriver();
        driver.login();
        if(ContextState.isIsLoggedin()){
            driver.main();
        }else{
            driver.login();
        }
    }
}
