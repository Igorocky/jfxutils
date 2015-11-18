package org.igye.jfxutils.autocomplete

trait AutocompleteQuery {
    /**
      * Should start new thread and return immediately.
      * @param text
      */
    def query(text: String, onCompleteHandler: List[AutocompleteItem] => Unit): Unit

    /**
      * Should not fail if query already has executed.
      */
    def cancel(): Unit
}
