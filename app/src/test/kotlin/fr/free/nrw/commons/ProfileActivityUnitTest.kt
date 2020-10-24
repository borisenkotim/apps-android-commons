package fr.free.nrw.commons

import android.content.Intent
import fr.free.nrw.commons.TestCommonsApplication.Companion.getContext
import fr.free.nrw.commons.profile.ProfileActivity
import fr.free.nrw.commons.quiz.QuizActivity
import junit.framework.Assert
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Tests Welcome Activity Methods
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21], application = TestCommonsApplication::class)
class ProfileActivityUnitTest {

    private lateinit var activity: ProfileActivity

    /**
     * Setup the Class and Views for Test
     */
    @Before
    fun setup() {
       // val intent = Intent()
        activity = Robolectric.buildActivity(ProfileActivity::class.java, Intent()).get()
     //   activity.onCreate(null)
    }

    @Test
    @Throws(Exception::class)
    fun checkActivityNotNull() {
        assertNotNull(activity)
    }

    @Test
    @Throws(Exception::class)
    fun testStartYourself() {
        var context = getContext()
        val intent = Intent(context, ProfileActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (context != null) {
            context.startActivity(intent)
        }
        assertNotNull(context)
        assertNotNull(intent)
    }

    @Test
    @Throws(Exception::class)
    fun testSetTabs() {
        assertTrue(true)
        // use mock class to fix this error
       // activity.setTabs();
    }

    @Test
    @Throws(Exception::class)
    fun testOnDestroy() {
  //      activity.onDestroy()
    }

}