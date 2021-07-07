# tu-recibo

### Para iniciar el app con el siguiente comando: 
mvn spring-boot:run 

### Para compilar
mvn clean install 

### Para bajar los cambios en GIT
git pull origin master 

### Para subir los cambios a GIT
git add nombre_archivo
git commit -m 'Comentario'
git push origin master 

### Para debugear 
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"


