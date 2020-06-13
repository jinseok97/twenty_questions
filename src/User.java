import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 게임에 접속한 유저의 정보를 관리하는 클래스
 *
 * name : 유저가 입력한 닉네임
 * pw   : 유저가 연결한 printWriter
 * host : 유저가 host일 경우 true
 * turn : 유저의 차례일 경우 true (host에게 질문)
 */
public class User {
    public String name;
    public PrintWriter pw;
    public boolean host;    // 방장인지 아닌지
    public boolean turn;    // 자기 차례인지 아닌지

    /**
     * 게임 접속시 입력한 닉네임, PrintWriter를 받아 User 객체 생성
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
 * User를 HashMap으로 관리하는 클래스
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
     * clients에 새 UserInfo 객체를 추가한다.
     * @param name
     * @param out
     */
    void add(String name, PrintWriter out) {
        clients.put(name, new User(name, out));
    }

    /**
     * name 이름을 가진 객체를 map에서 삭제
     * @param name
     */
    void remove(String name) {
        clients.remove(name);
    }

    /**
     * 현재 host인 UserInfo 리턴
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
     * 현재 Turn인 User 리턴
     * @return user
     */
    User getTurn() {
        Iterator it = clients.keySet().iterator();

        while (it.hasNext()) {
            try {
                User user = clients.get(it.next());
                if (user.turn) {
                    return user;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * user를 Turn으로 설정
     * @param user
     */
    void setTurn(User user) {
        Iterator it = clients.keySet().iterator();

        while (it.hasNext()) {
            try {
                User tmp = clients.get(it.next());
                if (tmp == user) {
                    tmp.turn = true;
                } else {
                    tmp.turn = false;
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 해당하는 User 리턴
     * @param name
     */
    User getUser(String name) {
        if (clients.containsKey(name)) {
            return clients.get(name);
        } else {
            return null;
        }
    }
}
