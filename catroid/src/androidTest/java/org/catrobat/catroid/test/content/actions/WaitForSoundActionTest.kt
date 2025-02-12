package org.catrobat.catroid.test.content.actions

import androidx.test.core.app.ApplicationProvider
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.TestCase.assertEquals
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.SoundFilePathWithSprite
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.WaitForSoundAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.SoundManager
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(JUnit4::class)
class WaitForSoundActionTest {
    private var action: WaitForSoundAction? = null
    private var pathSet: MutableSet<SoundFilePathWithSprite>? = null

    @Before
    fun setUp() {
        createProject(this.javaClass.getSimpleName())
    }

    @Test
    fun testWaitDurationSameAsSoundDuration() {
        createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE)
        action.act(0.1f)
        assertEquals(SOUND_DURATION, action.getDuration())
    }

    @Test
    fun testStopWaitWhenSameSoundStartsPlaying() {
        createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE)
        action.act(0.1f)
        assertEquals(action.getTime(), action.getDuration())
    }

    @Test
    fun testWaitWhenDifferentSoundsStartsPlaying() {
        createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE + "test")
        action.act(0.1f)
        assertNotEquals(action.getTime(), action.getDuration(), 0.0)
    }

    @Test
    fun testWaitWhenOtherSpriteStoppedSameSound() {
        createActionWithStoppedSoundFilePath(PATH_TO_SOUND_FILE)
        pathSet!!.clear()
        pathSet!!.add(
            SoundFilePathWithSprite(
                PATH_TO_SOUND_FILE, mock(
                    Sprite::class.java
                )
            )
        )
        action.act(0.1f)
        assertNotEquals(action.getTime(), action.getDuration(), 0.0)
    }

    private fun createProject(projectName: String) {
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        projectManager.setCurrentProject(project)
        projectManager.setCurrentSprite(project.getDefaultScene().getBackgroundSprite())
    }

    private fun createActionWithStoppedSoundFilePath(soundPath: String) {
        val soundManager: SoundManager = Mockito.mock(SoundManager::class.java)
        pathSet = java.util.HashSet<SoundFilePathWithSprite>()
        pathSet!!.add(
            SoundFilePathWithSprite(
                soundPath,
                project.getDefaultScene().getBackgroundSprite()
            )
        )
        `when`(soundManager.getRecentlyStoppedSoundfilePaths()).thenReturn(pathSet)
        `when`(soundManager.getDurationOfSoundFile(anyString())).thenReturn(SOUND_DURATION * 1000)
        action = (ActionFactory()).createWaitForSoundAction(
            project.getDefaultScene().getBackgroundSprite(), SequenceAction(),
            Formula(SOUND_DURATION),
            PATH_TO_SOUND_FILE
        ) as WaitForSoundAction?
        action.setSoundManager(soundManager)
    }

    companion object {
        private var projectManager: ProjectManager? = null
        private var project: Project? = null
        private const val SOUND_DURATION: Float = 2.0f
        private const val PATH_TO_SOUND_FILE: String = "soundFilePath"

        @BeforeClass
        fun setUpProjectManager() {
            project = Project(ApplicationProvider.getApplicationContext(), "projectName")
            projectManager = ProjectManager.getInstance()
        }
    }
}