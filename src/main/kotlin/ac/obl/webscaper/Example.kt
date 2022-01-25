package ac.obl.webscaper

fun main() {
    val browser = Browser()

    val page = browser.open("https://igo.rs/#home")
    println(page.html())

    browser.close()
}
