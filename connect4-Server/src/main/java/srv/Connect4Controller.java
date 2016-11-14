package srv;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.json.simple.*;

@RestController
public class Connect4Controller {

    private final AtomicLong counter = new AtomicLong();
    private final Map<String, String> name2Ip = new HashMap<>(2);
    private final Map<Integer, String> id2Ip = new HashMap<>(2);
    private final Map<Integer, String> id2Name = new HashMap<>(2);
    private final Map<String, Integer> ip2Id = new HashMap<>(2);
    private static boolean isPlayerOneTurn;
    private static boolean isPlayerTwoTurn;
    private int[][] loc = new int[6][7];
    private int lastMove = -1;
    //--------------------------------------------------------------------------------------------------------


    @RequestMapping(method = RequestMethod.POST, value = "Connect")  // http://127.0.0.1:8080/connect2Server
    public Map<String, Object> connectionHandler(@RequestBody Map<String, Object> Url, HttpServletRequest servletRequest) {
        Map<String, Object> respon = new HashMap<String, Object>();
        String name = Url.get("PlayerName").toString();

        if (name2Ip.containsValue(servletRequest.getRemoteAddr())) { //TODO: enable in the end
            respon.put("message", "You can't register since your IP has been registered earlier.");
            return respon;
        }

        if (counter.get() > 1) {
            respon.put("message", "This game is two player! you should try Play Station 4 instead ;)");
            return respon;
        }

        if (counter.get() == 0) {
            isPlayerOneTurn = false;
            isPlayerTwoTurn = true;
        } else if (counter.get() == 1) {
            isPlayerTwoTurn = false;
            isPlayerOneTurn = true;
        }

        name2Ip.put(name, servletRequest.getRemoteAddr());
        id2Ip.put((int) counter.get() + 1, servletRequest.getRemoteAddr());
        ip2Id.put(servletRequest.getRemoteAddr(), (int) counter.get());
        id2Name.put((int) counter.get(), name);
        String msg = counter.get() == 0 ? " player one " : " player two";


        respon.put("ID", counter.incrementAndGet());
        respon.put("connected", Boolean.TRUE);
        respon.put("message", " Hi " + name + ", you connected successfully! You are " + msg);
        return respon;
    }

    //--------------------------------------------------------------------------------------------------------
    @RequestMapping(method = RequestMethod.GET, value = "getState")  //http://127.0.0.1:8080/getState
    public JSONObject getCurrentState(HttpServletRequest servletRequest) {
        Map<String, String> respon = new HashMap<String, String>();
        JSONObject jobject = new JSONObject();
        respon = defineWinner(loc);

        if (respon.get("message").contains("one won")) {  // if there is any winner, finish the game and return the winner
            jobject.put("State", "WON");
            jobject.put("message", "player one won!");
            return jobject;
        } else if (respon.get("message").contains("two won")) {
            jobject.put("State", "WON");
            jobject.put("message", "player two won!");
            return jobject;
        }
/*
 * save the ip of requester
 */
        String playerOneIP = id2Ip.get(1);
        String playerTwoIP = id2Ip.get(2);

        if (id2Ip.size() == 0) {    // this message will be generated before player one and player two register.
            jobject.put("State", "WAITING_FOR_PLAYER");
            jobject.put("message", " Server is waiting for player one and player two to register.");
            return jobject;
        }

        if ((id2Ip.size() == 1) && (id2Ip.get(1).equals(playerOneIP))) {
            jobject.put("State", "WAITING_FOR_PLAYER");
            jobject.put("message", "Waiting for player two! The ip of player two is not registred on the server yet!");
            return jobject;
        }

        if ((playerOneIP != null) && (playerOneIP.equals(String.valueOf(servletRequest.getRemoteAddr()))) && (isPlayerOneTurn)) {  //if its player one's request and player one's turn:
            jobject.put("State", "YOUR_TURN");
            jobject.put("message", "It is player 1's turn.");
        } else if ((playerOneIP != null) && (playerOneIP.equals(String.valueOf(servletRequest.getRemoteAddr()))) && (isPlayerTwoTurn)) {//if its player one's request and player two's turn:
            jobject.put("State", "OPPONENTS_TURN");
            jobject.put("message", "It is player 2's turn.");
        } else if ((playerTwoIP != null) && (playerTwoIP.equals(String.valueOf(servletRequest.getRemoteAddr()))) && (isPlayerTwoTurn)) {//if its player two's request and player one's turn:
            jobject.put("State", "YOUR_TURN");
            jobject.put("message", "It is player 2's turn.");
        } else if ((playerTwoIP != null) && (playerTwoIP.equals(String.valueOf(servletRequest.getRemoteAddr()))) && (isPlayerOneTurn)) {//if its player one's request and player one's turn:
            jobject.put("State", "OPPONENTS_TURN");
            jobject.put("message", "It is player 1's turn.");
        }
        return jobject;
    }

    //--------------------------------------------------------------------------------------------------------

    /**
     * this method will return the winner.
     * @param loc
     * @return Map
     */
    private Map<String, String> defineWinner(int[][] loc) {
        Map<String, String> resp = new HashMap<>();

        for (int y = 0; y <= 5; y++) { // for col0 - col3 ( for first 4 column in the left)
            for (int x = 0; x <= 3; x++) {
                if ((loc[y][x] == 1) && (loc[y][x + 1] == 1) && (loc[y][x + 2] == 1) && (loc[y][x + 3] == 1)) {
                    resp.put("message", " player one won :)");
                    return resp;
                } else if ((loc[y][x] == 2) && (loc[y][x + 1] == 2) && (loc[y][x + 2] == 2) && (loc[y][x + 3] == 2)) {
                    resp.put("message", " player two won :)");
                    return resp;
                } else {
                    resp.put("message", " no winner in horizontal view");
                }
            }
        }
        for (int x = 0; x <= 6; x++) { // for row0 - row2 ( for first 3 row in the bottom)
            for (int y = 0; y <= 2; y++) {
                if ((loc[y][x] == 1) && (loc[y + 1][x] == 1) && (loc[y + 2][x] == 1) && (loc[y + 3][x] == 1)) {
                    resp.put("message", " player one won :)");
                    return resp;
                } else if ((loc[y][x] == 2) && (loc[y + 1][x] == 2) && (loc[y + 2][x] == 2) && (loc[y + 3][x] == 2)) {
                    resp.put("message", " player two won :)");
                    return resp;
                } else {
                    resp.put("message", " no winner in vertical view");
                }
            }
        }
        for (int y = 0; y <= 2; y++) { //num1
            if ((loc[y][0] == 1) && (loc[y + 1][1] == 1) && (loc[y + 2][2] == 1) && (loc[y + 3][3] == 1)) {
                resp.put("message", "player one won ;)");
                return resp;
            } else if ((loc[y][0] == 2) && (loc[y + 1][1] == 2) && (loc[y + 2][2] == 2) && (loc[y + 3][3] == 2)) {
                resp.put("message", "player two won ;)");
                return resp;
            } else {
                resp.put("message", " no winner in diagonal num1");
            }
        }
        for (int x = 1; x <= 3; x++) { //num2
            if ((loc[0][x] == 1) && (loc[1][x + 1] == 1) && (loc[2][x + 2] == 1) && (loc[3][x + 3] == 1)) {
                resp.put("message", "player one won ;)");
                return resp;
            } else if ((loc[0][x] == 2) && (loc[1][x + 1] == 2) && (loc[2][x + 2] == 2) && (loc[3][x + 3] == 2)) {
                resp.put("message", "player two won ;)");
                return resp;
            } else {
                resp.put("message", " no winner in diagonal num2");
            }
        }
        for (int x = 3; x <= 6; x++) { ///num3
            if ((loc[5][x] == 1) && (loc[4][x - 1] == 1) && (loc[3][x - 2] == 1) && (loc[2][x - 3] == 1)) {
                resp.put("message", "player one won ;)");
                return resp;
            } else if ((loc[5][x] == 2) && (loc[4][x - 1] == 2) && (loc[3][x - 2] == 2) && (loc[2][x - 3] == 2)) {
                resp.put("message", "player two won ;)");
                return resp;
            } else {
                resp.put("message", " no winner in diagonal num3");
            }
        }
        for (int y = 3; y <= 4; y++) { //num4
            if ((loc[y][6] == 1) && (loc[y - 1][5] == 1) && (loc[y - 2][4] == 1) && (loc[y - 3][3] == 1)) {
                resp.put("message", "player one won ;)");
                return resp;
            } else if ((loc[y][6] == 2) && (loc[y - 1][5] == 2) && (loc[y - 2][4] == 2) && (loc[y - 3][3] == 2)) {
                resp.put("message", "player two won ;)");
                return resp;
            } else {
                resp.put("message", " no winner in diagonal num4");
            }
        }
        for (int x = 3; x <= 6; x++) { //num 5
            if ((loc[0][x] == 1) && (loc[1][x - 1] == 1) && (loc[2][x - 2] == 1) && (loc[3][x - 3] == 1)) {
                resp.put("message", "player one won ;)");
                return resp;
            }
            if ((loc[0][x] == 2) && (loc[1][x - 1] == 2) && (loc[2][x - 2] == 2) && (loc[3][x - 3] == 2)) {
                resp.put("message", "player two won ;)");
                return resp;
            } else {
                resp.put("message", " no winner in diagonal num5");
            }
        }
        for (int y = 1; y <= 2; y++) {  //num6
            if ((loc[y][6] == 1) && (loc[y + 1][5] == 1) && (loc[y + 2][4] == 1) && (loc[y + 3][3] == 1)) {
                resp.put("message", "player one won ;)");
                return resp;
            } else if ((loc[y][6] == 2) && (loc[y + 1][5] == 2) && (loc[y + 2][4] == 2) && (loc[y + 3][3] == 2)) {
                resp.put("message", "player two won ;)");
                return resp;
            } else {
                resp.put("message", " no winner in diagonal num6");
            }
        }
        for (int y = 3; y <= 5; y++) {  //num7
            if ((loc[y][0] == 1) && (loc[y - 1][1] == 1) && (loc[y - 2][2] == 1) && (loc[y - 3][3] == 1)) {
                resp.put("message", "player one won ;)");
                return resp;
            } else if ((loc[y][0] == 2) && (loc[y - 1][1] == 2) && (loc[y - 2][2] == 2) && (loc[y - 3][3] == 2)) {
                resp.put("message", "player two won ;)");
                return resp;
            } else {
                resp.put("message", " no winner in diagonal num7");
            }
        }
        for (int x = 1; x <= 3; x++) { //num8
            if ((loc[5][x] == 1) && (loc[4][x + 1] == 1) && (loc[3][x + 2] == 1) && (loc[2][x = 3] == 1)) {
                resp.put("message", "player one won ;)");
                return resp;
            } else if ((loc[5][x] == 2) && (loc[4][x + 1] == 2) && (loc[3][x + 2] == 2) && (loc[2][x = 3] == 2)) {
                resp.put("message", "player two won ;)");
                return resp;
            } else {
                resp.put("message", " no winner in diagonal num8");
            }
        }
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

        if (isPlayerOneTurn) {  //TODO: enable it in th end
            if (servletRequest.getRemoteAddr().equals(id2Ip.get(2))) {
                respon.put("message", "This is not your turn! its player one turn.");
                return respon;
            }
        }
        if (isPlayerTwoTurn) {//TODO: enable it in th end
            if (servletRequest.getRemoteAddr().equals(id2Ip.get(1))) {
                respon.put("message", "This is not your turn! its player two turn.");
                return respon;
            }
        }

//        System.out.println("col value: ----> " + Url.get("col"));
//        System.out.println("loc[0][3] val is: ===> " + loc[0][3]);
//        System.out.println("loc[1][3] val is: ===> " + loc[1][3]);
//        System.out.println("loc[2][3] val is: ===> " + loc[2][3]);
//        System.out.println("loc[3][3] val is: ===> " + loc[3][3]);

        topOFThisColumn = topOfColumn(column);

        if (topOFThisColumn == 10) {
            respon.put("Status", "NOT");
            respon.put("message", " The column is already full or client is trying to overwrite the location!");
            return respon;
        }

        /**
         * swith the players
         */
        if (isPlayerOneTurn) {
            if (loc[topOFThisColumn][column] != 0) {
                isPlayerOneTurn = false;  // TODO: enable it later
                isPlayerTwoTurn = true;   // TODO: enable it later
                respon.put("Status", "NOT");
                respon.put("message", "overwriting the data is not accepted. you lost your turn.");
                return respon;
            }
            loc[topOFThisColumn][column] = 1;
            isPlayerOneTurn = false;  // TODO: enable it later
            isPlayerTwoTurn = true;   // TODO: enable it later
            respon.put("Status", "OK");
        } else if (isPlayerTwoTurn) {
            if (loc[topOFThisColumn][column] != 0) {
                isPlayerTwoTurn = false; // TODO: enable it for later
                isPlayerOneTurn = true;   // TODO: enable it later
                respon.put("Status", "NOT");
                respon.put("message", "overwriting the data is not accepted. you lost your turn.");
                return respon;
            }
            loc[topOFThisColumn][column] = 2;
            isPlayerTwoTurn = false;  // TODO: enable it for later
            isPlayerOneTurn = true;   // TODO: enable it later
            respon.put("Status", "OK");
        }
        lastMove = column;
        respon.put("message", "Disc entered");
        return respon;
    }

    //--------------------------------------------------------------------------------------------------------

    /**
     * this methos will return the location of top of entry column
     * @param column
     * @return
     */
    private int topOfColumn(int column) {
        int topIs = 0;  // if the column getting ful (means loc is not ==0 anymore), it will skip bottom for-loop and return 0 !

        for (int i = 0; i < 6; i++) {
            if ((i == 5) && (loc[i][column] != 0)) { // if the column getting ful or user try to change same location
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

    @RequestMapping(method = RequestMethod.GET, value = "GetLastTurn")  //http://127.0.0.1:8080/GetLastTurn
    public Map<String, String> GetLastTurn() {
        Map<String, String> respon = new HashMap<String, String>();

        if (lastMove == -1)
            return null;
        respon.put("column: ", Integer.toString(lastMove));
        return respon;
    }

    //--------------------------------------------------------------------------------------------------------
    @RequestMapping(method = RequestMethod.GET, value = "GetName")  //http://127.0.0.1:8080/GetName
    public Map<String, String> GetName(HttpServletRequest servletRequest) {
        Map<String, String> respon = new HashMap<String, String>();
        Integer id = (Integer) ip2Id.get(servletRequest.getRemoteAddr());

        if (id != null && (id >= 0 && id <= 1)) {
            int oponentId = (id == 0 ? 1 : 0);
            String oponentName = (String) id2Name.get(oponentId);
            if (oponentName != null) {
                respon.put("PlayerName: ", oponentName);
                return respon;
            }
        }
        return null;
    }

}













//    @RequestMapping("/connect")    //http://127.0.0.1:8080/connect/    or http://127.0.0.1:8080/connect?name=Mahdi //TODO: delete it
//    public Connect4 anything(@RequestParam(value="name" ) String name, HttpServletRequest servletRequest) {
//        if (counter.get() >1 ) {
//            return new Connect4(counter.get() , " This game is two player! you should try Play Station 4 instead ;)");
//        }
//        name2Ip.put(name, servletRequest.getRemoteAddr());
//        String msg = counter.get() == 0 ? " player one " : " player two" ;
//        return new Connect4(counter.incrementAndGet(), String.format("Hej " + name +", you are" + msg));
//    }
//--------------------------------------------------------------------------------------------------------