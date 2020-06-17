/**
 * '실행폴더'에서 인증서 생성
 *
 * % keytool -genkeypair -alias myKey -keyalg RSA -validity 7 -keystore mySrvKeystore (비밀번호 123456)
 * % keytool -export -alias myKey -keystore MySrvKeystore -rfc -file MySrv.cer
 * % keytool -import -alias myKey -file MySrv.cer -keystore trustedcerts
 *
 *
 *
 * 실행하기 (터미널 세 개 이상)
 *
 * 터미널 1 : rmiregistry
 * 터미널 2 : java Server
 * 터미널 3 : java ClientWindow
 * 터미널 4 : java CleintWindow
 *      .
 *      .
 *
 */

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Game extends Remote {
    String command(String command) throws RemoteException;
    void addQuestion(String questiong) throws RemoteException;
    void addAnswer(String answer) throws RemoteException;
    String getLastQuestion() throws RemoteException;
    boolean checkNameFormat(String name) throws RemoteException;

    void addUser(String name) throws RemoteException;
    void removeUser(String name) throws RemoteException;
    ArrayList<String> getUserList() throws RemoteException;

    void Start() throws RemoteException;
    boolean isRunning() throws RemoteException;

    ArrayList<String> showQuestions() throws RemoteException;
    void setFinalAnswer(String in) throws  RemoteException;

    void setHostname(String in) throws RemoteException;
    String getHostname() throws RemoteException;
}
