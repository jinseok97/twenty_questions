import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Game extends Remote {
    public String command(String command) throws RemoteException;
    public void addQuestion(String question) throws RemoteException;
    public void setAnswer(String answer) throws RemoteException;
    boolean checkNameFormat(String name) throws RemoteException;
}
