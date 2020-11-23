package fr.free.nrw.commons.media


import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import fr.free.nrw.commons.Media
import fr.free.nrw.commons.R
import fr.free.nrw.commons.TestCommonsApplication
import fr.free.nrw.commons.TestCommonsApplication.Companion.getContext
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21], application = TestCommonsApplication::class)
class MediaDetailFragmentHelperTest {

    private lateinit var media: MediaDetailFragmentHelper
    private lateinit var mediaInterface: MediaDetailFragmentHelperInterface

    @Mock
    private lateinit var context: Context

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        media = MediaDetailFragmentHelper();

        context = RuntimeEnvironment.application.applicationContext
    }

    @Test
    @Throws(Exception::class)
    fun checkClassNotNull() {
        Assert.assertNotNull(media)
    }

    @Test
    @Throws(Exception::class)
    fun testSanitise() {
        Assert.assertEquals(media.sanitise("testing|part1"), "testing")
        Assert.assertEquals(media.sanitise("testingpart1"), "testingpart1")
        Assert.assertEquals(media.sanitise(""), "")
    }

    @Test
    @Throws(Exception::class)
    fun testPrettyDiscussion() {
        Assert.assertEquals(media.prettyDiscussion("random"),"random")
    }
}