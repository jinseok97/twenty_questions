// -Djava.rmi.server.hostname=localhost

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class GameImpl extends UnicastRemoteObject implements Game {
    private static final long serialVersionUID = 1L;

    boolean isStart = false;
    String finalAnswer;
    ArrayList<String> questionList;
    ArrayList<String> answerList;
    ArrayList<String> userList;

    public GameImpl() throws RemoteException {
        super();
        finalAnswer = "";
        questionList = new ArrayList<>();
        answerList = new ArrayList<>();
        userList = new ArrayList<>();
    }

    @Override
    public String command(String command) throws RemoteException {
        String result = "";

        if (command.equals("!정답")) {

        } else if (command.equals("!목록")) {
//            for (String question : questionList.keySet()) {
//                result += question + " " + questionList.get(question) + "\n";
//            }
        } else if (command.equals("!패스")) {

        } else {
            result = "올바른 명령어가 아닙니다! : \n  1. !정답\n  2. !목록\n  3. !패스";
        }

        return result;
    }

    @Override
    public void addQuestion(String question) throws RemoteException {
        questionList.add(question);
    }

    @Override
    public void setAnswer(String answer) throws RemoteException {
        answerList.add(answer);
    }

    @Override
    public boolean checkNameFormat(String name) throws RemoteException{
        return name.length() <= 6 && !userList.contains(name);
    }

    @Override
    public void addUser(String name) throws RemoteException {
        userList.add(name);
    }

    @Override
    public void removeUser(String name) throws RemoteException {
        if (!name.isEmpty()) {
            userList.remove(name);
        }
    }

    @Override
    public ArrayList<String> getUserList() throws RemoteException {
        return userList;
    }

    @Override
    public void Start() throws RemoteException {
        isStart = true;
    }

    @Override
    public boolean isRunning() throws RemoteException {
        return isStart;
    }

    @Override
    public ArrayList<String> showQuestions() throws RemoteException {
        for (int i = 0; i < questionList.size(); i++) {
            questionList.set(i, questionList.get(i) + " " + answerList.get(i));
        }

        return questionList;
    }

    @Override
    public void setFinalAnswer(String in) throws RemoteException {
        finalAnswer = in;
    }
}