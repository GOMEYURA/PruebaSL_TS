package PARAMETER;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeTest;

import FUNCTION.Functions;

/**
 * Página con parámetros generales.
 */
public class Parameters {

	//Parametros Generales.
	/**Configura la variable log4j para el proyecto.*/
	public static Logger log = Logger.getLogger("PruebaSL");
	/**Código HTML con estilo para ampliar imagen en log.*/
	public static String htmlReport = "<style>.img:hover{color: #424242;-webkit-transition: all .3s ease-in;-moz-transition: all .3s ease-in;-ms-transition: all .3s ease-in;-o-transition: all .3s ease-in;" +
			"transition: all .3s ease-in;opacity: 1;transform: scale(2.99);-ms-transform: scale(2.99); /* IE 9 */-webkit-transform: scale(2.99); /* Safari and Chrome */}</style>";
	/**Fecha inicio ejecución caso de prueba.*/
	public static String startTime = null;
	/**Fecha finalización ejecución caso de prueba.*/
	public static String endTime = null;
		
	//Parametros Function.
	public static String ErrChContext;
	/**Mensaje fallido consiguiendo parámetro de DataEntry.*/
	public static final String ErrgetPrmtDataEnt = "Error al encontrar el parámetro en el archivo de entrada: ";

	//Credenciales ingreso aplicación XXXXXXX.  
	/**Dirección URL de la aplicación XXXXXXX.*/
	public static final String url = "https://co.ebay.com/";
	/**Nombre de usuario para ingreso a la aplicación.*/
	public static final String usuario = ""; 
	/**Contraseña de usuario para ingreso a la aplicación.*/
	public static final String contrasena = "";
	
	//Parametros Page XXXXXXX	
	/**Mensaje exitoso apertura ventana explorado.*/
	public static final String msgOpnBwr = "Explorador cargado correctamente.";
	/**Mensaje fallido apertura ventana explorado.*/
	public static final String ErrOpnBwr = "No se abrio correctamente el explorador.";
	
	/**
	 * Método para antes de iniciar el caso configurar parámetros generales:
	 * - Archivo propiedades log4j
	 * - Fecha de inicio caso de prueba
	 */
	@BeforeTest(alwaysRun = true)
	public static void getParameters(){
		PropertyConfigurator.configure("src\\log4j.properties");
		startTime = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
	}
	
	/**
	 * Método para después de terminar el caso:
	 * - Finalizar procesos asociados al driver.
	 * - Ejecutar método para la copia de los resultados del caso de prueba.
	 * @throws Exception RuntimeError
	 */
	@AfterSuite(alwaysRun = true)
	public static void endProcess() throws Exception{
		
		if(Functions.isProcessRunning("IEDriverServer.exe")){
			Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
		} 
		Functions.folderReportBK();
	}
}
