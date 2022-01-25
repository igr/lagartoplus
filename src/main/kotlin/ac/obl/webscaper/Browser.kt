package ac.obl.webscaper

import io.netty.handler.codec.http.EmptyHttpHeaders
import io.netty.handler.codec.http.HttpHeaders
import net.lightbody.bmp.BrowserMobProxy
import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.client.ClientUtil
import net.lightbody.bmp.proxy.CaptureType
import org.openqa.selenium.UnexpectedAlertBehaviour
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.CapabilityType
import java.time.Duration

class Browser() {
	private val driver: WebDriver
	private val chromeOptions: ChromeOptions
	private val proxy: BrowserMobProxy
	private val headers: HttpHeaders = EmptyHttpHeaders.INSTANCE

	init {
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/driver/chromedriver_mac64")

		proxy = BrowserMobProxyServer()
		proxy.setTrustAllServers(true)
		proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
		proxy.addRequestFilter { httpRequest, _, _ ->
			httpRequest.headers().add(headers)
			null
		}
		proxy.start()

		chromeOptions = ChromeOptions()
		chromeOptions.setHeadless(true)
		chromeOptions.addArguments("disable-infobars")

		chromeOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT)
		chromeOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)
		chromeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true)
		chromeOptions.setProxy(ClientUtil.createSeleniumProxy(proxy))

		driver = ChromeDriver(chromeOptions)
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30))
	}

	fun header(name: String, value: String): Browser {
		headers.add(name, value)
		return this
	}

	fun open(url: String): Page {
		//proxy.newHar("foo");

		driver.get(url)

		//proxy.har.writeTo(File("foo.har"))

		return Page(driver)
	}

	fun close() {
		driver.quit()
		proxy.stop()
	}
}
