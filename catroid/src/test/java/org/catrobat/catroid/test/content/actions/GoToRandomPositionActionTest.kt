package org.catrobat.catroid.test.content.actions

import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.GoToRandomPositionAction
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GoToRandomPositionActionTest {

    @Rule
    @JvmField
    val exception: ExpectedException = ExpectedException.none()

    private lateinit var sprite: Sprite
    private lateinit var dummySprite: Sprite
    private lateinit var action: GoToRandomPositionAction

    @Before
    @Throws(Exception::class)
    fun setUp() {
        sprite = Sprite("testSprite")
        dummySprite = Sprite("dummySprite")
        action = sprite.getActionFactory().createGoToAction(
            sprite, dummySprite, BrickValues.GO_TO_RANDOM_POSITION
        ) as GoToRandomPositionAction
    }

    @Test
    fun testGoToOtherSpriteAction() {
        sprite.look.setXInUserInterfaceDimensionUnit(0f)
        sprite.look.setYInUserInterfaceDimensionUnit(0f)

        assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit(), 0.001f)
        assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit(), 0.001f)

        action.act(1f)

        assertEquals(action.randomXPosition, sprite.look.getXInUserInterfaceDimensionUnit(), 0.001f)
        assertEquals(action.randomYPosition, sprite.look.getYInUserInterfaceDimensionUnit(), 0.001f)
    }

    @Test
    fun testNullActor() {
        val factory = ActionFactory()
        val action: Action = factory.createGoToAction(null, dummySprite, BrickValues.GO_TO_RANDOM_POSITION)
        exception.expect(NullPointerException::class.java)
        action.act(1.0f)
    }
}
