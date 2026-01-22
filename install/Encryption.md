## 키 저장소 생성

```shell
"C:\Program Files\Java\jdk-21\bin\keytool" -genkeypair -alias apiEncryptionKey -keyalg RSA -dname "CN=hyungeun Shin, OU=API Development, L=Seoul, C=KR" -keypass "1q2w3e4r" -keystore apiEncryptionKey.jks -storepass "1q2w3e4r"
```

## 확인

```shell
"C:\Program Files\Java\jdk-21\bin\keytool" -list -keystore apiEncryptionKey.jks -v
```

## Public Key 생성

```shell
"C:\Program Files\Java\jdk-21\bin\keytool" -exportcert -alias apiEncryptionKey -keystore apiEncryptionKey.jks -rfc -file public-key.pem
```

## Private Key 변환 및 생성

```shell
"C:\Program Files\Java\jdk-21\bin\keytool" -importkeystore -srckeystore apiEncryptionKey.jks -srcalias apiEncryptionKey -destkeystore test-private.p12 -deststoretype PKCS12
```

```shell
"C:\Program Files\Git\usr\bin\openssl.exe" pkcs12 -in test-private.p12 -nocerts -nodes -out private-key.pem
```