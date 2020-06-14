import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface Game extends Remote {
    String command(String command) throws RemoteException;
    void addQuestion(String question) throws RemoteException;
    void setAnswer(String answer) throws RemoteException;
    boolean checkNameFormat(String name) throws RemoteException;

    void addUser(String name) throws RemoteException;
    void removeUser(String name) throws RemoteException;
    ArrayList<String> getUserList() throws RemoteException;

    void setIsStart() throws RemoteException;
    boolean isRunning() throws RemoteException;
}
