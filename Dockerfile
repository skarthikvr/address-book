FROM openjdk:21-ea-jdk-slim
ADD target/address-book-0.0.1-SNAPSHOT.jar address-book.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "address-book.jar" ]