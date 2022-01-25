package ac.obl.webscaper

import io.netty.handler.codec.http.EmptyHttpHeaders
import io.netty.handler.codec.http.HttpHeaders
import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.client.ClientUtil
import net.lightbody.bmp.proxy.CaptureType
import org.openqa.selenium.UnexpectedAlertBehaviour
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.CapabilityType
import java.time.Duration

class Browser(private val withProxy: Boolean = false) {
	private val headers: HttpHeaders = EmptyHttpHeaders.INSTANCE

	init {
		System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/driver/chromedriver_mac64")
	}

	private fun proxy(): BrowserMobProxyServer? {
		return (if (withProxy) {
			BrowserMobProxyServer()
		} else null )?.apply {
			this.setTrustAllServers(true)
			this.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
			this.addRequestFilter { httpRequest, _, _ ->
				httpRequest.headers().add(headers)
				null
			}
			this.start()
		}
	}

	fun get(): Session {
		val chromeOptions = ChromeOptions()
		chromeOptions.setHeadless(true)
		chromeOptions.addArguments("disable-infobars")

		chromeOptions.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT)
		chromeOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)
		chromeOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true)

		val proxy = proxy()?.apply {
			chromeOptions.setProxy(ClientUtil.createSeleniumProxy(this))
		}

		val driver = ChromeDriver(chromeOptions)
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30))

		return object : Session {
			override fun open(url: String): Page {
				//proxy.newHar("foo");
				driver.get(url)
				//proxy.har.writeTo(File("foo.har"))
				return Page(driver)
			}

			override fun close() {
				driver.quit()
				proxy?.stop()
			}
		}
	}
}
