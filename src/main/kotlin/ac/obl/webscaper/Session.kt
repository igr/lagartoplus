package ac.obl.webscaper

interface Session {
	fun open(url: String): Page
	fun close()
}