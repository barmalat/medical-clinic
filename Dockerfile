FROM eclipse-temurin:21-jdk
COPY target/medicalclinic-0.0.1-SNAPSHOT.jar appMedicalclinic/appMedicalclinic.jar
ENTRYPOINT ["java", "-jar", "appMedicalclinic/appMedicalclinic.jar"]