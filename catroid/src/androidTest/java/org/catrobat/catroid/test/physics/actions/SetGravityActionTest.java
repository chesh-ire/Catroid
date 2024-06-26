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
package org.catrobat.catroid.test.physics.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.physics.PhysicsTestRule;
import org.catrobat.catroid.test.utils.Reflection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SetGravityActionTest {

	private static final float GRAVITY_X = 10.0f;
	private static final float GRAVITY_Y = 10.0f;

	@Rule
	public PhysicsTestRule rule = new PhysicsTestRule();

	private Sprite sprite;
	private PhysicsWorld physicsWorld;

	@Before
	public void setUp() {
		sprite = rule.sprite;
		physicsWorld = rule.physicsWorld;
	}

	@Test
	public void testNormalBehavior() throws Exception {
		float gravityX = GRAVITY_X;
		float gravityY = GRAVITY_Y;

		initGravityValues(gravityX, gravityY);
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals(gravityX, gravityVector.x);
		assertEquals(gravityY, gravityVector.y);
	}

	@Test
	public void testNegativeValue() throws Exception {
		float gravityX = 10.0f;
		float gravityY = -10.0f;

		initGravityValues(gravityX, gravityY);
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals(gravityX, gravityVector.x);
		assertEquals(gravityY, gravityVector.y);
	}

	@Test
	public void testZeroValue() throws Exception {
		float gravityX = 0.0f;
		float gravityY = 10.0f;

		initGravityValues(gravityX, gravityY);
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals(gravityX, gravityVector.x);
		assertEquals(gravityY, gravityVector.y);
	}

	private void initGravityValues(float gravityX, float gravityY) throws Exception {
		Action action = sprite.getActionFactory().createSetGravityAction(sprite,
				new SequenceAction(), new Formula(gravityX),
					new Formula(gravityY));
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals(PhysicsWorld.DEFAULT_GRAVITY.x, gravityVector.x);
		assertEquals(PhysicsWorld.DEFAULT_GRAVITY.y, gravityVector.y);

		action.act(1.0f);
	}

	@Test
	public void testBrickWithStringFormula() throws Exception {
		sprite.getActionFactory().createSetGravityAction(sprite,
			new SequenceAction(), new Formula(String.valueOf(GRAVITY_X)),
				new Formula(String.valueOf(GRAVITY_Y))).act(1.0f);
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals(GRAVITY_X, gravityVector.x);
		assertEquals(GRAVITY_Y, gravityVector.y);

		sprite.getActionFactory().createSetGravityAction(sprite, new SequenceAction(),
				new Formula("not a numerical string"),
				new Formula("not a numerical string")).act(1.0f);
		gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world")).getGravity();

		assertEquals(GRAVITY_X, gravityVector.x);
		assertEquals(GRAVITY_Y, gravityVector.y);
	}

	@Test
	public void testNullFormula() throws Exception {
		sprite.getActionFactory().createSetGravityAction(sprite, new SequenceAction(), null,
				null).act(1.0f);
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals(0f, gravityVector.x);
		assertEquals(0f, gravityVector.y);
	}

	@Test
	public void testNotANumberFormula() throws Exception {
		sprite.getActionFactory().createSetGravityAction(sprite, new SequenceAction(),
				new Formula(Double.NaN),
				new Formula(Double.NaN))
				.act(1.0f);
		Vector2 gravityVector = ((World) Reflection.getPrivateField(PhysicsWorld.class, physicsWorld, "world"))
				.getGravity();

		assertEquals(PhysicsWorld.DEFAULT_GRAVITY.x, gravityVector.x);
		assertEquals(PhysicsWorld.DEFAULT_GRAVITY.y, gravityVector.y);
	}
}
