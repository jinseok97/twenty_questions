# 스무고개 게임

RMI Registry와 SSLSocketFactory를 이용한 스무고개 게임<br><br>

## 실행

### 컴파일
<br>

### 인증서 생성
 % keytool -genkeypair -alias myKey -keyalg RSA -validity 7 -keystore mySrvKeystore (비밀번호 123456) <br><br>
 % keytool -export -alias myKey -keystore MySrvKeystore -rfc -file MySrv.cer <br><br>
 % keytool -import -alias myKey -file MySrv.cer -keystore trustedcerts <br><br>


### 서버 실행

##### 터미널 1
% rmiregistry

##### 터미널 2
% java Server

### 클라이언트 실행

##### 터미널 3
% java ClientWindow

##### 터미널 4
% java ClientWindow

##### 터미널 5
% java ClientWindow


