package fr.free.nrw.commons

import android.content.Intent
import androidx.viewpager.widget.ViewPager
import fr.free.nrw.commons.TestCommonsApplication.Companion.getContext
import fr.free.nrw.commons.profile.ProfileActivity
import fr.free.nrw.commons.profile.ViewPagerAdapter
import junit.framework.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

/**
 * Tests Welcome Activity Methods
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21], application = TestCommonsApplication::class)
class ProfileActivityUnitTest {

    private lateinit var activity: ProfileActivity
    private lateinit var activityMock: ProfileActivity
    private lateinit var adapter: ViewPagerAdapter
    var viewPager: ViewPager? = null

    /**
     * Setup the Class and Views for Test
     */
    @Before
    fun setup() {
        activity = Robolectric.buildActivity(ProfileActivity::class.java, Intent()).get()
        adapter = ViewPagerAdapter(null)
        viewPager?.setAdapter(adapter)
        activityMock = mock(ProfileActivity::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun checkActivityNotNull() {
        assertNotNull(activity)
        assertNotNull(adapter)
    }

    /**
     * Tests the contents of the startYourself method
     */
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
        activityMock.setTabs(ArrayList(),ArrayList())
        activity.setTabs(ArrayList(),ArrayList())
    }

    @Test
    @Throws(Exception::class)
    fun testOnDestroy() {
        activityMock.onDestroy()
    }

}