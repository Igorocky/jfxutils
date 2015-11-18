package org.igye.jfxutils.autocomplete

import org.apache.logging.log4j.Logger
import org.igye.commonutils.ThreadLoggable

class BasicAutocompleteQuery(queryProc: String => List[AutocompleteItem])(implicit log: Logger) extends AutocompleteQuery {
    private var thread: Option[ThreadLoggable] = None

    override def query(text: String, onCompleteHandler: (List[AutocompleteItem]) => Unit): Unit = {
        cancel()
        thread = Option(new ThreadLoggable() {
            override def run(): Unit = {
                onCompleteHandler(queryProc(text))
            }
        })
        thread.get.start()
    }

    override def cancel(): Unit = {
        thread.foreach(_.interrupt())
    }
}