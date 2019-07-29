package PAGE;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import FUNCTION.Functions;
import PARAMETER.Parameters;

public class PruebaSL extends Functions {

	// Mapeo de elementos en funcionalidad
	private static By IntSearch = By.xpath("//*[@id='gh-ac']");
	private static By ChkTalla = By.xpath("//*[@id='w_1564409330412_cbx']");
	private static By IntSearchMarca = By.xpath("//*[@id='w3-w2-0[0]0']");
	private static By ChkMarca = By.xpath("//*[@id='w_1564411107299_cbx']");
	private static By pagResult = By.xpath("//*[@id='srp-river-results']/ul");

	/**
	 * Método para el ingreso a la aplicación
	 * 
	 * @throws Exception RuntimeError
	 */
	public static void PruebaSLM() throws Exception {
		try {
			openBrowser(Parameters.url);

			if (waitForElement(driver, IntSearch, 5)) {
				driver.findElement(IntSearch).sendKeys(Functions.getParameter("Informacion", "Busca1"));
				if (waitForElement(driver, ChkTalla, 5)) {
					driver.findElement(ChkTalla).click();
					if (waitForElement(driver, IntSearchMarca, 5)) {
						driver.findElement(IntSearchMarca).sendKeys(Functions.getParameter("Informacion", "Busca1"));
						if (waitForElement(driver, ChkMarca, 5)) {
							driver.findElement(ChkMarca).click();
							LogReport("PUMA:", "Generación de Información.", 0, null);
							OrderData();
						} else {
							LogReport("PUMA:", "Generación Erronea", 2, null);
						}
					}else {
						LogReport("MARCA:", "Generación Erronea", 2, null);
					}
				}else {
					LogReport("Talla:", "Generación Erronea", 2, null);
				}
			}

		} catch (Exception e) {
			LogReport("PruebaSL", e.toString(), 3, null);
		}
	}
	public static void OrderData() throws Exception{
		if (waitForElement(driver,pagResult,5)) {
			ArrayList<String> obtainedList = new ArrayList<>(); 
			List<WebElement> elementList= driver.findElements(pagResult);
			for(WebElement we:elementList){
			   obtainedList.add(we.getText());
			}
			ArrayList<String> sortedList = new ArrayList<>();   
			for(String s:obtainedList){
			sortedList.add(s);
			}
			Collections.sort(sortedList);
			Assert.assertTrue(sortedList.equals(obtainedList));
			LogReport("Información PUMA:", "Generación de Información.", 0, null);
		}
	}
}