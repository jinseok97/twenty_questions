// -Djava.rmi.server.hostname=localhost

import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class GameImpl extends UnicastRemoteObject implements Game {
    private static final long serialVersionUID = 1L;

    boolean isStart = false;
    String currQuestion;
    HashMap<String, String> questionList;
    ArrayList<String> nameList;

    public GameImpl() throws RemoteException {
        super();
        currQuestion = "";
        questionList = new HashMap<>();
        nameList = new ArrayList<>();
    }

    @Override
    public String command(String command) throws RemoteException {
        String result = "";

        if (command.equals("!정답")) {

        } else if (command.equals("!목록")) {
            for (String question : questionList.keySet()) {
                result += question + " " + questionList.get(question) + "\n";
            }
        } else if (command.equals("!패스")) {

        } else {
            result = "올바른 명령어가 아닙니다! : \n  1. !정답\n  2. !목록\n  3. !패스";
        }

        return result;
    }

    @Override
    public void addQuestion(String question) throws RemoteException {
        questionList.put(question, "");
    }

    @Override
    public void setAnswer(String answer) throws RemoteException {
        questionList.replace(currQuestion, answer);
    }

    @Override
    public boolean checkNameFormat(String name) throws RemoteException{
        return name.length() <= 6 && !nameList.contains(name);
    }

    @Override
    public void addUser(String name) throws RemoteException {
        nameList.add(name);
    }

    @Override
    public void removeUser(String name) throws RemoteException {
        if (!name.isEmpty()) {
            nameList.remove(name);
        }
    }

    @Override
    public ArrayList<String> getUserList() throws RemoteException {
        return nameList;
    }

    @Override
    public void setIsStart() throws RemoteException {
        isStart = true;
    }

    @Override
    public boolean isRunning() throws RemoteException {
        return isStart;
    }
}