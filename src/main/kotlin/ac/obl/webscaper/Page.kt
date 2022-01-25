package ac.obl.webscaper

import org.openqa.selenium.WebDriver

class Page(private val driver: WebDriver) {
	fun html(): String {
		return driver.pageSource
	}

	fun use(consumer: (WebDriver) -> Unit) {
		consumer(driver)
	}
}
