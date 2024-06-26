/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.content.bricks

import android.content.Context
import android.view.View
import android.widget.TextView
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.actions.SetNextLookAction
import org.catrobat.catroid.formulaeditor.Formula

class CopyLookBrick constructor() : FormulaBrick() {
    constructor(value: String) : this(Formula(value))

    constructor(formula: Formula) : this() {
        setFormulaWithBrickField(Brick.BrickField.LOOK_COPY, formula)
    }

    init {
        addAllowedBrickField(Brick.BrickField.LOOK_COPY, R.id.brick_copy_look_edit_text)
    }

    override fun getViewResource(): Int = R.layout.brick_copy_look

    override fun getView(context: Context): View {
        super.getView(context)
        if (ProjectManager.getInstance().currentSprite.isBackgroundSprite(context)) {
            view.findViewById<TextView>(R.id.brick_copy_look_text_view)
                .setText(R.string.brick_copy_background)
        }
        return view
    }

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        val nextLookAction = sprite.actionFactory.createSetNextLookAction(sprite, sequence)
        val formula = getFormulaWithBrickField(Brick.BrickField.LOOK_COPY)
        sequence.addAction(sprite.actionFactory.createCopyLookAction(sprite, sequence, formula,
            nextLookAction as SetNextLookAction
        ))
        sequence.addAction(nextLookAction)
    }
}
