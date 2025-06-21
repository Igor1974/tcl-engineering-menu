package com.example.tclengineeringmenu

import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AutoInputAccessibilityService : android.accessibilityservice.AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.source?.findAccessibilityNodeInfosByViewId("com.tcl.factory.view:id/password_input")?.forEach { node ->
            if (node.isEditable) {
                node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
                val arguments = Bundle().apply {
                    putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "1950")
                }
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)

                // Нажимаем Enter
                node.performAction(AccessibilityNodeInfo.ACTION_NEXT_AT_MOVEMENT_GRANULARITY)
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }

        // Попытка нажать OK
        event?.source?.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)?.parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    override fun onInterrupt() {}
}
