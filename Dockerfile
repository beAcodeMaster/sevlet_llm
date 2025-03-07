# Tomcat 10.1.0과 OpenJDK 17 (Temurin) 기반 이미지 사용
FROM tomcat:10.1.361-jdk17-temurin

# 기본으로 포함된 웹 애플리케이션 제거
RUN rm -rf /usr/local/tomcat/webapps/*

# Maven 빌드 후 생성된 WAR 파일을 Tomcat의 ROOT 웹앱으로 복사
COPY target/ex09.war /usr/local/tomcat/webapps/ROOT.war

# Tomcat 기본 포트 노출
EXPOSE 8080

# Tomcat 실행
CMD ["catalina.sh", "run"]
