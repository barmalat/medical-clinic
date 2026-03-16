FROM eclipse-temurin:21-jdk
COPY target/medicalclinnic-0.0.1-SNAPSHOT.jar appMedicalclinnic/appMedicalclinnic.jar
ENTRYPOINT ["java", "-jar", "appMedicalclinnic/appMedicalclinnic.jar"]