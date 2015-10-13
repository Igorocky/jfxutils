package org.igye.jfxutils.action

trait ActionStateAware {
    def shortcutSet(newShortcut: Shortcut)
    def shortcutRemoved()
    def actionWasDisabled()
    def actionWasEnabled()
    def thisWasBoundToAction(action: Action)
    def thisWasUnboundFromAction(action: Action)
}
