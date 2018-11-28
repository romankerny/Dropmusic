Para correr :

	- Multicast Server
		Entrar na directoria MulticastServer_jar
		Digitar na consola:[*]
		>> java -jar MulticastServer.jar

	- RMI Server
		Entrar na directoria RMIServer_jar
		Digitar na consola:[*]
		>> java -jar rmiserver.RMIServer.jar
		

	- RMI Client
		Entrar na directoria RMICLIent_jar
		Digitar na consola:
		>> java -jar RMIClient.jar IP_DO_RMI_SERVER


		IP_DO_RMI_SERVER - endereço da máquina onde está a correr o servidor RMI
			

[*]
Em algumas máquinas a JVM usa IPv6 por defeito, para contornar digite a seguinte de flag de configuração da JVM:

	-Djava.net.preferIPv4Stack=true 

Exemplo:
	java -Djava.net.preferIPv4Stack=true -jar MulticastServer.jar

