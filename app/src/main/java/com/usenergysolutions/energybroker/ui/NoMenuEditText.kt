package com.usenergysolutions.energybroker.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText

// @Ref: https://stackoverflow.com/questions/27869983/edittext-disable-paste-replace-menu-pop-up-on-text-selection-handler-click-even

class NoMenuEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    EditText(context, attrs) {
    private val TAG: String = "NoMenuEditText"

    /** This is a replacement method for the base TextView class' method of the same name. This
     * method is used in hidden class android.widget.Editor to determine whether the PASTE/REPLACE popup
     * appears when triggered from the text insertion handle. Returning false forces this window
     * to never appear.
     * @return false
     */
    fun canPaste(): Boolean {
        return false
    }

    /** This is a replacement method for the base TextView class' method of the same name. This method
     * is used in hidden class android.widget.Editor to determine whether the PASTE/REPLACE popup
     * appears when triggered from the text insertion handle. Returning false forces this window
     * to never appear.
     * @return false
     */
    override fun isSuggestionsEnabled(): Boolean {
        return false
    }

    init {
        this.customSelectionActionModeCallback = ActionModeCallbackInterceptor()
        this.isLongClickable = false
    }

    inner class ActionModeCallbackInterceptor : ActionMode.Callback {
        private val TAG: String = "ActionModeCallbackInterceptor"

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {}
    }
}