import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 게임 유저 정보를 관리하는 클래스
 */
public class User {
    public String name;
    public PrintWriter pw;
    public boolean host;    // 출제자인지 아닌지
    public boolean turn;    // 자기 차례인지 아닌지

    /**
     * 닉네임, PrintWriter를 받아 User 객체 생성
     * @param name
     * @param pw
     */
    public User(String name, PrintWriter pw) {
        this.name = name;
        this.pw = pw;
        host = false;
        turn = false;
    }
}

/**
 * User 클래스를 HashMap으로 관리하는 클래스
 */
class UserMap {
    HashMap<String, User> clients;

    UserMap() {
        clients = new HashMap<String, User>();
        Collections.synchronizedMap(clients);
    }

    HashMap<String, User> getMap() {
        return clients;
    }

    /**
     * 문자열 name과 PrintWriter out을 인자로 받아
     * clients에 새 User 객체를 추가한다.
     * @param name
     * @param out
     */
    void add(String name, PrintWriter out) {
        clients.put(name, new User(name, out));
    }

    /**
     * 현재 출제자(host)인 User 리턴
     * @return user
     */
    User getHost() {
        Iterator it = clients.keySet().iterator();

        while (it.hasNext()) {
            try {
                User user = clients.get(it.next());
                if (user.host) {
                    return user;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 현재 차례인 User 리턴
     * @return user
     */
    User getTurn() {
        for (String s : clients.keySet()) {
            try {
                User user = clients.get(s);
                if (user.turn) return user;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 해당 user.turn = true로 설정
     * @param user
     */
    void setTurn(User user) {
        for (String s : clients.keySet()) {
            try {
                User tmp = clients.get(s);
                tmp.turn = tmp == user;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 해당하는 User 리턴
     * @param name
     */
    User getUser(String name) {
        return clients.getOrDefault(name, null);
    }

    /**
     * 게임 시작 시,
     * 랜덤으로 User에게 turn = true 설정
     */
    void setRandomTurn() {
        for (String s : clients.keySet()) {
            User tmp = clients.get(s);
            if (!tmp.host && !tmp.turn) {
                tmp.turn = true;
                break;
            }
        }
    }

    /**
     * 게임 진행 시,
     * 다음 차례 User를 찾아 turn = true 설정
     */
    void setNextTurn() {
        ArrayList<User> tempList = new ArrayList<>();
        for (String name : clients.keySet()) {
            tempList.add(clients.get(name));
        }
        for (String name : clients.keySet()) {
            tempList.add(clients.get(name));
        }

        User nowTurn = getTurn();
        nowTurn.turn = false;
        if (!tempList.get(tempList.indexOf(nowTurn) + 1).host)
            tempList.get(tempList.indexOf(nowTurn) + 1).turn = true;
        else
            tempList.get(tempList.indexOf(nowTurn) + 2).turn = true;
    }
}
