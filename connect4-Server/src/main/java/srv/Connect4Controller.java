package srv;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Connect4Controller {

    private final AtomicLong counter = new AtomicLong();
    private final Map<String, String> name2Ip = new HashMap<>(2);
    private final Map<Integer, String> id2Ip = new HashMap<>(2);
    private final Map<Integer, String> id2Name = new HashMap<>(2);
    private final Map<String, Integer> ip2Id = new HashMap<>(2);
    private static boolean isPlayerOneTurn = false;
    private static boolean isPlayerTwoTurn = false;
    private int[][] loc = new int[6][7];
    private int lastMove = -1;

//    @RequestMapping("/connect")    //http://127.0.0.1:8080/connect/    or http://127.0.0.1:8080/connect?name=Mahdi //TODO: delete it
//    public Connect4 anything(@RequestParam(value="name" ) String name, HttpServletRequest servletRequest) {
//        if (counter.get() >1 ) {
//            return new Connect4(counter.get() , " This game is two player! you should try Play Station 4 instead ;)");
//        }
//        name2Ip.put(name, servletRequest.getRemoteAddr());
//        String msg = counter.get() == 0 ? " player one " : " player two" ;
//        return new Connect4(counter.incrementAndGet(), String.format("Hej " + name +", you are" + msg));
//    }



    @RequestMapping(method = RequestMethod.POST, value = "connect2Server")  // http://127.0.0.1:8080/connect2Server
    public Map<String, String> connectionHandler(@RequestBody Map<String, Object> Url, HttpServletRequest servletRequest) {
        Map<String, String> respon = new HashMap<String, String>();
        String name = Url.get("name").toString();

//        if (name2Ip.containsValue(servletRequest.getRemoteAddr())) { //TODO: enable in the end
//            return new Connect4(counter.get(), "You can't register since your IP has been registered earlier.");
//        }
        if (counter.get() > 1) {
            respon.put("message" , "This game is two player! you should try Play Station 4 instead ;)");
            return  respon;
//            return new Connect4(" This game is two player! you should try Play Station 4 instead ;)");
        }

        name2Ip.put(name, servletRequest.getRemoteAddr());
        id2Ip.put((int) counter.get() + 1, servletRequest.getRemoteAddr());
        ip2Id.put(servletRequest.getRemoteAddr(), (int) counter.get());
        id2Name.put((int) counter.get(),name);
        String msg = counter.get() == 0 ? " player one " : " player two";

        respon.put("ID" , counter.incrementAndGet()+ "" );
        respon.put("message" , " Hej " + name + ", you are " + msg);
        return respon;
//        return new Connect4(counter.incrementAndGet(), "Hej " + name + ", you are " + msg);
    }

    //--------------------------------------------------------------------------------------------------------
    @RequestMapping(method = RequestMethod.GET, value = "getState")  //http://127.0.0.1:8080/getState
    public Map<String, String> getCurrentState() {
        Map<String, String> respon = new HashMap<String, String>();

//        System.out.println("get state ====> " + loc[0][3]);

        if (counter.get() == 0) {
            isPlayerOneTurn = true;
            isPlayerTwoTurn = false;
        } else if (counter.get() == 1) {
            isPlayerTwoTurn = true;
            isPlayerOneTurn = false;
        }



        System.out.println(" THE WINNER IN -----> " + defineWinner(loc));

        if (isPlayerOneTurn) {
            respon.put("message: ", " player one turn.");
            return respon;
        } else if (isPlayerTwoTurn) {
            respon.put("message", " player two turn");
            return respon;
        }
        return null;
    }
    
   //--------------------------------------------------------------------------------------------------------
    @RequestMapping(method = RequestMethod.GET, value = "GetLastTurn")  //http://127.0.0.1:8080/GetLastTurn
    public Map<String, String> GetLastTurn() {
        Map<String, String> respon = new HashMap<String, String>();

        if(lastMove == -1)
        	return null;
        respon.put("column: ", Integer.toString(lastMove));

        return respon;

        
    }
    
  //--------------------------------------------------------------------------------------------------------
    @RequestMapping(method = RequestMethod.GET, value = "GetName")  //http://127.0.0.1:8080/GetName
    public Map<String, String> GetName(HttpServletRequest servletRequest) {
        Map<String, String> respon = new HashMap<String, String>();
        
        Integer id = (Integer)ip2Id.get(servletRequest.getRemoteAddr());
        
        
        
        if(id != null && (id >= 0 && id <= 1))
        {
        	int oponentId = (id==0?1:0);
        	String oponentName = (String)id2Name.get(oponentId);
        	if(oponentName != null)
        	{
        		respon.put("PlayerName: ",oponentName);
        		return respon;
        	}
        	
        }
        
        return null;
        
    }
    
    
    //--------------------------------------------------------------------------------------------------------
    private Map<String, String> defineWinner(int[][] loc) {

        Map<String, String> resp = new HashMap<>();

        outOfLoop:
        {
            for (int y = 0; y <= 5; y++) { // for col0 - col3 ( for first 4 column in the left)
                for (int x = 0; x <= 3; x++) {
                    resp.put("message", whoIsWinnerHoriz(x, y, loc).get("message"));
                   // age message winnero peida karde break kon, vagar na ma be loope dovomi ham niaz darim!

                    break outOfLoop;
                }
            }
            for (int x = 0; x <= 6; x++) { // for row0 - row2 ( for first 3 row in the bottom)
                for (int y = 0; y <= 2; y++) {
                    resp.put("message", whoIsWinnerVert(x, y, loc).get("message"));
                    break outOfLoop;
                }
            }
        }
        return resp;
    }
    //--------------------------------------------------------------------------------------------------------
    private Map<String, String> whoIsWinnerHoriz(int x, int y, int[][] loc) { //x:0-3 , y:0-5
        Map<String, String> resp = new HashMap<>();
        if ((loc[y][x] == 1) && (loc[y][x+1] == 1) && (loc[y][x+2] == 1) && (loc[y][x+3] == 1)){
            resp.put("message" , " player one won :)");
            return resp;
        }
        if ((loc[y][x] == 2) && (loc[y][x+1] == 2) && (loc[y][x+2] == 2) && (loc[y][x+3] == 2)){
            resp.put("message" , " player two won :)");
            return resp;
        }
        resp.put("message" , " no winner in horizontal view");
        return resp;
    }

    //--------------------------------------------------------------------------------------------------------
    private Map<String,String> whoIsWinnerVert(int x, int y, int[][] loc) {
        Map<String, String> resp = new HashMap<>();
        if ((loc[y][x] == 1) && (loc[y+1][x] == 1) && (loc[y+2][x] == 1) && (loc[y+3][x] == 1)){
            System.out.println("AVALI ---------------------------> " + y + ","+ x + "  loc is ==> " + loc[y][x] + " "+loc[y+1][x]+" "+loc[y+2][x]+" " +loc[y+3][x]);
            resp.put("message" , " player one won :)");
            return resp;
        }
        if ((loc[y][x] == 2) && (loc[y+1][x] == 2) && (loc[y+2][x] == 2) && (loc[y+3][x] == 2)){
            System.out.println("DOVOMI ---------------------------> "+ y + ","+ x + "  loc is ==> " + loc[y][x] + " "+loc[y+1][x]+" "+loc[y+2][x]+" " +loc[y+3][x]);
            resp.put("message" , " player two won :)");
            return resp;
        }
        resp.put("message" , " no winner in vertical view ");
        return resp;
    }
    //--------------------------------------------------------------------------------------------------------
    @RequestMapping(method = RequestMethod.POST, value = "enterDisc")  // http://127.0.0.1:8080/connect2Server
    public Map<String, String> enterTheDisk(@RequestBody Map<String, Object> Url, HttpServletRequest servletRequest) {
        int topOFThisColumn;
        Map<String, String> respon = new HashMap<String, String>();
        int column = Integer.valueOf(Url.get("col").toString());

        if (column > 6) {
            throw new IndexOutOfBoundsException(" the column number is not in the range 0-6");
        }

//        if (isPlayerOneTurn) {  //TODO: enable it in th end
//            if (servletRequest.getRemoteAddr().equals(id2Ip.get(2))) {
//                respon.put("message" , "This is not your turn! its player one turn.");
//                return respon;
//            }
//        }
//        if (isPlayerTwoTurn) {
//            if (servletRequest.getRemoteAddr().equals(id2Ip.get(1))) {
//                respon.put("message" , "This is not your turn! its player two turn.");
//                return respon;
//            }
//        }

        System.out.println("col value: ----> " + Url.get("col"));
        System.out.println("loc[0][3] val is: ===> " + loc[0][3]);
        System.out.println("loc[1][3] val is: ===> " + loc[1][3]);
        System.out.println("loc[2][3] val is: ===> " + loc[2][3]);
        System.out.println("loc[3][3] val is: ===> " + loc[3][3]);

        topOFThisColumn = topOfColumn(column);

        if (topOFThisColumn == 10) {
            respon.put("message" , " The column is already full or client is trying to overwrite the location!");
            return  respon;
        }

        
        if (isPlayerOneTurn) {
            loc[topOFThisColumn][column] = 1;
        } else if (isPlayerTwoTurn) {
            loc[topOFThisColumn][column] = 2;
        }

        lastMove = column; 
        return null;
    }
//--------------------------------------------------------------------------------------------------------
    private int topOfColumn(int column) {
        int topIs = 0;  // if the column getting ful (means loc is not ==0 anymore), it will skip bottom for-loop and return 0 !

        for (int i = 0; i <6  ; i++) {
//            System.out.println("==========>  injas" );
            if ( (i == 5) && (loc[i][column] != 0)) { // if the column getting ful or user try to change same location
                return 10;
            }
            if (loc[i][column] == 0) {
                topIs = i;
                break;
            }
        }
        System.out.println("top is: ----> " + "top ine: " + topIs);
        return topIs;
    }
//--------------------------------------------------------------------------------------------------------

}

