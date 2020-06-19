import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * RMI Interface를 구현한 클래스이다.
 * 현재 진행중인 게임의 전반적인 정보를 담고 있다.
 */
public class GameImpl extends UnicastRemoteObject implements Game {
    private static final long serialVersionUID = 1L;

    // 게임이 진행 중인지
    boolean isRunning = false;

    // 출제자 이름
    String hostname;

    // 최종 정답
    String finalAnswer;

    // 질문, 답변 목록
    ArrayList<String> questionList;
    ArrayList<String> answerList;

    // 유저 목록
    ArrayList<String> userList;

    public GameImpl() throws RemoteException {
        super();
        finalAnswer = "";
        questionList = new ArrayList<>();
        answerList = new ArrayList<>();
        userList = new ArrayList<>();
    }

    /**
     * User의 질문을 GameImpl 클래스 내 질문 목록에 업데이트한다.
     * @param question
     * @throws RemoteException
     */
    @Override
    public void addQuestion(String question) throws RemoteException {
        questionList.add(question.substring(4));
    }

    /**
     * User의 질문을 GameImpl 클래스 내 답변 목록에 업데이트한다.
     * @param res
     * @throws RemoteException
     */
    @Override
    public void addAnswer(String res) throws RemoteException {
        String answer = getLastQuestion() + "\t" + res.substring(4);
        answerList.add(answer);
    }

    /**
     * 질문 중 마지막으로 업데이트 된 것을 리턴한다.
     * @return
     * @throws RemoteException
     */
    @Override
    public String getLastQuestion() throws RemoteException {
        return questionList.get(questionList.size()-1);
    }

    /**
     * 현재까지 나온 질문 목록을 리턴한다.
     * @return
     * @throws RemoteException
     */
    @Override
    public ArrayList<String> showQuestions() throws RemoteException {
        return answerList;
    }

    /**
     * 사용자가 입력한 이름의 형식과 중복을 검사한다.
     * @param name
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean checkNameFormat(String name) throws RemoteException{
        return name.length() <= 6 && !userList.contains(name);
    }

    /**
     * 유저 목록에 유저 이름을 업데이트한다.
     * @param name
     * @throws RemoteException
     */
    @Override
    public void addUser(String name) throws RemoteException {
        userList.add(name);
    }

    /**
     * 유저 목록에서 유저 이름을 삭제한다.
     * @param name
     * @throws RemoteException
     */
    @Override
    public void removeUser(String name) throws RemoteException {
        if (!name.isEmpty()) {
            userList.remove(name);
        }
    }

    /**
     * 유저 목록을 리턴한다.
     * @return
     * @throws RemoteException
     */
    @Override
    public ArrayList<String> getUserList() throws RemoteException {
        return userList;
    }

    /**
     * 게임 시작 시 실행되는 함수이다.
     * 출제자 이름을 저장하고, 현재 실행 중인지 가리키는 isRunning 을 true로 설정한다.
     * @param hostname
     * @param answer
     * @throws RemoteException
     */
    @Override
    public void Start(String hostname, String answer) throws RemoteException {
        this.hostname = hostname;
        isRunning = true;
        finalAnswer = answer;
    }

    /**
     * 게임 종료 시 호출되는 함수이다.
     * @throws RemoteException
     */
    @Override
    public void End() throws RemoteException {
        isRunning = false;
    }

    /**
     * 현재 게임이 실행중인지 확인할 수 있는 함수이다.
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean isRunning() throws RemoteException {
        return isRunning;
    }

    /**
     * 현재 출제자의 이름을 리턴하는 함수이다.
     * @return
     * @throws RemoteException
     */
    @Override
    public String getHostname() throws RemoteException {
        return hostname;
    }

    /**
     * 최종 정답을 가져오는 함수이다.
     * @return
     * @throws RemoteException
     */
    @Override
    public String getFinalAnswer() throws RemoteException {
        return finalAnswer;
    }


}