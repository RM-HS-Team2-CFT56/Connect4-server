package srv;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Connect4Controller {

    private final AtomicLong counter = new AtomicLong();
    private final Map<String, String> name2Ip = new HashMap<>(2);

    @RequestMapping("/connect")    //http://127.0.0.1:8080/connect/    or http://127.0.0.1:8080/connect?name=Mahdi
    public Connect4 anything(@RequestParam(value="name" ) String name, HttpServletRequest servletRequest) {
        if (counter.get() >1 ) {
            return new Connect4(counter.get() , " This game is two player! you should try Play Station 4 instead ;)");
        }
        name2Ip.put(name, servletRequest.getRemoteAddr());
        String msg = counter.get() == 0 ? " player one " : " player two" ;
        return new Connect4(counter.incrementAndGet(), String.format("Hej " + name +", you are" + msg));
    }

    @RequestMapping(method = RequestMethod.POST, value= "myupdate")  // http://127.0.0.1:8080/myupdate
    public Map<String, String> uploadMultimedia(@RequestBody Map<String, Object> Url){
        Map<String, String> respon = new HashMap<String, String>();
        System.out.println( " ============> " + Url.get("servername").toString());
        respon.put("message" , " finishes!! your id is " + counter.get());
        return respon;
    }



}
