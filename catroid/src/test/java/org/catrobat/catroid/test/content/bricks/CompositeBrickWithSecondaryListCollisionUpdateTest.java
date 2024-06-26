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

package org.catrobat.catroid.test.content.bricks;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.CompositeBrick;
import org.catrobat.catroid.content.bricks.ConcurrentFormulaHashMap;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.RaspiIfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.test.PowerMockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({CatroidApplication.class})
public class CompositeBrickWithSecondaryListCollisionUpdateTest {

	private FormulaBrick primaryFormulaBrick;
	private FormulaBrick secondaryFormulaBrick;
	private Sprite sprite;
	private static final String VARIABLE_NAME = "Test";
	private static final String DIFFERENT_VARIABLE_NAME = "Abcd";
	private static final String NEW_VARIABLE_NAME = "NewName";
	private static final String REPLACED_VARIABLE = "null(" + NEW_VARIABLE_NAME + ") ";
	private static final String NO_CHANGE_VARIABLE = "null(" + DIFFERENT_VARIABLE_NAME + ") ";

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{IfLogicBeginBrick.class.getSimpleName(), IfLogicBeginBrick.class},
				{PhiroIfLogicBeginBrick.class.getSimpleName(), PhiroIfLogicBeginBrick.class},
				{RaspiIfLogicBeginBrick.class.getSimpleName(), RaspiIfLogicBeginBrick.class},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Class<CompositeBrick> compositeBrickClass;

	@Before
	public void setUp() throws IllegalAccessException, InstantiationException {
		PowerMockUtil.mockStaticAppContextAndInitializeStaticSingletons();

		Project project = new Project();
		Scene scene = new Scene();
		sprite = new Sprite(VARIABLE_NAME);
		Script script = new WhenScript();
		CompositeBrick compositeBrick = compositeBrickClass.newInstance();
		primaryFormulaBrick = new SetXBrick();
		secondaryFormulaBrick = new SetXBrick();

		project.addScene(scene);
		scene.addSprite(sprite);
		sprite.addScript(script);
		script.addBrick(compositeBrick);
		compositeBrick.getNestedBricks().add(primaryFormulaBrick);
		compositeBrick.getSecondaryNestedBricks().add(secondaryFormulaBrick);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(scene);
	}

	@Test
	public void testRenameSprite() {
		Formula newFormula = new Formula(new FormulaElement(FormulaElement.ElementType.COLLISION_FORMULA,
				VARIABLE_NAME, null));

		ConcurrentFormulaHashMap primaryMap = primaryFormulaBrick.getFormulaMap();
		ConcurrentFormulaHashMap secondaryMap = primaryFormulaBrick.getFormulaMap();

		primaryMap.forEach((k, v) -> {
			primaryFormulaBrick.setFormulaWithBrickField(k, newFormula);
		});
		secondaryMap.forEach((k, v) -> {
			primaryFormulaBrick.setFormulaWithBrickField(k, newFormula);
		});

		sprite.rename(NEW_VARIABLE_NAME);

		primaryMap.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					REPLACED_VARIABLE);
		});
		secondaryMap.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					REPLACED_VARIABLE);
		});
	}

	@Test
	public void testRenameSpriteNoChange() {
		Formula newFormula = new Formula(new FormulaElement(FormulaElement.ElementType.COLLISION_FORMULA,
				DIFFERENT_VARIABLE_NAME, null));

		ConcurrentFormulaHashMap primaryMap = primaryFormulaBrick.getFormulaMap();
		ConcurrentFormulaHashMap secondaryMap = primaryFormulaBrick.getFormulaMap();

		primaryMap.forEach((k, v) -> {
			primaryFormulaBrick.setFormulaWithBrickField(k, newFormula);
		});
		secondaryMap.forEach((k, v) -> {
			primaryFormulaBrick.setFormulaWithBrickField(k, newFormula);
		});

		sprite.rename(NEW_VARIABLE_NAME);

		primaryMap.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					NO_CHANGE_VARIABLE);
		});
		secondaryMap.forEach((k, v) -> {
			assertEquals(v.getTrimmedFormulaString(CatroidApplication.getAppContext()),
					NO_CHANGE_VARIABLE);
		});
	}
}
