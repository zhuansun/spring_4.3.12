import com.zspc.core.spring.ext.MainConfig;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhuansunpengcheng
 * @create 2019-07-10 2:48 PM
 **/
public class ExtTest {


    @Test
    public void test(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);

        applicationContext.publishEvent(new ContextClosedEvent(applicationContext));
        applicationContext.close();
    }

    @Test
    public void test2(){

        String a1 = "123,45,23";


        String b1 = "123,45,12";


        System.out.println(compareBeforeAndCurrent(a1,b1));


    }


    private String compareBeforeAndCurrent(String before, String current){
        List<String> be = new ArrayList<>();
        be.addAll(Arrays.asList(before.split(",")));
        List<String> cu =  new ArrayList<>();
        cu.addAll(Arrays.asList(current.split(",")));
        be.removeAll(cu);
        return be.toString();
    }

}
