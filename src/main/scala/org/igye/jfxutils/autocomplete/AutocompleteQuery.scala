package org.igye.jfxutils.autocomplete

trait AutocompleteQuery {
    /**
      * Should start new thread and return immediately.
      */
    def execute(onCompleteHandler: List[AutocompleteItem] => Unit): Unit

    /**
      * Should not fail if query already has executed.
      */
    def cancel(): Unit
}
