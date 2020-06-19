# 스무고개 게임

RMI Registry와 SSLSocketFactory를 이용한 스무고개 게임<br><br>

## 실행
아래 모든 과정은 source 폴더에서 수행되어야 한다<br>

### 컴파일
javac *.java <br><br>

### 인증서 생성
모든 비밀번호는 123456으로 통일한다.

1. % keytool -genkeypair -alias myKey -keyalg RSA -validity 7 -keystore mySrvKeystore (비밀번호 123456) <br><br>
2. % keytool -export -alias myKey -keystore MySrvKeystore -rfc -file MySrv.cer <br><br>
3. % keytool -import -alias myKey -file MySrv.cer -keystore trustedcerts <br><br>


### 서버 실행

##### 터미널 1
% rmiregistry

##### 터미널 2
% java Server

### 클라이언트 실행
3인 이상의 클라이언트가 필요하다 

##### 터미널 3
% java ClientWindow

##### 터미널 4
% java ClientWindow

##### 터미널 5
% java ClientWindow


### 게임 방법

1. 3인 이상 이름을 입력, 접속한 후 게임 시작 버튼을 누릅니다.

2. 버튼을 누른 사람이 출제자가 되며, 나머지 인원이 출제자에게 질문을 합니다.

3. 출제자는 예, 아니오, 정답! 중 선택할 수 있으며, 정답!이 아닐 경우 2, 3번을 반복합니다.

4. 정답! 이거나 20번 스무 고개가 끝났을 경우 게임은 종료됩니다. 대화창만 사용 가능합니다.  

