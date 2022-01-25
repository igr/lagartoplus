package ac.obl.webscaper

fun main() {
    val session = Browser(withProxy = true).get()

    val page = session.open("https://igo.rs/#home")

    println(page.html())

    session.close()
}
