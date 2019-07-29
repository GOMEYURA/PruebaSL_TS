package FUNCTION;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.Reporter;

import PARAMETER.Parameters;

/**Métodos Funciones Generales.*/
public class Functions {

	/**Contiene la sesión del explorador.*/
	public static WebDriver driver = null;
	/**Contiene el estado de ejecución del escenario.*/
	public static ITestResult result;

	/**
	 * Método que realiza la conexiónn al driver del explorador y abre una sesión.
	 * @param url String - Contiene la direcciónn URL de la aplicación a conectar.
	 * @throws Exception RuntimeError
	 */
	@SuppressWarnings("deprecation")
	public static void openBrowser(String url) throws Exception {

		By overidelink = By.xpath("//a[@name='overridelink']");
		
		File ieFile = new File("D:\\Selenium\\Drivers\\chromedriver.exe");
		System.setProperty("webdriver.ie.driver", ieFile.getAbsolutePath());
		DesiredCapabilities ieCaps = DesiredCapabilities.internetExplorer();
		ieCaps.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL,url);
		ieCaps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		driver = new InternetExplorerDriver(ieCaps);
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		driver.get(url);
		if(waitForElement(driver,overidelink,5)){
			driver.findElement(overidelink).click();
		}
	}

	/**
	 * Método que espera si el WebElement buscado existe en pantalla.
	 * @param driver WebDriver - Contiene la sesión abierta en el explorador.
	 * @param webElementId String - Cadena xpath con el identificador del elemento a buscar.
	 * @param timeOut Integer - Tiempo en segundos de espera del elemento.
	 * @throws Exception RuntimeError
	 * @return Boolean True-Existe / False-No existe
	 */
	public static boolean waitForElement(WebDriver driver, final By webElementId, int timeOut) throws Exception{

		try {
			
			WebDriverWait wait = new WebDriverWait(driver, timeOut);
			wait.until(ExpectedConditions.visibilityOfElementLocated(webElementId));
			return Boolean.TRUE;
		} catch (Exception e) {
		
			return Boolean.FALSE;
		}
	}

	/**
	 * Método que identifica en el reporte de resultados el estado de los eventos de ejecución.
	 * @param module String - Texto con el nombre del modulo que se está ejecutando.
	 * @param message String - Texto con descripción del evento ejecutado. 
	 * @param level Integer - Identificador del estado de ejecución del evento: 0-Default; 1-Warning; 2-Error; 3-Fatal.
	 * @param element WebElement - Elemento al cual se va a realizar la captura de pantalla, si es null, tomara una captura de toda la pantalla.
	 * @throws Exception RuntimeError
	 */
	public static void LogReport(String module, String message, int level, WebElement element) throws Exception {

		result = Reporter.getCurrentTestResult();
		switch (level){
		default: //Information
			Parameters.log.info(message);
			fillReporter(module,"INFO",message,takeScreenshot(element));
			break;
		case 1: //Warning
			Parameters.log.warn(message);
			result.setStatus(ITestResult.SKIP);
			Reporter.setCurrentTestResult(result);
			fillReporter(module,"WARNING",message,takeScreenshot(element));
			break;
		case 2: //Error
			Parameters.log.error(message  + " Page: "+Thread.currentThread().getStackTrace()[2].getMethodName());
			result.setStatus(ITestResult.FAILURE);
			Reporter.setCurrentTestResult(result);
			fillReporter(module,"ERROR",message,takeScreenshot(element));
			throw new RuntimeException(module + ": " + message);
		case 3: //Fatal
			Parameters.log.fatal(message  + " Page: "+ Thread.currentThread().getStackTrace()[2].getMethodName());
			result.setStatus(ITestResult.FAILURE);
			Reporter.setCurrentTestResult(result);
			fillReporter(module,"FATAL",message,takeScreenshot(element));
			throw new RuntimeException(module + ": " + message);
		}
	}

	/**
	 * Método que genera reporte HTML para usuario final.
	 * */
	public static void createReporter(){
		String html = "<table border=\"1\"><tr><th>Action</th><th>Status</th><th>Description</th><th>Screenshot</th></tr>"
				+Parameters.htmlReport+"</table>"; 
		Reporter.log("<table border=\"1\"><tr><th>Inicio Prueba</th><th>"+Parameters.startTime+"</th></tr></table>");
		Reporter.log(html);
		Parameters.endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
		Parameters.startTime = Parameters.endTime;
		Reporter.log("<table border=\"1\"><tr><th>Fin Prueba</th><th>"+Parameters.endTime+"</th></tr></table>");
		Parameters.htmlReport = "";
	}

	/**
	 * Método que convierte a estructura HTML cada evento reportado en el Log.
	 * @param module String - Texto con el nombre del modulo que se está ejecutando.
	 * @param status String - Texto del status de ejecución del evento.
	 * @param comments String - Texto con descripción del evento ejecutado.
	 * @param srcImgFile String - Texto con ruta donde fue almacenada la imagen del evento.
	 */
	public static void fillReporter(String module, String status, String comments, String srcImgFile){
		Parameters.htmlReport = Parameters.htmlReport+
				"<tr><th>"+module+"</th><th>"+status+"</th><th align=\"left\">"+comments+"</th><th>"+srcImgFile+"</th>";
	}

	/**
	 * Método para capturar imagen de pantalla.
	 * @param element WebElement - Elemento en pantalla del cual se quiere capturar una imagen, en caso de enviar null el parámetro, va a capturar una imagen de toda la pantalla.
	 * @return String ruta donde se almacena la captura de pantalla.
	 * @throws Exception RuntimeError
	 */
	public static String takeScreenshot(WebElement element) throws Exception {

		Robot rbt = new Robot();
		Point position = null;
		Dimension dimension = null;
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		File ScrFile = new File(".\\test-output\\Screenshots\\"+timeStamp+".png");
		String path = "<img class=\"img\" width=\"40%\"  id=\""+timeStamp+"\" src="+".\\Screenshots\\"+timeStamp+".png"+" alt=\"PortalColaborador\"/>"; 
		Rectangle capture = null;

		if(element==null){

			position = driver.manage().window().getPosition();
			dimension = driver.manage().window().getSize();
			capture = new Rectangle(position.getX(),position.getY(),dimension.width,dimension.height);
		}
		else{
			position = element.getLocation();
			dimension = element.getSize();
			capture = new Rectangle(position.getX(),position.getY()+60,dimension.width,dimension.height);
		}

		BufferedImage Image = rbt.createScreenCapture(capture);
		ImageIO.write(Image, "png", ScrFile);
		return path;
	}

	/**
	 * Método para hacer llamado al archivo .bat que ejecuta un backup de la carpeta test-output donde se genera el reporte HTML.
	 * @throws Exception RuntimeError
	 */
	public static void folderReportBK() throws Exception{
		try {
			Runtime.getRuntime().exec("cmd /c start \"\" .\\Scripts\\DirProcess.bat");
		
		} catch (IOException e) {
			
			LogReport("Creación Reporte",e.toString(),3, null);
		}
	}
	
	/**
	 * Método para cancelar la ejecución de procesos en la máquina.
	 * @param serviceName String - Texto con el nombre del proceso a cancelar.
	 * @throws Exception RuntimeError
	 * @return Boolean True-Existe / False-No existe
	 */
	public static boolean isProcessRunning(String serviceName) throws Exception {

		Process p = Runtime.getRuntime().exec("tasklist");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {

			System.out.println(line);
			if (line.contains(serviceName)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Método para cambiar el contexto de la sesión en caso de que se abra una nueva ventana del explorador.
	 * @throws Exception RuntimeError
	 * @return String Current Context
	 */
	public static String switchContext() throws Exception{
		
		Boolean flag = false;
		String CurrentContext = null;		
		
		try {
			
			Object[] contexts = driver.getWindowHandles().toArray();
			CurrentContext = driver.getWindowHandle().toString();
			
			for(int i=0;i<contexts.length;i++){
				if(!CurrentContext.equals(contexts[i].toString())){
					driver.switchTo().window(contexts[i].toString());
					flag = true;
					break;
				}
			}
			if(!flag){
				LogReport("Nueva ventana explorador",Parameters.ErrChContext,2,null);
			}						
			
		} catch (Exception e) {
			LogReport("Nueva ventana explorador",e.toString(),3, null);
		}
		return CurrentContext;
	}
	
	/**
	 * Método para retornar valor de un archivo de Excel .xlsx.
	 * @param Modulo String - Texto con nombre de la hoja del archivo de Excel donde se encuentra el valor a buscar.
	 * @param Parameter String - Texto con el nombre de la columna donde se encuentra el valor a buscar.
	 * @throws Exception RuntimeError
	 * @return String Valor del parámetro solicitado.
	 */
	@SuppressWarnings("deprecation")
	public static String getParameter(String Modulo, String Parameter) throws Exception{
		
		String fileDataEntry = ".\\DataEntry\\DataEntry.xlsx";
		XSSFWorkbook workbook;
		XSSFRow row;
		XSSFCell cell;
		String parameter = null;
		
		try {
			FileInputStream fileInputStream = new FileInputStream(fileDataEntry);
			workbook = new XSSFWorkbook(fileInputStream);
			XSSFSheet sheet = workbook.getSheet(Modulo);
			row=(XSSFRow) sheet.getRow(0);
			Iterator<Cell> cells = row.cellIterator();
			boolean flag = false;
            
			while (cells.hasNext()) {            		
				cell=(XSSFCell) cells.next();
				if(cell.getStringCellValue().equals(Parameter)){
					XSSFCell Cellvalue = sheet.getRow(1).getCell(cell.getColumnIndex());
					if(Cellvalue.getCellType()==XSSFCell.CELL_TYPE_NUMERIC){
						if(DateUtil.isCellDateFormatted(Cellvalue)) {
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
							parameter = dateFormat.format(Cellvalue.getDateCellValue()).toString();
						}
						else{
							parameter = String.format("%.0f", Cellvalue.getNumericCellValue());
						}						
					}
					else{
						parameter = sheet.getRow(1).getCell(cell.getColumnIndex()).toString();
					}
					flag = true;
					break;
				}
            }
			if(!flag){
				LogReport("Carga Datos Excel",Parameters.ErrgetPrmtDataEnt,2, null);
			}
			
		} catch (Exception e) {
			LogReport("Carga Datos Excel",e.toString(),3, null);
		}
		return parameter;
	}

	/**
	 * Método para asignar un valor en algún parametro del archivo de Excel .xlsx.
	 * @param Parameter String - Texto con el nombre de la columna donde se encuentra el valor a modificar.
	 * @param Value String - Texto por el cual se va a modificar el parámetro buscado.
	 * @throws Exception RuntimeError
	 */
	public static void setParameter(String Parameter, String Value) throws Exception{
		
		String fileDataEntry = ".\\DataEntry\\DataEntry.xlsx";
		XSSFWorkbook workbook;
		XSSFSheet sheet;
		XSSFRow row;
		XSSFCell cell;
		
		try {
			FileInputStream fileInputStream = new FileInputStream(fileDataEntry);
			workbook = new XSSFWorkbook(fileInputStream);
			Iterator<Sheet> sheets = workbook.sheetIterator();
			while (sheets.hasNext()) {
				
				sheet=(XSSFSheet) sheets.next();
				row=(XSSFRow) sheet.getRow(0);
				Iterator<Cell> cells = row.cellIterator();
	            
				while (cells.hasNext()) {            		
					cell=(XSSFCell) cells.next();
					if(cell.getStringCellValue().equals(Parameter)){
						sheet.getRow(1).getCell(cell.getColumnIndex()).setCellValue(Value);
					}
	            }
			}
			fileInputStream.close();
			FileOutputStream FileOutputStream = new FileOutputStream(fileDataEntry);
			workbook.write(FileOutputStream);
			FileOutputStream.close();
			
		} catch (Exception e) {
			LogReport("Modificar Datos Excel",e.toString(),3, null);
		}
	}		
}