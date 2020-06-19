import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * RMI Interface
 */
public interface Game extends Remote {
    void addQuestion(String questiong) throws RemoteException;
    void addAnswer(String answer) throws RemoteException;
    String getLastQuestion() throws RemoteException;
    ArrayList<String> showQuestions() throws RemoteException;

    boolean checkNameFormat(String name) throws RemoteException;
    void addUser(String name) throws RemoteException;
    void removeUser(String name) throws RemoteException;
    ArrayList<String> getUserList() throws RemoteException;

    void Start(String hostname, String answer) throws RemoteException;
    void End() throws RemoteException;
    boolean isRunning() throws RemoteException;
    String getHostname() throws RemoteException;
    String getFinalAnswer() throws RemoteException;
}
