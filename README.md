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


