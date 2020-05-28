package Components

import scala.io.StdIn

class SimpleListener extends StdinListener {

    override def getAndParseInput(): Any = {
        val text: String = StdIn.readLine
        text match {
            case s"login $name"  =>
                Login(name)
            case _ =>
                Signal(text)
        }
    }
}
